#ifndef _TESTUTILS_H_
#define _TESTUTILS_H_

#include <string>
extern "C" {
#include "types.h"
}

void convertFromHex(std::string sHex, void * const output, const size_t outputLength);

#endif