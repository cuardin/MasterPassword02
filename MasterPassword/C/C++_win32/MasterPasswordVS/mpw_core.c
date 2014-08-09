#include <stdio.h>
#include <stdint.h>
#include "types.h"
#if defined(WIN32)
#include <WinSock2.h>
#else
#include <netinet/in.h>
#endif

#include "sha256.h"
#include "crypto_scrypt.h"


#define MP_N                32768
#define MP_r                8
#define MP_p                2
#define MP_dkLen            64
#define MP_hash             PearlHashSHA256

int mpw_core(char * const password, const size_t passLen, char const * const userName, 
	char const * const masterPassword, char const * const siteTypeString, char const * const siteName,
	const int siteCounter )
{
	// Calculate the master key salt.
	char *mpNameSpace = "com.lyndir.masterpassword";
	const uint32_t n_userNameLength = htonl((const u_long)strlen(userName));
	size_t masterKeySaltLength = strlen(mpNameSpace) + sizeof(n_userNameLength)+strlen(userName);
	char *masterKeySalt = (char*)malloc(masterKeySaltLength);
	if (!masterKeySalt) {		
		sprintf_s( password, passLen, "Could not allocate master key salt: %d\n", errno);
		return -1;
	}

	char *mKS = masterKeySalt;
	memcpy(mKS, mpNameSpace, strlen(mpNameSpace)); mKS += strlen(mpNameSpace);
	memcpy(mKS, &n_userNameLength, sizeof(n_userNameLength)); mKS += sizeof(n_userNameLength);
	memcpy(mKS, userName, strlen(userName)); mKS += strlen(userName);
	if (mKS - masterKeySalt != masterKeySaltLength) {
		sprintf_s(password, passLen, "Error preparing the salt" );
		return -1;
	}
		
	trc("masterKeySalt ID: %s\n", IDForBuf(masterKeySalt, masterKeySaltLength));

	// Calculate the master key.
	uint8_t *masterKey = (uint8_t*)malloc(MP_dkLen);
	if (!masterKey) {
		sprintf_s(password, passLen, "Could not allocate master key: %d\n", errno);
		return -1;
	}

	if (crypto_scrypt((const uint8_t *)masterPassword, strlen(masterPassword), (const uint8_t *)masterKeySalt, masterKeySaltLength, MP_N, MP_r, MP_p, masterKey, MP_dkLen) < 0) {
		sprintf_s(password,passLen,"Could not generate master key: %d\n", errno);
		return -1;
	}
	memset(masterKeySalt, 0, masterKeySaltLength);
	free(masterKeySalt);
	trc("masterPassword Hex: %s\n", Hex(masterPassword, strlen(masterPassword)));
	trc("masterPassword ID: %s\n", IDForBuf(masterPassword, strlen(masterPassword)));
	trc("masterKey ID: %s\n", IDForBuf(masterKey, MP_dkLen));

	// Calculate the site seed.
	const uint32_t n_siteNameLength = htonl((const u_long)strlen(siteName));
	const uint32_t n_siteCounter = htonl(siteCounter);
	size_t sitePasswordInfoLength = strlen(mpNameSpace) + sizeof(n_siteNameLength)+strlen(siteName) + sizeof(n_siteCounter);
	char *sitePasswordInfo = (char*)malloc(sitePasswordInfoLength);
	if (!sitePasswordInfo) {
		fprintf(stderr, "Could not allocate site seed: %d\n", errno);
		return 1;
	}

	char *sPI = sitePasswordInfo;
	memcpy(sPI, mpNameSpace, strlen(mpNameSpace)); sPI += strlen(mpNameSpace);
	memcpy(sPI, &n_siteNameLength, sizeof(n_siteNameLength)); sPI += sizeof(n_siteNameLength);
	memcpy(sPI, siteName, strlen(siteName)); sPI += strlen(siteName);
	memcpy(sPI, &n_siteCounter, sizeof(n_siteCounter)); sPI += sizeof(n_siteCounter);
	if (sPI - sitePasswordInfo != sitePasswordInfoLength)
		abort();
	trc("seed from: hmac-sha256(masterKey, 'com.lyndir.masterpassword' | %s | %s | %s)\n", Hex(&n_siteNameLength, sizeof(n_siteNameLength)), siteName, Hex(&n_siteCounter, sizeof(n_siteCounter)));
	trc("sitePasswordInfo ID: %s\n", IDForBuf(sitePasswordInfo, sitePasswordInfoLength));

	uint8_t sitePasswordSeed[32];
	HMAC_SHA256_Buf(masterKey, MP_dkLen, sitePasswordInfo, sitePasswordInfoLength, sitePasswordSeed);
	memset(masterKey, 0, MP_dkLen);
	memset(sitePasswordInfo, 0, sitePasswordInfoLength);
	free(masterKey);
	free(sitePasswordInfo);
	trc("sitePasswordSeed ID: %s\n", IDForBuf(sitePasswordSeed, 32));

	// Determine the cipher.
	MPElementType siteType = TypeWithName(siteTypeString);
	const char *cipher = CipherForType(siteType, sitePasswordSeed[0]);
	trc("type %s, cipher: %s\n", siteTypeString, cipher);
	if (strlen(cipher) > 32)
		abort();

	// Encode the password from the seed using the cipher.	
	for (int c = 0; c < strlen(cipher) && c < passLen; ++c) {
		password[c] = CharacterFromClass(cipher[c], sitePasswordSeed[c + 1]);
		trc("class %c, character: %c\n", cipher[c], sitePassword[c]);
	}
	
	//Setting this to 0 is false security since the master password is lying around in memory anyways.
	//memset(sitePasswordSeed, 0, sizeof(sitePasswordSeed));
	
	return 0;
}