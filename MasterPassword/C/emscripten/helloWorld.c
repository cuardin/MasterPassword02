#include <stdio.h>

#include "testutils.h"
#include "sha256.h"


int main() {
  	printf( "hello, world!\n" );
  	
	
	//This test case is an official one from an RFC.
	uint8_t key[64];
	convertFromHex("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b",key, 20);
	uint8_t data[64];
	convertFromHex("4869205468657265", data, 8);

	uint8_t digest[32];
	
	HMAC_SHA256_Buf(key, 20, data, 8, digest);

	printf( "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7\n" );
	printf( "%s", Hex(digest, 32) ); printf("\n");
	

  	return 0;
}