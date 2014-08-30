#include <stdint.h>
#include <sstream>
#include <iomanip>      // std::setfill, std::setw

#include "testutils.h"


/*
function parseHexString(str) {
var result = [];
while (str.length >= 8) {
result.push(parseInt(str.substring(0, 8), 16));

str = str.substring(8, str.length);
}

return result;
}*/
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

void convertFromHex(std::string sHex, void * const output, const size_t outputLength)
{
	uint8_t * const buffer = (uint8_t * const)output;
	for (unsigned int i = 0; i < sHex.length() / 2 && i < outputLength; i++)
	{
		int v1 = charToValue(sHex[2 * i]);
		int v2 = charToValue(sHex[2 * i + 1]);

		buffer[i] = v1 * 16 + v2;
	}
}
