#include <iostream>
#include <string>
#include <fstream>
#if defined(WIN32)
typedef long ssize_t;
#include <WinSock2.h>
#define PSEP "\\"
#else
#define PSEP "/"
#include <sys/ioctl.h>
#include <unistd.h>
#include <pwd.h>
#include <netinet/in.h>
#include <termios.h>
#if defined(__linux__)
#include <linux/fs.h>
#elif defined(__CYGWIN__)
#include <cygwin/fs.h>
#else
#include <sys/disk.h>
#endif
#endif
#include <stdlib.h>
#include <errno.h>



#include "getopt.h"

extern "C" {
#include "mpw_core.h"
#include "types.h"
}



#define MP_env_username     "MP_USERNAME"
#define MP_env_sitetype     "MP_SITETYPE"
#define MP_env_sitecounter  "MP_SITECOUNTER"

#define PASS_LENGTH 64



void usage() {
	std::cerr <<
		"Usage: mpw [-u name] [-t type] [-c counter] site\n\n"
		"    -u name      Specify the full name of the user.\n"
		"                 Defaults to " << MP_env_username << " in env.\n\n"		
		"    -s sitename  Specify the site name.\n"
		"    -t type      Specify the password's template.\n"
		"                 Defaults to " << MP_env_sitetype << " in env or 'long'.\n"
		"                     x, max, maximum | 20 characters, contains symbols.\n"
		"                     l, long         | Copy-friendly, 14 characters, contains symbols.\n"
		"                     m, med, medium  | Copy-friendly, 8 characters, contains symbols.\n"
		"                     b, basic        | 8 characters, no symbols.\n"
		"                     s, short        | Copy-friendly, 4 characters, no symbols.\n"
		"                     p, pin          | 4 numbers.\n\n"
		"    -c counter   The value of the counter.\n"
		"                 Defaults to " << MP_env_sitecounter << " in env or '1'.\n\n";
	exit(0);
}

std::string homedir(const std::string filename) {
	std::string homedir;

#if defined(WIN32) | defined(__CYGWIN__)
	char* buffer = getenv("USERPROFILE");
	homedir = buffer ? buffer : "";
	if (homedir.empty()) {
		buffer = getenv("HOMEDRIVE");
		const std::string homeDrive = buffer?buffer:"";
		buffer = getenv("HOMEPATH");
		const std::string homePath = buffer?buffer:"";
		if ( !homeDrive.empty() || !homeDrive.empty() )
			homedir = homeDrive + PSEP + homePath;
	}
#else
	struct passwd* passwd = getpwuid(getuid());
	if (passwd)
		homedir = passwd->pw_dir;
	if (!homedir.empty()) {
        char* buffer = getenv("HOME");
		homedir = buffer?buffer:"";
    }
#endif

	if (homedir.empty()) {
		// homedir = getcwd(NULL, 0);
		std::cerr << "Current working directory not read. FIXME" << std::endl;
	}

	std::string homefile;
	homefile = homedir + PSEP + filename;
	return homefile;
}

//Downloaded this off of the internetz. Might be better to try to port the readpass from Colin.
void SetStdinEcho(bool enable = true)
{
#ifdef WIN32
	HANDLE hStdin = GetStdHandle(STD_INPUT_HANDLE);
	DWORD mode;
	GetConsoleMode(hStdin, &mode);

	if (!enable)
		mode &= ~ENABLE_ECHO_INPUT;
	else
		mode |= ENABLE_ECHO_INPUT;

	SetConsoleMode(hStdin, mode);

#else
	struct termios tty;
	tcgetattr(STDIN_FILENO, &tty);
	if (!enable)
		tty.c_lflag &= ~(ICANON |ECHO);
	else
		tty.c_lflag |= (ICANON | ECHO);

	(void)tcsetattr(STDIN_FILENO, TCSANOW, &tty);
#endif
}

int main(int argc, char *const argv[]) {
	
	// Read the environment.
	char* pointer = getenv(MP_env_username);	
	std::string userName = pointer?pointer:"";
	std::string masterPassword;
	std::string siteName;		
	pointer = getenv(MP_env_sitetype);
	std::string siteTypeString = pointer ? pointer : "";
	uint32_t siteCounter = 1;
	pointer = getenv(MP_env_sitecounter);
	std::string siteCounterString = pointer ? pointer : ""; 

	//Set the default to long.
	if (siteTypeString.empty()) {
		siteTypeString = "long";
	}

	ParsedInput input = parseInput(argc, argv);
	for (Option o : input.options) {
		switch (o.opt) {
		case 'h':
			usage();
			break;
		case 'u':
			userName = o.value;
			break;
		case 't':
			siteTypeString = o.value;
			break;
		case 's':
			siteName = o.value;
			break;
		case 'c':
			siteCounterString = o.value;
			break;
		default:
			std::cerr << "Unknown option found: " << o.opt << std::endl;
			abort();
		}
	}

	if (userName.empty()) {
		std::cout << "Username: ";
		std::cin >> userName;
	}
	
	if (siteName.empty()) {
		std::cout << "SiteName: ";
		std::cin >> siteName;
	}
    
    bool validSiteCounter = false;
    try {
        siteCounter = std::stoi(siteCounterString);
        validSiteCounter = true;
    } catch ( std::exception& e ) {
        validSiteCounter = false;
    }

    while( !validSiteCounter ) {
		std::cout << "SiteCounter (Must be positive integer): ";
		std::cin >> siteCounterString;
        std::cin.clear();
        std::cin.ignore(10000,'\n');
        try {
            int siteCounterTemp = std::stoi(siteCounterString);
            siteCounter = siteCounterTemp;
            if ( siteCounterTemp >= 0 ) {
                validSiteCounter = true;
            }
        } catch ( std::exception& e ) {
            validSiteCounter = false;
        }
	}
	
	// Read the master password.
	std::string mpwConfigPath = homedir(".mpw");
	if (mpwConfigPath.empty()) {
		std::cerr << "Couldn't resolve path for configuration file: " << errno << std::endl;
		return 1;
	}
	trc("mpwConfigPath: %s\n", mpwConfigPath.c_str());
	std::ifstream fin(mpwConfigPath);
	while(fin.good()) {
		const int buffer_size = 256;
		char * const buffer = new char[buffer_size];	
		fin.getline(buffer, buffer_size);

		std::string line(buffer);
		//Split at :
		size_t splitIdx = line.find_first_of(':');
		std::string fileUserName = line.substr(0, splitIdx);
		std::string fileMasterPass = line.substr(splitIdx + 1, line.size());
		if (fileUserName == userName) {
			masterPassword = fileMasterPass;
			break;
		}
	}

	if (masterPassword.empty()) {
		std::cout << "MasterPassword (Will not echo): ";
		SetStdinEcho(false);
		std::cin >> masterPassword;
		SetStdinEcho(true);
		std::cout << std::endl;
	}
	trc("masterPassword: %s\n", masterPassword.c_str());

	std::cout << "Generating site password...." << std::endl;

	char password[PASS_LENGTH];
	memset(password, 0, PASS_LENGTH);

	int status = mpw_core("com.lyndir.masterpassword", password, PASS_LENGTH, userName.c_str(),
		masterPassword.c_str(), siteTypeString.c_str(), siteName.c_str(), siteCounter, "", 0, 0);
	if (status != 0) {
		std::cerr << "Error generating password: " << std::endl;
		std::cout << password << std::endl;
	}

	// Output the password.
	std::cout << "Site password: " << password << std::endl;

	
	return 0;
}

