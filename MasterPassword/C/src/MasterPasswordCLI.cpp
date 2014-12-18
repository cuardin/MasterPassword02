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

#define MP_env_fullname     "MP_FULLNAME"
#define MP_env_sitetype     "MP_SITETYPE"
#define MP_env_sitecounter  "MP_SITECOUNTER"

#define PASS_LENGTH 64
#define KEY_ID_LENGTH 128



void usage() {
    fprintf(stderr, "Usage: mpw [-u name] [-t type] [-c counter] site\n\n");
    fprintf(stderr, "    -u name      Specify the full name of the user.\n"
            "                 Defaults to %s in env.\n\n", MP_env_fullname);
    fprintf(stderr, "    -t type      Specify the password's template.\n"
            "                 Defaults to %s in env or 'long' for password, 'name' for login.\n"
            "                     x, max, maximum | 20 characters, contains symbols.\n"
            "                     l, long         | Copy-friendly, 14 characters, contains symbols.\n"
            "                     m, med, medium  | Copy-friendly, 8 characters, contains symbols.\n"
            "                     b, basic        | 8 characters, no symbols.\n"
            "                     s, short        | Copy-friendly, 4 characters, no symbols.\n"
            "                     i, pin          | 4 numbers.\n"
            "                     n, name         | 9 letter name.\n"
            "                     p, phrase       | 20 character sentence.\n\n", MP_env_sitetype);
    fprintf(stderr, "    -c counter   The value of the counter.\n"
            "                 Defaults to %s in env or '1'.\n\n", MP_env_sitecounter);
    fprintf(stderr, "    -v variant   The kind of content to generate.\n"
            "                 Defaults to 'password'.\n"
            "                     p, password | The password to log in with.\n"
            "                     l, login    | The username to log in as.\n"
            "                     a, answer   | The answer to a security question.\n\n");
    fprintf(stderr, "    -C context   A variant-specific context.\n"
            "                 Defaults to empty.\n"
            "                  -v p, password | Doesn't currently use a context.\n"
            "                  -v l, login    | Doesn't currently use a context.\n"
            "                  -v a, answer   | Empty for a universal site answer or\n"
            "                                 | the most significant word(s) of the question.\n\n");
    fprintf(stderr, "    ENVIRONMENT\n\n"
            "        MP_FULLNAME    | The full name of the user.\n"
            "        MP_SITETYPE    | The default password template.\n"
            "        MP_SITECOUNTER | The default counter value.\n\n");
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
    char* pointer = getenv(MP_env_fullname);
    std::string fullName = pointer?pointer:"";
    std::string masterPassword;
    std::string siteName;
    std::string siteVariantString = "password";
    std::string siteContextString;
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
                fullName = o.value;
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
            case 'v':
                siteVariantString = o.value;
                break;
            case 'C':
                siteContextString = o.value;
                break;
            /*case '?':
                switch (optopt) {
                    case 'u':
                        fprintf(stderr, "Missing full name to option: -%c\n", optopt);
                        break;
                    case 't':
                        fprintf(stderr, "Missing type name to option: -%c\n", optopt);
                        break;
                    case 'c':
                        fprintf(stderr, "Missing counter value to option: -%c\n", optopt);
                        break;
                    default:
                        fprintf(stderr, "Unknown option: -%c\n", optopt);
                }
                return 1;*/
            default:
                std::cerr << "Unknown option found: " << o.opt << std::endl;
                return 1;
        }
    }
    
    if (fullName.empty()) {
        std::cout << "Full Name: ";
        std::cin >> fullName;
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

    MPSiteVariant siteVariant = MPSiteVariantPassword;
    MPSiteType siteType;
    if (siteVariantString.size() == 0 )
        siteVariant = VariantWithName( siteVariantString.c_str() );
    if (siteVariant == MPSiteVariantLogin)
        siteType = MPSiteTypeGeneratedName;
    if (siteVariant == MPSiteVariantAnswer)
        siteType = MPSiteTypeGeneratedPhrase;
    if (siteTypeString.size() == 0)
        siteType = TypeWithName( siteTypeString.c_str() );
    
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
        std::string filefullName = line.substr(0, splitIdx);
        std::string fileMasterPass = line.substr(splitIdx + 1, line.size());
        if (filefullName == fullName) {
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
    std::cout << fullName << "'s password for " << siteName << std::endl;
    std::cout << Identicon( fullName.c_str(), masterPassword.c_str() ) << std::endl;
    
    std::cout << "Generating site password...." << std::endl;
    
    char password[PASS_LENGTH];
    memset(password, 0, PASS_LENGTH);
    char keyID[KEY_ID_LENGTH];
    memset(keyID, 0, KEY_ID_LENGTH);

    
    int status = mpw_core(ScopeForVariant(MPSiteVariantPassword), password, PASS_LENGTH,
                          fullName.c_str(), masterPassword.c_str(), siteTypeString.c_str(),
                          siteName.c_str(), siteCounter, siteContextString.c_str(), keyID, KEY_ID_LENGTH);
    if (status != 0) {
        std::cerr << "Error generating password." << std::endl;        
		return status;
    }
    
    // Output the password.
    std::cout << "Site password: " << password << std::endl;    
	std::cout << "Press enter to exit." << std::endl;
	std::cin.get();
	std::cin.get();
    return 0;
}

