#ifndef _MPW_CORE_H_
#define _MPW_CORE_H_

int mpw_core(char * const password, const size_t passLen, char const * const userName,
	char const * const masterPassword, char const * const siteTypeString, char const * const siteName,
	const int siteCounter);

int mpw_core_calculate_master_key_salt(char const * const mpNameSpace, char const * const userName,
                                       char * const masterKeySalt, unsigned long * const masterKeySaltLength );

int mpw_core_calculate_site_seed( char * const sitePasswordInfo, size_t * const sitePasswordInfoLength,
                                 char const * const mpNameSpace, char const * const siteName, int siteCounter );

int mpw_core_convert_to_password(char const * const siteTypeString, uint8_t const * const sitePasswordSeed,
                                 const size_t passLen, char * const password );
#endif