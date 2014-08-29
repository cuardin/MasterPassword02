extern "C" {
#include "mpw_core.h"
#include "types.h"
}

#include "gtest/gtest.h"

#include "testutils.h"
/*
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
                         std::string(Hex(masterKeySalt, masterKeySaltLength)) );
}
*/
/*
TEST(MasterPasswordTest, testGenerateSecretKey)
{	
	char masterKeySalt[128];
	convertFromHex("636f6d2e6c796e6469722e6d617374657270617373776f72640000000c757365723031c3a5c3a4c3b6",
		masterKeySalt, 128 );
	const size_t masterKeySaltLength = 41;
	uint8_t masterKey[64];

	char const * const masterPassword = "MasterPass01";

	int bOK = mpw_core_calculate_master_key(masterPassword, masterKeySalt, masterKeySaltLength, masterKey);

	EXPECT_EQ(0, bOK);
	EXPECT_EQ(std::string("9124510a3ff74e95b5447686f717c52bd5f6b39676054472bf8ba83a72cd6972b790629de544d94d1e5f105d8c74a24910d944099cf4204dab16ac0feabb17b0"), 
		std::string(Hex(masterKey, 64)) );
}
*/
/*
TEST(MasterPasswordTest,testPassGenerateSiteSeed) 
{
    const int buffLength = 1024;
    char  buffer[buffLength];
    char * const sitePasswordInfo = buffer;
    size_t sitePasswordInfoLength = 0;
    
    char const * const siteName = "site01.åäö";
    const int siteCounter = 1;
	const char *mpNameSpace = "com.lyndir.masterpassword";
    
    
    int bOK = mpw_core_calculate_site_seed( sitePasswordInfo, &sitePasswordInfoLength,
                                           mpNameSpace, siteName, siteCounter );

    EXPECT_EQ( 13, (int)strlen(siteName) ); //Ensure utf8 encoding(So last 3 are 2-byte);
    EXPECT_EQ( 0, bOK );
    EXPECT_EQ( 46, (int)sitePasswordInfoLength );		
	EXPECT_EQ(std::string("636f6d2e6c796e6469722e6d617374657270617373776f72640000000d7369746530312ec3a5c3a4c3b600000001"),
        std::string( Hex(sitePasswordInfo, sitePasswordInfoLength) ) );
}
*/
TEST(MasterPasswordTest, testPassHashSecretKey)
{
	uint8_t masterKey[64];
	convertFromHex("9124510a3ff74e95b5447686f717c52bd5f6b39676054472bf8ba83a72cd6972b790629de544d94d1e5f105d8c74a24910d944099cf4204dab16ac0feabb17b0",
		masterKey, 64);
	
	uint8_t sitePasswordInfo[46]; //46 characters in this case.
	convertFromHex("636f6d2e6c796e6469722e6d617374657270617373776f72640000000d7369746530312ec3a5c3a4c3b600000001",
		sitePasswordInfo, 46);
	uint8_t sitePasswordSeed[32];

	mpw_core_compute_hmac(masterKey, sitePasswordInfo, 46, sitePasswordSeed);
		
	EXPECT_EQ(std::string("21d6d4b2466641c519c5f3e6903e0557ef6d7efd46a5dddbbe9d0e7d13be9c2a"),
		std::string(Hex(sitePasswordSeed,32)) );
}
/*
TEST(MasterPasswordTest,testPassConvertToPassword)
{
    const int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
        
	uint8_t sitePasswordSeed[32];
	convertFromHex("21d6d4b2466641c519c5f3e6903e0557ef6d7efd46a5dddbbe9d0e7d13be9c2a",
		sitePasswordSeed, 32);
    
    char const * const siteTypeString = "long";
    
    int bOK = mpw_core_convert_to_password(siteTypeString, sitePasswordSeed,
                                           passLength, password );
    
    EXPECT_EQ( 0, bOK );
	EXPECT_EQ(std::string("Gink2^LalqZuza"), std::string(password));
}


TEST(MasterPasswordTest,testPassGet01)
{
    const int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    
    char const * const userName = "user01åäö";
    char const * const masterPassword = "MasterPass01";
    char const * const siteTypeString = "long";
	char const * const siteName = "site01.åäö";
    const int siteCounter = 1;
    
    
    int bOK = mpw_core(password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter);

    EXPECT_EQ( 0, bOK );
	EXPECT_EQ(std::string("Gink2^LalqZuza"), std::string(password));
}


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