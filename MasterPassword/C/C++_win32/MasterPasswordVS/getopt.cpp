#include "getopt.h"

ParsedInput parseInput(const int nargs, char const * const arguments[])
{
	ParsedInput rValue;
	int i = 0;	
	rValue.fileName = arguments[i++];

	while (i < nargs)
	{
		std::string nextArg = arguments[i];
		if (nextArg.front() == '-' && nextArg.size() == 2 && i < nargs-1) {
			Option opt;
			opt.opt = nextArg.at(1);
			opt.value = arguments[i+1];
			rValue.options.push_back(opt);
			i++;
		}
		else {
			rValue.remaining.push_back(nextArg);
		}		
		i++;
	}
	return rValue;	
}