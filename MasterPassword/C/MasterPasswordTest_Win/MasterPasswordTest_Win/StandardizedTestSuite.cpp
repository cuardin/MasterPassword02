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
typedef std::map<std::string,TestCase> TestData;

using namespace tinyxml2;

#ifndef XMLCheckResult
#define XMLCheckResult(a_eResult) if (a_eResult != XML_SUCCESS) { printf("Error: %i\n", a_eResult); return TestData(); }
#endif

TestData readXMLDoc() {
	XMLError eResult;

	XMLDocument doc;	
	XMLCheckResult(doc.LoadFile("../../test/standardTests.xml"));

	TestData data;
	XMLNode * pRoot = doc.FirstChild();
	if (pRoot == nullptr) return data;

	XMLElement * pListElement = pRoot->FirstChildElement("case");
	TestData testData;

	while ( pListElement != nullptr ) {
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
		while (pNodeListElement != nullptr) {
			const char* name = pNodeListElement->Name();
			const char* value = pNodeListElement->GetText();
			if (value != nullptr) {
				testCase[name] = value;
				std::cout << name << ": " << testCase[name] << std::endl;
			} else {
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
class EncodeTest: public testing::TestWithParam<std::pair<const std::string,TestCase>> {};

TEST_P(EncodeTest, EncodesAsExpected ) {	
	std::cout << "Test ID: " << GetParam().first << std::endl;
	//TODO: Fill in the test case here. :D
	ASSERT_TRUE(true);
}

INSTANTIATE_TEST_CASE_P(InstantiationName, EncodeTest, ::testing::ValuesIn(readXMLDoc()) );