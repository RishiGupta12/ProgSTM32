#!/bin/bash
#
# Copyright (C) 2018, Rishi Gupta. All rights reserved.
#

# Add custom copyright information
AC_COPYRIGHT(Copyright (C) 2018, Rishi Gupta. All rights reserved.)

# Specify minimum version of autoconf required
AC_PREREQ(2.64)

# Package version will remain constant unless edited manually
# by modifying package_version file
m4_define([CURRENT_VERSION],
     m4_esyscmd([ if test -e package_version; then
                     . ./package_version
                     printf "$PACKAGE_VERSION"
                  else
                     printf "unknown"
                  fi ]))

# Process any command-line arguments and perform initialization 
# and verification
AC_INIT([flashSTM32], [CURRENT_VERSION], [gupt21@gmail.com])

# Instantiating macro to generate config.h for passing #DEFINE
# to source code
AC_CONFIG_HEADERS([config.h])

# Central place to that will contain all custom macros
AC_CONFIG_MACRO_DIR([m4])

# Automake options that should be applied to every Makefile.am 
# in the tree
AM_INIT_AUTOMAKE([foreign 1.11 serial-tests -Wall -Wno-portability subdir-objects silent-rules color-tests dist-xz tar-ustar])

# Compute the canonical host-system type variable, host, 
# host_cpu, host_vendor, and host_os
AC_CANONICAL_HOST
AC_DEFINE_UNQUOTED([CANONICAL_HOST], "$host", [Canonical host string.])

# Add tests so that the generated libtool script will understand 
# the characteristics of the host
LT_INIT

# Determine a C compiler to use
AC_PROG_CC

# Set output variable INSTALL to the name of a BSD-compatible install 
# program, if one is found in the current PATH. Otherwise, set INSTALL 
# to ‘dir/install-sh -c’
AC_PROG_INSTALL

# Check for each program and if found set variable to the name of that 
# program
AC_CHECK_PROGS([M4], gm4 m4, no)

# List of makefiles that automake should generate
AC_CONFIG_FILES([
Makefile
sdk/Makefile
sdk/uart/Makefile
sdk/uart/src/Makefile
])

# Generate config.status and launch it, run the configuration action
# now
AC_OUTPUT

# 
AC_DEFUN([AX_SUMMARIZE_ENV],
[
BUILD_USER=${USER}
BUILD_ARCH=${host}
BUILD_HOST=${ac_hostname}
BUILD_DATE=$(date +'%F %H:%M')

AC_DEFINE_UNQUOTED([BUILD_USER],     "${BUILD_USER}",     [The fine user who built the package])
AC_DEFINE_UNQUOTED([BUILD_ARCH],     "${BUILD_ARCH}",     [Architecture of the build host])
AC_DEFINE_UNQUOTED([BUILD_HOST],     "${BUILD_HOST}",     [Build host name])
AC_DEFINE_UNQUOTED([BUILD_DATE],     "${BUILD_DATE}",     [Build date])

AC_SUBST(BUILD_USER)
AC_SUBST(BUILD_ARCH)
AC_SUBST(BUILD_HOST)
AC_SUBST(BUILD_DATE)
AC_SUBST(BUILD_VERSION)
])

AC_DEFUN([AX_SUMMARIZE_CONFIG],
[
echo
echo '##########################################################################'
echo '#                                SUMMARY                                 #'
echo '##########################################################################'
echo
echo Configure date................ : $BUILD_DATE
echo Package version............... : $PACKAGE-$VERSION
echo C compiler.................... : $CC
echo Build architecture............ : $BUILD_ARCH
echo C compiler flags.............. : $CFLAGS
echo
echo Configure complete! Now proceed with:
echo "    - 'make'               compile the libraries/binaries"
echo "    - 'make V=s'           to print extra logs from make while building"
echo "    - 'make check'         run automated tests"
echo "    - 'make install'       install the libraries/binaries/files in output/product folder"
echo
])

AX_SUMMARIZE_ENV
AX_SUMMARIZE_CONFIG

