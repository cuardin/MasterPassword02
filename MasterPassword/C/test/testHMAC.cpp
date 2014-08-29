#include "gtest/gtest.h"
#include "testutils.h"

extern "C" {
#include "sha256.h"
}

TEST(UtilitiesTest, testHMAC01) {
	//This test case is an official one from an RFC.
	uint8_t key[64];
	convertFromHex("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b",key, 20);
	uint8_t data[64];
	convertFromHex("4869205468657265", data, 8);

	uint8_t digest[32];
	
	HMAC_SHA256_Buf(key, 20, data, 8, digest);

	EXPECT_EQ(std::string("b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7"), std::string(Hex(digest, 32)));
}

TEST(UtilitiesTest, testSHAInit) {	
	SHA256_CTX ctx;
	SHA256_Init(&ctx);

	EXPECT_EQ(
		std::string("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"), 
		std::string(Hex(ctx.buf, 64)));
	EXPECT_EQ(
		std::string("67e6096a85ae67bb72f36e3c3af54fa57f520e518c68059babd9831f19cde05b"),
		std::string(Hex(ctx.state, 8*sizeof(uint32_t))));
	EXPECT_EQ(
		std::string("0000000000000000"),
		std::string(Hex(ctx.count, 2*sizeof(uint32_t))));
}

TEST(UtilitiesTest, testSHAWrite) {
	const size_t len = 32;
	uint8_t in[len];
	convertFromHex("123456789abcdef123456789abcdef123456789abcdef123456789abcdef", in, len);

	SHA256_CTX ctx;
	SHA256_Init(&ctx);	
	SHA256_Update(&ctx, in, len);

	EXPECT_EQ(	
		std::string("123456789abcdef123456789abcdef123456789abcdef123456789abcdefcccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"),
		std::string(Hex(ctx.buf, 64)));
	EXPECT_EQ(
		std::string("67e6096a85ae67bb72f36e3c3af54fa57f520e518c68059babd9831f19cde05b"),
		std::string(Hex(ctx.state, 8 * sizeof(uint32_t))));
	EXPECT_EQ(
		std::string("0000000000010000"),
		std::string(Hex(ctx.count, 2 * sizeof(uint32_t))));
}

TEST(UtilitiesTest, testSHAFinalize) {
	const size_t len = 32;
	uint8_t in[len];
	convertFromHex("123456789abcdef123456789abcdef123456789abcdef123456789abcdef", in, len);

	uint8_t digest[32];

	SHA256_CTX ctx;
	SHA256_Init(&ctx);
	SHA256_Update(&ctx, in, len);
	SHA256_Final(digest, &ctx);

	EXPECT_EQ(
		std::string("5b957f71592fb4a517e33db442cc2367231e07b67c4c6c34508e03a174e05191"),
		std::string(Hex(digest, 32)));	
}
