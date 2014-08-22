#include "MasterPasswordTest.h"
extern "C" {
#include "mpw_core.h"
#include "types.h"
}

Test* MasterPasswordTest::suite() {
    TestSuite *suite = new TestSuite;
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd",
                                                                &MasterPasswordTest::testPassGenerateMainSeed ) );
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd",
                                                                &MasterPasswordTest::testPassGenerateSiteSeed ) );
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd", &MasterPasswordTest::testPassGet01 ) );
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd", &MasterPasswordTest::testPassGet02 ) );
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd", &MasterPasswordTest::testPassGet03 ) );
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd", &MasterPasswordTest::testPassGetLLunath ) );
    return suite;
}


void MasterPasswordTest::testPassGenerateMainSeed()
{
    int buffLength = 1024;
    char  buffer[buffLength];
    char * const masterKeySalt = buffer;
    size_t masterKeySaltLength = 0;
    
    char const * const userName = "user01";
    char const * const mpNameSpace = "com.lyndir.masterpassword";

    
    int bOK = mpw_core_calculate_master_key_salt(mpNameSpace, userName, masterKeySalt, &masterKeySaltLength );

    CPPUNIT_ASSERT_EQUAL( 35, (int)masterKeySaltLength );
    CPPUNIT_ASSERT_EQUAL( 0, bOK );
    CPPUNIT_ASSERT_EQUAL( std::string("EA791F81ADC02DE8F8035681E56D60105EFFD6B611E79A0F4C2755C5C3D57943"),
                         std::string(IDForBuf(masterKeySalt, masterKeySaltLength) ) );
}

void MasterPasswordTest::testPassGenerateSiteSeed()
{
    int buffLength = 1024;
    char  buffer[buffLength];
    char * const sitePasswordInfo = buffer;
    size_t sitePasswordInfoLength = 0;
    
    char const * const siteName = "site01.com";
    const int siteCounter = 3;
    char const * const mpNameSpace = "com.lyndir.masterpassword";
    
    
    int bOK = mpw_core_calculate_site_seed( sitePasswordInfo, &sitePasswordInfoLength,
                                           mpNameSpace, siteName, siteCounter );
    
    CPPUNIT_ASSERT_EQUAL( 0, bOK );
    CPPUNIT_ASSERT_EQUAL( 43, (int)sitePasswordInfoLength );
    CPPUNIT_ASSERT_EQUAL( std::string("01842AEE52DB48194A1B2E0C26B27AB9F549C1FACF5D2D4651B5C06B03BC001E"),
                         std::string(IDForBuf(sitePasswordInfo, sitePasswordInfoLength) ) );
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

    CPPUNIT_ASSERT_EQUAL( 0, bOK );
    CPPUNIT_ASSERT_EQUAL( std::string("SebeKuka3[Vavk"), std::string(password) );
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
    
    CPPUNIT_ASSERT_EQUAL( 0, bOK );
    CPPUNIT_ASSERT_EQUAL( std::string("0535"), std::string(password) );

}

void MasterPasswordTest::testPassGet03()
{
    int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    
    char const * const userName = "AnåthörUser";
    char const * const masterPassword = "AndMöstärPoss";
    char const * const siteTypeString = "pin";
    char const * const siteName = "anöther.com";
    const int siteCounter = 5;
    
    
    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);
    
    CPPUNIT_ASSERT_EQUAL(0, bOK );
    CPPUNIT_ASSERT_EQUAL(std::string("5307"), std::string(password) );
    
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

    CPPUNIT_ASSERT_EQUAL( 0, bOK );
    CPPUNIT_ASSERT_EQUAL(std::string("Dora6.NudiDuhj"), std::string(password) );
}