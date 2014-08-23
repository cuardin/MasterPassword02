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
    suite->addTest( new CppUnit::TestCaller<MasterPasswordTest>( "testAdd",
                                                                &MasterPasswordTest::testPassConvertToPassword ) );
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
    
    char const * const userName = "user01åäö";
    char const * const mpNameSpace = "com.lyndir.masterpassword";

    
    int bOK = mpw_core_calculate_master_key_salt(mpNameSpace, userName, masterKeySalt, &masterKeySaltLength );

    CPPUNIT_ASSERT_EQUAL( 12, (int)strlen(userName) ); //Ensure utf8 encoding(So last 3 are 2-byte);
    CPPUNIT_ASSERT_EQUAL( 41, (int)masterKeySaltLength );
    CPPUNIT_ASSERT_EQUAL( 0, bOK );
    CPPUNIT_ASSERT_EQUAL( std::string("222E3E9BD4111B77ADF4AA55A380C4E0D79C5858F210218CA8431384A9735BA0"),
                         std::string(IDForBuf(masterKeySalt, masterKeySaltLength) ) );
}

void MasterPasswordTest::testPassGenerateSiteSeed()
{
    int buffLength = 1024;
    char  buffer[buffLength];
    char * const sitePasswordInfo = buffer;
    size_t sitePasswordInfoLength = 0;
    
    char const * const siteName = "site01.åäö";
    const int siteCounter = 3;
    char const * const mpNameSpace = "com.lyndir.masterpassword";
    
    
    int bOK = mpw_core_calculate_site_seed( sitePasswordInfo, &sitePasswordInfoLength,
                                           mpNameSpace, siteName, siteCounter );

    CPPUNIT_ASSERT_EQUAL( 13, (int)strlen(siteName) ); //Ensure utf8 encoding(So last 3 are 2-byte);
    CPPUNIT_ASSERT_EQUAL( 0, bOK );
    CPPUNIT_ASSERT_EQUAL( 46, (int)sitePasswordInfoLength );
    CPPUNIT_ASSERT_EQUAL( std::string("C4157B94088A1A54DEE0516F7505A3A3C94EACFA6B6E6A32339333257AE85355"),
                         std::string(IDForBuf(sitePasswordInfo, sitePasswordInfoLength) ) );
}

void MasterPasswordTest::testPassConvertToPassword()
{
    int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;


    uint8_t const * const sitePasswordSeed =     //31 random characters and a null terminator
        (uint8_t*)"C4157B94088A1A54DEE0516F7505A3A";

    char const * const siteTypeString = "long";

    int bOK = mpw_core_convert_to_password(siteTypeString, sitePasswordSeed,
                                     passLength, password );

    CPPUNIT_ASSERT_EQUAL( 0, bOK );
    CPPUNIT_ASSERT_EQUAL( std::string("NuprFino6_Dudo"), std::string(password) );
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