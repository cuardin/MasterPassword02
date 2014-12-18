#ifndef _GETOPT_H_
#define _GETOPT_H_

#include <string>
#include <vector>

struct Option
{
	char opt;
	std::string value;
};

struct ParsedInput
{
	std::string fileName;
	std::vector<Option> options;
	std::vector<std::string> remaining;
};

ParsedInput parseInput(const int nargs, char const * const arguments[]);


#endif