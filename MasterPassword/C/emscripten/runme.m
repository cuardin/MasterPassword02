!emcc helloWorld.c testutils.c types.c sha256.c crypto_scrypt-ref.c -s TOTAL_MEMORY=134217728 -s EXPORTED_FUNCTIONS="['_libcperciva_HMAC_SHA256_Buf', '_scrypt_wrapper']"
tic;
!node a.out.js
toc;    