!emcc helloWorld.c testutils.c types.c sha256.c crypto_scrypt-ref.c -s TOTAL_MEMORY=128000000
!node a.out.js