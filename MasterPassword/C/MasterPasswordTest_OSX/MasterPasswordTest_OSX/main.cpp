//
//  main.cpp
//  MasterPasswordTest_OSX
//
//  Created by Daniel Armyr on 11/07/14.
//  Copyright (c) 2014 cuardin. All rights reserved.
//

//NOTE: For this to compile, you need CPPUnit 1.12.1 installed on your computer.

#include <iostream>
#include "MasterPasswordTest.h"
#include <cppunit/ui/text/TestRunner.h>


int main(int argc, const char * argv[])
{
 
    TextUi::TestRunner runner;
    runner.addTest(MasterPasswordTest::suite());
    runner.run();
    
    return 0;
}

