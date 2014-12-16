//
//  main.cpp
//  MasterPasswordTest_Win
//
//  Created by Daniel Armyr on 11/07/14.
//  Copyright (c) 2014 cuardin. All rights reserved.
//

#include "gtest/gtest.h"
#include <iostream>
#ifdef WIN32
//Win32-specific
#else
#include <unistd.h>
#endif

void readXMLDoc();

int main(int argc, char **argv) {        
	::testing::InitGoogleTest(&argc, argv);
    int rValue = RUN_ALL_TESTS();
	std::cout << "Press enter to quit";	
	std::cin.get();
    return rValue;
}
