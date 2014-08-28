#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <errno.h>
#include <string.h>
#include "types.h"
#if defined(WIN32)
#include <WinSock2.h>
#else
#include <netinet/in.h>
#endif

#include "sha256.h"
#include "crypto_scrypt.h"
#include "mpw_core.h"
//#include "utils.h"

#define MAXSTRLEN           1024 //The maximum length of an input string we support.
#define MAXPASSLEN          32 //The maximum length of password that we can generate for any cipher. (Inc null term)
#define MP_N                32768
#define MP_r                8
#define MP_p                2
#define MP_dkLen            64
#define MP_hash             PearlHashSHA256

//This function assumes that the strings passed are properly null terminated. It is the responsibility of the calling
//function to ensure that only sane inputs are returned. A sanity check on the length of strings is done, however.

//Additionally, since char is used, only the ANSI character set is supported. It is the job of the calling function
//to ensure that ANSI character encoding is used.

//This function make sure that any memory allocated internally and used to store sensitive info is reset to 0 before
//the function returns. It is the job of the calling function to do the same with the input and output arrays, if this
//sort of paranoia is asked for.

void cleanup(uint8_t * const masterKey, uint8_t * const sitePasswordSeed)
{
    memset(masterKey,0,MP_dkLen);
    memset(sitePasswordSeed,0,MAXPASSLEN);
}


int mpw_core(char * const password, const size_t passLen, char const * const userName, 
	char const * const masterPassword, char const * const siteTypeString, char const * const siteName,
	const int siteCounter )
{
    //**************************************************
    //Verify that the input strings are not longer than MAXSTRLEN
    if ( strlen(userName) >= MAXSTRLEN || strlen(masterPassword) >= MAXSTRLEN || strlen(siteTypeString) >= MAXSTRLEN || strlen(siteName) >= MAXSTRLEN ) {
        sprintf( password, "At least one input string was longer than the cap of %d\n", MAXSTRLEN);
        return -1;
    }

    //**************************************************
    //Allocate all of the memory we neeed
    
    //A string that makes salts from this program unique from other programs.
    char *mpNameSpace = "com.lyndir.masterpassword"; //Must be a few chars shorter than MAXSTRLEN
    
    //The main salt based on the user name.
    char masterKeySalt[2*MAXSTRLEN]; memset(masterKeySalt, 0, 2 * MAXSTRLEN);
    unsigned long masterKeySaltLength = 0;
    
    //The master key itself.
    uint8_t masterKey[MP_dkLen]; memset( masterKey, 0, MP_dkLen);

    //The satlt for the specific site.
    uint8_t sitePasswordInfo[2*MAXSTRLEN];	memset(sitePasswordInfo, 0, 2 * MAXSTRLEN );
    size_t sitePasswordInfoLength = 0;
    
    //The random string of bits that will be used to form the password.
    uint8_t sitePasswordSeed[MAXPASSLEN]; memset( masterKey, 0, MAXPASSLEN);

    //**************************************************
    //Compute the main program salt based on the username.
    if ( 0 != mpw_core_calculate_master_key_salt(mpNameSpace, userName, masterKeySalt, &masterKeySaltLength ) ) {
        cleanup(masterKey, sitePasswordSeed);
		sprintf(password, "Error preparing the salt" );
        return -1;
    }

    //*****************************************************
	// Calculate the master key.
	if (0 != mpw_core_calculate_master_key(masterPassword, masterKeySalt, masterKeySaltLength, masterKey) ) {
		cleanup(masterKey, sitePasswordSeed);
		sprintf(password, "Error preparing the salt");
		return -1;
	}
	
	//*****************************************************
	// Calculate the seed for the site.
    if ( 0 != mpw_core_calculate_site_seed(sitePasswordInfo, &sitePasswordInfoLength, mpNameSpace, siteName, siteCounter )) {
        cleanup(masterKey, sitePasswordSeed);
		fprintf(stderr, "Coding error when building the sitePasswordInfo seed.\n" );
    }
    
    //*****************************************************
	// Hash the secret key and the site seed to get a string of bits we can use to generate a password
    mpw_core_compute_hmac(masterKey, sitePasswordInfo, sitePasswordInfoLength, sitePasswordSeed );
    

    //*****************************************************
    //Convert the sitePasswordSeed to a password using a cipher.
    if ( 0 != mpw_core_convert_to_password(siteTypeString, sitePasswordSeed,
                                       passLen, password ) ) {
        cleanup(masterKey, sitePasswordSeed);
        return -1;
    }

	
	//We make sure we do not leave sensitive info in the RAM.
    cleanup(masterKey, sitePasswordSeed);
    
	return 0;
    
    
}

