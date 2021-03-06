#include "parseInput.h"

ParsedInput parseInput(const int nargs, char const * const arguments[])
{
    ParsedInput rValue;
    int i = 0;
    rValue.fileName = arguments[i++];

    while (i < nargs)
    {
        std::string nextArg = arguments[i];
        if (nextArg.front() == '-' && nextArg.size() == 2) {
            Option opt;
            opt.opt = nextArg.at(1);
            if (i < nargs - 1 && arguments[i + 1][0] != '-') {
                opt.value = arguments[i + 1];
                rValue.options.push_back(opt);
                i++;
            }
            else {
                opt.value = "";
            }
        }
        else {
            rValue.remaining.push_back(nextArg);
        }
        i++;
    }
    return rValue;
}