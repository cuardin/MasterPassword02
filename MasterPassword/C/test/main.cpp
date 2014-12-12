//
//  main.cpp
//  MasterPasswordTest_Win
//
//  Created by Daniel Armyr on 11/07/14.
//  Copyright (c) 2014 cuardin. All rights reserved.
//

#include "gtest/gtest.h"
#include <iostream>
#include <unistd.h>

void readXMLDoc();

int main(int argc, char **argv) {
    char cwd[1024];
    if (getcwd(cwd, sizeof(cwd)) != NULL)
        fprintf(stdout, "Current working dir: %s\n", cwd);
    else
        perror("getcwd() error");
    
	::testing::InitGoogleTest(&argc, argv);
    int rValue = RUN_ALL_TESTS();
	std::cout << "Press enter to quit";
	std::cin.get();
    return rValue;
}
