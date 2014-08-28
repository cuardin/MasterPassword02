#include "gtest/gtest.h"
#include "testutils.h"

extern "C" {
#include "sha256.h"
}

TEST(UtilitiesTest, testSHAInit) {
	uint8_t masterKey[64];
	convertFromHex("9124510a3ff74e95b5447686f717c52bd5f6b39676054472bf8ba83a72cd6972b790629de544d94d1e5f105d8c74a24910d944099cf4204dab16ac0feabb17b0",
		masterKey, 64);

	HMAC_SHA256_CTX ctx;
	HMAC_SHA256_Init(&ctx, masterKey, 64);

	EXPECT_EQ(std::string(
"9ded5e9e3a57df9e662d5ff4dc57552f296c86b103762393d779f171ca5e91b900000\
00000020000a712673c09c178a3837240b0c121f31de3c085a04033724489bd9e0c44fb5f4481a65\
4abd372ef7b2869266bba42947f26ef723faac2167b9d209a39dc8d2186fa4d09f8628562efa2375\
1d7064ee4df59cc5c8107aa11e85684541a9bc347c20000000000020000cd780d5663ab12c9e9182\
adaab4b997789aaefca2a59182ee3d7f4662e91352eebcc3ec1b918851142034c01d028fe154c851\
855c0a87c11f74af053b6e74bec"), std::string(Hex(&ctx, sizeof(ctx))));
}