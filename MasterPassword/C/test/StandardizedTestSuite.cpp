extern "C" {
#include "mpw_core.h"
#include "types.h"
}

#include "gtest/gtest.h"

#include "testutils.h"
#include "tinyXML2.h"
#include <map>
#include <string>

//**********************************************************************
//Read in the XML file
//A map that maps from id to test case.
//The test case is a map from paramter name to parameter value.
typedef std::map<std::string, std::string> TestCase;
typedef std::map<std::string, TestCase> TestData;

using namespace tinyxml2;

#ifndef XMLCheckResult
#define XMLCheckResult(a_eResult) if (a_eResult != XML_SUCCESS) { printf("Error: %i\n", a_eResult); return TestData(); }
#endif

TestData readXMLDoc() {

    XMLDocument doc;
#ifdef WIN32
    XMLCheckResult(doc.LoadFile("../../test/mpw_tests.xml"));
#else
    XMLCheckResult(doc.LoadFile("mpw_tests.xml"));
#endif

    TestData data;
    XMLNode * pRoot = doc.FirstChild();
    if (pRoot == 0) return data;

    XMLElement * pListElement = pRoot->FirstChildElement("case");
    TestData testData;

    while (pListElement != 0) {
        //We have a test case. Allocate memory for it.
        TestCase testCase;

        //Get the ID of this case
        std::string id = pListElement->Attribute("id");
        std::cout << "ID: " << id << std::endl;

        //Now check if we have a parent.
        const char* pParent = pListElement->Attribute("parent");
        if (pParent) {
            testCase = testData.at(pParent);
        }

        //Now fill in the data from this node.
        XMLElement * pNodeListElement = pListElement->FirstChildElement();
        while (pNodeListElement != 0) {
            const char* name = pNodeListElement->Name();
            const char* value = pNodeListElement->GetText();
            if (value != 0) {
                testCase[name] = value;
                std::cout << name << ": " << testCase[name] << std::endl;
            }
            else {
                testCase[name] = "";
                std::cout << name << ": " << "null" << std::endl;
            }
            pNodeListElement = pNodeListElement->NextSiblingElement();
        }

        testData[id] = testCase;

        pListElement = pListElement->NextSiblingElement("case");
    }

    return testData;
}



//**********************************************************************
class EncodeTest : public testing::TestWithParam<std::pair<const std::string, TestCase> > {};

TEST_P(EncodeTest, EncodesAsExpected) {
    std::string id = GetParam().first;
    std::cout << "Test ID: " << id << std::endl;

    const int passLength = 128;
    char  passwd[passLength];
    char * const password = passwd;
    char  keyIDBuf[passLength];
    char * const keyID = keyIDBuf;

    TestCase testCase = GetParam().second;

    char const * const userName = testCase.at("fullName").c_str();
    char const * const masterPassword = testCase.at("masterPassword").c_str();
    char const * const siteTypeString = testCase.at("siteType").c_str();
    char const * const siteName = testCase.at("siteName").c_str();
    const uint32_t siteCounter = (uint32_t)atoll(testCase.at("siteCounter").c_str());
    char const * const siteScope = ScopeForVariant(VariantWithName(testCase.at("siteVariant").c_str()));
    char const * const siteContext = testCase.at("siteContext").c_str();

    int bOK = mpw_core(siteScope, password, passLength, userName, masterPassword, siteTypeString, siteName, siteCounter, siteContext, keyID, passLength);

    EXPECT_EQ(0, bOK);
    EXPECT_EQ(std::string(testCase.at("keyID")), std::string(keyID));
    EXPECT_EQ(std::string(testCase.at("result")), std::string(password));

}

INSTANTIATE_TEST_CASE_P(StandardizedTest, EncodeTest, ::testing::ValuesIn(readXMLDoc()));