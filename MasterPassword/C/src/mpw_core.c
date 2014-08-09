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

int mpw_core(char * const password, const size_t passLen, char const * const userName, 
	char const * const masterPassword, char const * const siteTypeString, char const * const siteName,
	const int siteCounter )
{
    //Verify that the input strings are not longer than MAXSTRLEN
    if ( strlen(userName) >= MAXSTRLEN || strlen(masterPassword) >= MAXSTRLEN || strlen(siteTypeString) >= MAXSTRLEN || strlen(siteName) >= MAXSTRLEN ) {
        sprintf( password, "At least one input string was longer than the cap of %d\n", MAXSTRLEN);
        return -1;
    }
    
    //*****************************************************
	// Calculate the master key salt.
	char *mpNameSpace = "com.lyndir.masterpassword";
	const uint32_t n_userNameLength = htonl((const u_long)strlen(userName));
	//Is this needed now?
    size_t masterKeySaltLength = strlen(mpNameSpace) + sizeof(n_userNameLength)+strlen(userName);

	char masterKeySalt[masterKeySaltLength];
    memset(masterKeySalt, 0, masterKeySaltLength);

	char *mKS = masterKeySalt;
	memcpy(mKS, mpNameSpace, strlen(mpNameSpace));
    mKS += strlen(mpNameSpace);
    
	memcpy(mKS, &n_userNameLength, sizeof(n_userNameLength));
    mKS += sizeof(n_userNameLength);
    
	memcpy(mKS, userName, strlen(userName));
    mKS += strlen(userName);
    
    //Check that we ended up where we expected. This is only to check for coding errors since the code
    //above quarantees this.
	if (mKS - masterKeySalt != masterKeySaltLength) {
		sprintf(password, "Error preparing the salt" );
		return -1;
	}
		
	trc("masterKeySalt ID: %s\n", IDForBuf(masterKeySalt, masterKeySaltLength));

    //*****************************************************
	// Calculate the master key.
	uint8_t masterKey[MP_dkLen];

	if (crypto_scrypt((const uint8_t *)masterPassword, strlen(masterPassword), (const uint8_t *)masterKeySalt, masterKeySaltLength, MP_N, MP_r, MP_p, masterKey, MP_dkLen) < 0)
    {
    
        //We make sure that at least we do not leave any copies of sensitive info in RAM.
        memset(masterKeySalt, 0, masterKeySaltLength);
        memset(masterKey,0,MP_dkLen);
        
		sprintf(password,"Could not generate master key: %d\n", errno);
		return -1;
	}
    
    //We make sure that at least we do not leave any copies of sensitive info in RAM.
    memset(masterKeySalt, 0, masterKeySaltLength);
    
	trc("masterPassword Hex: %s\n", Hex(masterPassword, strlen(masterPassword)));
	trc("masterPassword ID: %s\n", IDForBuf(masterPassword, strlen(masterPassword)));
	trc("masterKey ID: %s\n", IDForBuf(masterKey, MP_dkLen));

    //*****************************************************
	// Calculate the site seed.
	const uint32_t n_siteNameLength = htonl((const u_long)strlen(siteName));
	const uint32_t n_siteCounter = htonl(siteCounter);
	size_t sitePasswordInfoLength = strlen(mpNameSpace) + sizeof(n_siteNameLength)+strlen(siteName) + sizeof(n_siteCounter);

	char sitePasswordInfo[sitePasswordInfoLength];
    memset( sitePasswordInfo, 0, sitePasswordInfoLength );
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
	if (sPI - sitePasswordInfo != sitePasswordInfoLength) {
        //We make sure we do not leave sensitive info in RAM.
        memset(masterKey, 0, MP_dkLen);
        memset(sitePasswordInfo, 0, sitePasswordInfoLength);

		fprintf(stderr, "Coding error when building the sitePasswordInfo seed.\n" );
		return -1;
    }
    
	trc("seed from: hmac-sha256(masterKey, 'com.lyndir.masterpassword' | %s | %s | %s)\n", Hex(&n_siteNameLength, sizeof(n_siteNameLength)), siteName, Hex(&n_siteCounter, sizeof(n_siteCounter)));
	trc("sitePasswordInfo ID: %s\n", IDForBuf(sitePasswordInfo, sitePasswordInfoLength));

	uint8_t sitePasswordSeed[MAXPASSLEN];
	HMAC_SHA256_Buf(masterKey, MP_dkLen, sitePasswordInfo, sitePasswordInfoLength, sitePasswordSeed);

    //We make sure we do not leave sensitive info in RAM.
	memset(masterKey, 0, MP_dkLen);
	memset(sitePasswordInfo, 0, sitePasswordInfoLength);
	   
	trc("sitePasswordSeed ID: %s\n", IDForBuf(sitePasswordSeed, 32));

    //*****************************************************
    //Convert the sitePasswordSeed to a password using a cipher.
    
	// Determine the cipher
	MPElementType siteType = TypeWithName(siteTypeString);
	const char *cipher = CipherForType(siteType, sitePasswordSeed[0]);
	trc("type %s, cipher: %s\n", siteTypeString, cipher);
	if (strlen(cipher) >= MAXPASSLEN) {
        //We make sure we do not leave sensitive info in the RAM.
        memset( sitePasswordSeed, 0, MAXPASSLEN );
        
		fprintf(stderr, "Cipher template longer than 32 characters returned. Not supported.\n" );
        return -1;
    }
		

	// Encode the password from the seed using the cipher.
    if ( strlen(cipher) > passLen -1 ) {
        //We make sure we do not leave sensitive info in the RAM.
        memset(sitePasswordSeed, 0, sizeof(sitePasswordSeed));

        fprintf(stderr, "Cipher length was longer than requested output password length: %d vs %d\n",
                (int)strlen(cipher), (int)passLen );
        return -1;
    }
    
    //Set the entire password memory to 0 to ensure null termination.
    memset( password, 0, passLen*sizeof(password[0]) );
	for (int c = 0; c < strlen(cipher); ++c) {
        password[c] = CharacterFromClass(cipher[c], sitePasswordSeed[c + 1]);
        trc("class %c, character: %c\n", cipher[c], password[c]);
	}
	
	//We make sure we do not leave sensitive info in the RAM.
	memset(sitePasswordSeed, 0, sizeof(sitePasswordSeed));
	
	return 0;
}