int mpw_core_calculate_master_key_salt(char const * const mpNameSpace, char const * const userName,
                                       char * const masterKeySalt, size_t * const masterKeySaltLength )
{
    //*****************************************************
	// Calculate the master key salt.
	const uint32_t n_userNameLength = htonl((const u_long)strlen(userName));
	//Is this needed now?
    *masterKeySaltLength = strlen(mpNameSpace) + sizeof(n_userNameLength)+strlen(userName);
    
    
	char *mKS = masterKeySalt;
	memcpy(mKS, mpNameSpace, strlen(mpNameSpace));
    mKS += strlen(mpNameSpace);
    
	memcpy(mKS, &n_userNameLength, sizeof(n_userNameLength));
    mKS += sizeof(n_userNameLength);
    
	memcpy(mKS, userName, strlen(userName));
    mKS += strlen(userName);
    
    //Check that we ended up where we expected. This is only to check for coding errors since the code
    //above quarantees this.
	if (mKS - masterKeySalt != *masterKeySaltLength) {
		return -1;
	}
    
	trc("masterKeySalt Hex %s\n", Hex(masterKeySalt, *masterKeySaltLength));
    
    return 0;
}

int mpw_core_calculate_master_key(char const * const masterPassword, char const * const masterKeySalt,
	size_t masterKeySaltLength, uint8_t * const masterKey)
{
	if (crypto_scrypt((const uint8_t *)masterPassword, strlen(masterPassword),
		(const uint8_t *)masterKeySalt, masterKeySaltLength, MP_N, MP_r, MP_p, masterKey, MP_dkLen) < 0)
	{
		return -1;
	}
	trc("masterPassword Hex: %s\n", Hex(masterPassword, strlen(masterPassword)));	
	trc("masterKeySalt Hex: %s\n", Hex(masterKeySalt, masterKeySaltLength));
	trc("masterKey Hex: %s\n", Hex(masterKey, MP_dkLen));

	return 0;
}

int mpw_core_calculate_site_seed( char * const sitePasswordInfo, size_t * const sitePasswordInfoLength, char const * const mpNameSpace, char const * const siteName, int siteCounter )
{
    //*****************************************************
	// Calculate the site seed.
	const uint32_t n_siteNameLength = htonl((const u_long)strlen(siteName));
	const uint32_t n_siteCounter = htonl(siteCounter);
	*sitePasswordInfoLength = strlen(mpNameSpace) + sizeof(n_siteNameLength)+strlen(siteName) + sizeof(n_siteCounter);
    
    char* sPI = sitePasswordInfo;
    
	memcpy(sPI, mpNameSpace, strlen(mpNameSpace));
    sPI += strlen(mpNameSpace);
	
    memcpy(sPI, &n_siteNameLength, sizeof(n_siteNameLength));
    sPI += sizeof(n_siteNameLength);
	
    memcpy(sPI, siteName, strlen(siteName));
    sPI += strlen(siteName);
	
    memcpy(sPI, &n_siteCounter, sizeof(n_siteCounter));
    sPI += sizeof(n_siteCounter);
    
    //This is just to check for coding errors. The above code quarantees that this will allways pass.
	if (sPI - sitePasswordInfo != *sitePasswordInfoLength) {
		return -1;
    }
	    	
	trc("siteName Hex: %s\n", Hex(siteName, strlen(siteName)));
	trc("sitePasswordInfo Hex: %s\n", Hex(sitePasswordInfo, *sitePasswordInfoLength));
    
    return 0;
}

void mpw_core_compute_hmac(uint8_t const * const masterKey, uint8_t const * const sitePasswordInfo,
                           const size_t sitePasswordInfoLength, uint8_t * const sitePasswordSeed )
{
	trc("*masterKey Hex %s,\n", Hex(masterKey, MP_dkLen));
	trc("*sitePasswordInfo Hex %s,\n", Hex(sitePasswordInfo, sitePasswordInfoLength));
	HMAC_SHA256_Buf(masterKey, MP_dkLen, sitePasswordInfo, sitePasswordInfoLength, sitePasswordSeed);
    trc("-sitePasswordSeed Hex: %s\n", Hex(sitePasswordSeed, 32));
}

int mpw_core_convert_to_password(char const * const siteTypeString, uint8_t const * const sitePasswordSeed,
                                 const size_t passLen, char * const password )
{
    // Determine the cipher
	MPElementType siteType = TypeWithName(siteTypeString);
	const char *cipher = CipherForType(siteType, sitePasswordSeed[0]);
	trc("type %s, cipher: %s\n", siteTypeString, cipher);
	if (strlen(cipher) >= MAXPASSLEN) {
		fprintf(stderr, "Cipher template longer than 32 characters returned. Not supported.\n" );
        return -1;
    }
    
    //Check that our output buffer is large enough.
    if ( strlen(cipher) > passLen -1 ) {
        fprintf(stderr, "Cipher length was longer than requested output password length: %d vs %d\n",
                (int)strlen(cipher), (int)passLen );
        return -1;
    }
    
    //Encode the password using out cipher.
    memset( password, 0, passLen*sizeof(password[0]) ); //Set the entire password memory to 0 to ensure null termination.
	for (unsigned int c = 0; c < strlen(cipher); ++c) {
        password[c] = CharacterFromClass(cipher[c], sitePasswordSeed[c + 1]);
        //trc("class %c, character: %c\n", cipher[c], password[c]);
	}
    
    return 0;
}
