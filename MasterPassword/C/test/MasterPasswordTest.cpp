#include "MasterPasswordTest.h"
extern "C" {
#include "mpw_core.h"
}

Test* MasterPasswordTest::suite() {
    TestSuite *suite = new TestSuite;
    
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd", &MasterPasswordTest::testPassGet01 ) );
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd", &MasterPasswordTest::testPassGet02 ) );
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd", &MasterPasswordTest::testPassGetLLunath ) );
    return suite;
}


void MasterPasswordTest::testPassGet01()
{
    int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    
    char const * const userName = "user01";
    char const * const masterPassword = "MasterPass01";
    char const * const siteTypeString = "long";
    char const * const siteName = "testSite";
    const int siteCounter = 1;
    
    
    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);

    CPPUNIT_ASSERT( bOK == 0 );
    CPPUNIT_ASSERT( !strcmp("SebeKuka3[Vavk", password ) );
}

void MasterPasswordTest::testPassGet02()
{
    int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    
    char const * const userName = "anotherUsername";
    char const * const masterPassword = "AndAMasterPassword";
    char const * const siteTypeString = "pin";
    char const * const siteName = "anotherSite.com";
    const int siteCounter = 5;
    
    
    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);
    
    CPPUNIT_ASSERT( bOK == 0 );
    CPPUNIT_ASSERT( !strcmp("0535", password ) );

}

void MasterPasswordTest::testPassGetLLunath()
{
    int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;

    char const * const userName = "Robert Lee Mitchel";
    char const * const masterPassword = "banana colored duckling";
    char const * const siteTypeString = "long";
    char const * const siteName = "masterpasswordapp.com";
    const int siteCounter = 1;


    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);

    CPPUNIT_ASSERT( bOK == 0 );
    CPPUNIT_ASSERT( !strcmp("Dora6.NudiDuhj", password ) );
}