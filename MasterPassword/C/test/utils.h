#ifndef _UTILS_H_
#define _UTILS_H_

#include <string>
extern "C" {
#include "types.h"
}

std::string convertToHex(void const * const input, size_t const inputLen);
void convertFromHex(std::string sHex, void * const output, const int outputLength);

#endif