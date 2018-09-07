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

























