#!/bin/bash
#
# Copyright (C) 2018, Rishi Gupta. All rights reserved.
#

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
