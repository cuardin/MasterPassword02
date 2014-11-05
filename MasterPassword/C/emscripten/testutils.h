#ifndef _TESTUTILS_H_
#define _TESTUTILS_H_

#include "types.h"

void convertFromHex(char const * const input, void * const output, const size_t outputLength);
const char *Hex(const void *buf, size_t length);

#endif