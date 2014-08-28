#include "gtest/gtest.h"
#include "utils.h"

TEST(UtilitiesTest, testConvertToHex) {
	uint8_t rawBytes[] = { 3, 156, 54 };
	std::string hexString = convertToHex(rawBytes, 3);

	EXPECT_EQ("039c36", hexString);
}

TEST(UtilitiesTest, testConvertFromHex) {
	std::string input("039c36");
	uint8_t rawBytes[3];
	convertFromHex(input, rawBytes, 3);

	EXPECT_EQ(3, rawBytes[0]);
	EXPECT_EQ(156, rawBytes[1]);
	EXPECT_EQ(54, rawBytes[2]);
}
