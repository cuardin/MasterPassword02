#!/bin/sh

#  download_scrypt.sh
#  MasterPasswordCLI_OSX
#
#  Created by Daniel Armyr on 06/07/14.
#  Copyright (c) 2014 cuardin. All rights reserved.

svn checkout http://scrypt.googlecode.com/svn/trunk/ scrypt
cp scrypt/lib/crypto/crypto_scrypt-ref.c ../src/scrypt/
cp scrypt/lib/crypto/crypto_scrypt.h ../src/scrypt/
cp scrypt/scrypt_platform.h ../src/scrypt/
cp scrypt/libcperciva/alg/sha256.c ../src/scrypt/
cp scrypt/libcperciva/alg/sha256.h ../src/scrypt/
cp scrypt/libcperciva/util/sysendian.h ../src/scrypt/

cd ../src/scrypt
git apply -v ../../util/patch.txt
cd ../../util

