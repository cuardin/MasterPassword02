#include <stdint.h>
#include <string.h>
#include <stdio.h>
#include "testutils.h"



int charToValue(char c) {
	switch (c)
	{
	case '0': return 0;
	case '1': return 1;
	case '2': return 2;
	case '3': return 3;
	case '4': return 4;
	case '5': return 5;
	case '6': return 6;
	case '7': return 7;
	case '8': return 8;
	case '9': return 9;
	case 'a': return 10;
	case 'b': return 11;
	case 'c': return 12;
	case 'd': return 13;
	case 'e': return 14;
	case 'f': return 15;
	}
    return -100000;
}

void convertFromHex(char const * const input, void * const output, const size_t outputLength)
{	
	const unsigned int inputLength = strlen(input);
	uint8_t * const buffer = (uint8_t * const)output;
	for (unsigned int i = 0; i < inputLength / 2 && i < outputLength; i++)
	{
		int v1 = charToValue(input[2 * i]);
		int v2 = charToValue(input[2 * i + 1]);

		buffer[i] = v1 * 16 + v2;
	}
}

char hexBuffer[2048];

const char *Hex(const void *buf, size_t length) {
	memset(hexBuffer, 0, 2048);
    for (unsigned int kH = 0; kH < length; kH++)
        sprintf(&(hexBuffer[kH * 2]), "%02x", ((const uint8_t*)buf)[kH]);
	
    return hexBuffer;
}
