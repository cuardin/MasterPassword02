emcc -I../src/ -I../src/scrypt/ helloWorld.c testutils.c ..\src\scrypt\sha256.c ..\src\scrypt\crypto_scrypt-ref.c -s TOTAL_MEMORY=134217728 -s EXPORTED_FUNCTIONS="['_libcperciva_HMAC_SHA256_Buf', '_scrypt_wrapper']" 
