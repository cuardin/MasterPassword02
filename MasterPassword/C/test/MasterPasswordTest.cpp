extern "C" {
#include "mpw_core.h"
#include "types.h"
}

#include "gtest/gtest.h"

#include "utils.h"

TEST(MasterPasswordTest, testPassGenerateMainSeed ) {    
    const int buffLength = 1024;
    char  buffer[buffLength];
    char * const masterKeySalt = buffer;
    size_t masterKeySaltLength = 0;
    
    char const * const userName = "user01åäö";
    char const * const mpNameSpace = "com.lyndir.masterpassword";

    
    int bOK = mpw_core_calculate_master_key_salt(mpNameSpace, userName, masterKeySalt, &masterKeySaltLength );

    EXPECT_EQ( 12, (int)strlen(userName) ); //Ensure utf8 encoding(So last 3 are 2-byte);
    EXPECT_EQ( 41, (int)masterKeySaltLength );
    EXPECT_EQ( 0, bOK );
    EXPECT_EQ( std::string("636f6d2e6c796e6469722e6d617374657270617373776f72640000000c757365723031c3a5c3a4c3b6"),
                         convertToHex(masterKeySalt, masterKeySaltLength) );
}

TEST(MasterPasswordTest, testGenerateSecretKey)
{	
	EXPECT_EQ(0, 0);
}

TEST(MasterPasswordTest,testPassGenerateSiteSeed) 
{
    const int buffLength = 1024;
    char  buffer[buffLength];
    char * const sitePasswordInfo = buffer;
    size_t sitePasswordInfoLength = 0;
    
    char const * const siteName = "site01.åäö";
    const int siteCounter = 3;
    char const * const mpNameSpace = "com.lyndir.masterpassword";
    
    
    int bOK = mpw_core_calculate_site_seed( sitePasswordInfo, &sitePasswordInfoLength,
                                           mpNameSpace, siteName, siteCounter );

    EXPECT_EQ( 13, (int)strlen(siteName) ); //Ensure utf8 encoding(So last 3 are 2-byte);
    EXPECT_EQ( 0, bOK );
    EXPECT_EQ( 46, (int)sitePasswordInfoLength );
    EXPECT_EQ( std::string(
        "636f6d2e6c796e6469722e6d617374657270617373776f72640000000d7369746530312ec3a5c3a4c3b600000003"),
        convertToHex(sitePasswordInfo, sitePasswordInfoLength) );
}

TEST(MasterPasswordTest, testPassHashSecretKey)
{
	uint8_t const * const masterKey =     //64 random characters and a null terminator
		(uint8_t*)"C4157B94088A1A54DEE0516F7505A3ABC4157B94088A1A54DEE0516F7505A3AB";
	char const * const sitePasswordInfo =     //64 random characters and a null terminator
		"C4157B94088A1A54DEE0516F7505A3ABC4157B94088A1A54DEE0516F7505A3AB";
	uint8_t sitePasswordSeed[32];

	mpw_core_compute_hmac(masterKey, sitePasswordInfo, 64, sitePasswordSeed);
	EXPECT_EQ(std::string("51e6505192032398dbc083868413371308e0f6eac31e4af36074fa12ee0e9565"), convertToHex(sitePasswordSeed,32));
}

TEST(MasterPasswordTest,testPassConvertToPassword)
{
    const int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    
    
    uint8_t const * const sitePasswordSeed =     //31 random characters and a null terminator
    (uint8_t*)"C4157B94088A1A54DEE0516F7505A3A";
    
    char const * const siteTypeString = "long";
    
    int bOK = mpw_core_convert_to_password(siteTypeString, sitePasswordSeed,
                                           passLength, password );
    
    EXPECT_EQ( 0, bOK );
    EXPECT_EQ( std::string("NuprFino6_Dudo"), std::string(password) );
}

TEST(MasterPasswordTest,testPassGet01)
{
    const int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    
    char const * const userName = "user01";
    char const * const masterPassword = "MasterPass01";
    char const * const siteTypeString = "long";
    char const * const siteName = "testSite";
    const int siteCounter = 1;
    
    
    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);

    EXPECT_EQ( 0, bOK );
    EXPECT_EQ( std::string("SebeKuka3[Vavk"), std::string(password) );
}

/*
TEST(MasterPasswordTest,testPassGet02)
{
    const int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    
    char const * const userName = "anotherUsername";
    char const * const masterPassword = "AndAMasterPassword";
    char const * const siteTypeString = "pin";
    char const * const siteName = "anotherSite.com";
    const int siteCounter = 5;
    
    
    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);
    
    EXPECT_EQ( 0, bOK );
    EXPECT_EQ( std::string("0535"), std::string(password) );

}

TEST(MasterPasswordTest,testPassGet03)
{
    const int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    
    char const * const userName = "AnåthörUser";
    char const * const masterPassword = "AndMöstärPoss";
    char const * const siteTypeString = "pin";
    char const * const siteName = "anöther.com";
    const int siteCounter = 5;
    
    
    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);
    
    EXPECT_EQ(0, bOK );
    EXPECT_EQ(std::string("5307"), std::string(password) );
    
}


TEST(MasterPasswordTest,testPassGetLLunath)
{
    const int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;

    char const * const userName = "Robert Lee Mitchel";
    char const * const masterPassword = "banana colored duckling";
    char const * const siteTypeString = "long";
    char const * const siteName = "masterpasswordapp.com";
    const int siteCounter = 1;


    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);

    EXPECT_EQ( 0, bOK );
    EXPECT_EQ(std::string("Dora6.NudiDuhj"), std::string(password) );
}

*/