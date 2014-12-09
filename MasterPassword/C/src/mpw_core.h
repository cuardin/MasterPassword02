#ifndef _MPW_CORE_H_
#define _MPW_CORE_H_

#include "types.h"

int mpw_core(char const * const mpNameSpace, char * const password, const size_t passLen, char const * const userName,
	char const * const masterPassword, char const * const siteTypeString, char const * const siteName,
	const int siteCounter, char * const keyID, const size_t keyIDLen );

int mpw_core_calculate_master_key_salt(char const * const mpNameSpace, char const * const userName,
                                       char * const masterKeySalt, size_t * const masterKeySaltLength );

int mpw_core_calculate_master_key(char const * const masterPassword, char const * const masterKeySalt,
	size_t masterKeySaltLength, uint8_t * const masterKey, char * const keyID, const size_t keyIDLen);

int mpw_core_calculate_site_seed( uint8_t * const sitePasswordInfo, size_t * const sitePasswordInfoLength,
                                 char const * const mpNameSpace, char const * const siteName, uint32_t siteCounter );

void mpw_core_compute_hmac(uint8_t const * const masterKey, uint8_t const * const sitePasswordInfo,
                           const size_t sitePasswordInfoLength, uint8_t * const sitePasswordSeed );

int mpw_core_convert_to_password(char const * const siteTypeString, uint8_t const * const sitePasswordSeed,
                                 const size_t passLen, char * const password );
#endif