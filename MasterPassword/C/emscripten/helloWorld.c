#include <stdio.h>
#include <string.h>

#include "testutils.h"
#include "sha256.h"
#include "crypto_scrypt.h"

#define MAXSTRLEN           1024 //The maximum length of an input string we support.
#define MAXPASSLEN          32 //The maximum length of password that we can generate for any cipher. (Inc null term)
#define MP_N                32768
#define MP_r                8
#define MP_p                2
#define MP_dkLen            64


void test01()
{
    printf( "\n* Test 01\n" );
    //This test case is an official one from an RFC.
	uint8_t key[64];
	convertFromHex("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b",key, 20);
	uint8_t data[64];
	convertFromHex("4869205468657265", data, 8);

	uint8_t digest[32];
	
	HMAC_SHA256_Buf(key, 20, data, 8, digest);

	printf( "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7\n" );
	printf( "%s\n", Hex(digest, 32) );
}

int scrypt_wrapper( uint8_t const * const masterPass, const int masterpassLen,
        uint8_t const * const masterKeySalt, const int masterKeySaltLen, 
        uint8_t * const masterKey )
{
    return crypto_scrypt(masterPass, masterpassLen, masterKeySalt, 
            masterKeySaltLen, MP_N, MP_r, MP_p, masterKey, MP_dkLen);
}

void test02()
{
    printf( "\n* Test 02\n" );
	char masterKeySalt[128];
	convertFromHex("636f6d2e6c796e6469722e6d617374657270617373776f72640000000c757365723031c3a5c3a4c3b6",
		masterKeySalt, 128 );
	const size_t masterKeySaltLength = 41;
	uint8_t masterKey[64];
    memset( masterKey, 0, 64 );

	char const * const masterPassword = "MasterPass01";
    
    int bOK = 0;
    bOK = scrypt_wrapper( (const uint8_t *)masterPassword, 
            strlen(masterPassword), (const uint8_t *)masterKeySalt, 
            masterKeySaltLength, masterKey );
        
	printf( "%d==%d\n", 0, bOK);
	printf ( "9124510a3ff74e95b5447686f717c52bd5f6b39676054472bf8ba83a72cd6972b790629de544d94d1e5f105d8c74a24910d944099cf4204dab16ac0feabb17b0\n");
    printf ( "%s\n", Hex(masterKey, 64) );
}

int main() {
  	printf( "hello, world!\n" );
  	
	
	//Run test 1
    test01();
    
    //Run test 2
    test02();
	

  	return 0;
}