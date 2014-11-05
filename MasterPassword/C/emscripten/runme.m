!emcc helloWorld.c testutils.c types.c sha256.c crypto_scrypt-ref.c -s TOTAL_MEMORY=134217728
tic;
!node a.out.js
toc;