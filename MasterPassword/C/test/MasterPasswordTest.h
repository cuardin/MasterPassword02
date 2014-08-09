//
//  MasterPasswordTest.h
//  MasterPasswordTest_OSX
//
//  Created by Daniel Armyr on 11/07/14.
//  Copyright (c) 2014 cuardin. All rights reserved.
//

#ifndef MasterPasswordTest_OSX_MasterPasswordTest_h
#define MasterPasswordTest_OSX_MasterPasswordTest_h

#include <cppunit/TestCase.h>
#include <cppunit/TestSuite.h>
#include <cppunit/TestCaller.h>
#include <cppunit/TestRunner.h>

using namespace CppUnit;

class MasterPasswordTest : public TestFixture {
public:
        
    void testPassGet01();
    void testPassGet02();
    void testPassGetLLunath();
    
    static Test* suite();
    
};

#endif
