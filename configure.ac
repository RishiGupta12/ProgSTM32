# -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

#
# This file is part of progstm32.
# 
# Copyright (C) 2018, Rishi Gupta. All rights reserved.
# 
# The progstm32 is free software; you can redistribute it and/or modify it 
# under the terms of the GNU Lesser General Public License as published 
# by the Free Software Foundation; either version 2.1 of the License, or 
# (at your option) any later version.
# 
# The progstm32 is distributed in the hope that it will be useful, but 
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
# License for more details.
# 
# You should have received a copy of the GNU Lesser General Public License 
# along with this library; if not, write to the Free Software Foundation,Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#

dnl Process this file with Autoconf to produce a portable
dnl configure script.

dnl Add custom copyright information
AC_COPYRIGHT(Copyright (C)2018 Rishi Gupta. All rights reserved.)

dnl Specify minimum version of autoconf required
AC_PREREQ(2.68)

dnl Central place to that will contain all custom macros
AC_CONFIG_MACRO_DIR([m4])

dnl Package version will remain constant unless edited manually
dnl by modifying package_version file
m4_define([CURRENT_VERSION],
     m4_esyscmd([ if test -e package_version; then
                     . ./package_version
                     printf "$PACKAGE_VERSION"
                  else
                     printf "unknown"
                  fi ]))

dnl Process any command-line arguments and perform initialization and verification
AC_INIT([progstm32], [CURRENT_VERSION], [gupt21@gmail.com])

dnl If the user does not supply any base location for installation, let us use
dnl standard location /usr/share/*
AC_PREFIX_DEFAULT(/usr)

dnl Instantiating macro to generate config.h for passing #DEFINE
dnl to source code
AC_CONFIG_HEADERS([config.h])

dnl Look for various helper scripts
AC_CONFIG_AUX_DIR([build-aux])

dnl Automake options that should be applied to every Makefile.am 
dnl in the tree
AM_INIT_AUTOMAKE([foreign 1.11 serial-tests -Wall -Wno-portability subdir-objects silent-rules color-tests dist-xz tar-ustar])

dnl Compute the canonical host-system type variable, host, 
dnl host_cpu, host_vendor, and host_os
AC_CANONICAL_HOST
AC_DEFINE_UNQUOTED([CANONICAL_HOST], "$host", [Canonical host string.])

dnl Add tests so that the generated libtool script will understand 
dnl the characteristics of the host
LT_INIT

dnl Determine a C compiler to use
AC_PROG_CC

dnl Set output variable INSTALL to the name of a BSD-compatible install 
dnl program, if one is found in the current PATH. Otherwise, set INSTALL 
dnl to ‘dir/install-sh -c’
AC_PROG_INSTALL

dnl Check for each program and if found set variable to the name of that 
dnl program
AC_CHECK_PROGS([M4], gm4 m4, no)

pdir=`pwd`
AC_SUBST(pdir)

dnl check if global common files exist or not for build infrastructure sanity
AS_IF([test ! -e $pdir/progstm32.mk], [AC_MSG_ERROR([Couldn't find progstm32.mk])])

dnl check if shell supports color, if yes, use it when printing messages from makefile
AM_CONDITIONAL([COLOR_SUPPORTED], [command -v tput >/dev/null 2>&1])

dnl if JAVA HOME is set, locate jvm in that installation otherwise try to find it in PATH
AS_IF([test "x$JAVA_HOME" == "x"],
  [AC_PATH_PROG([JAVA], [java], [no])
   AS_IF([test "x$JAVA" == "xno"], [AC_MSG_ERROR([Couldn't find java in path])])],
  [AS_IF([test -e $JAVA_HOME/bin/java], [JAVA=$JAVA_HOME/bin/java], [AC_MSG_ERROR([Couldn't find $JAVA_HOME/bin/java])])])

dnl if JAVA HOME is set, locate javac compiler in that installation otherwise try to find it in PATH
AS_IF([test "x$JAVA_HOME" == "x"],
  [AC_PATH_PROG([JAVAC], [javac], [no])
   AS_IF([test "x$JAVAC" == "xno"], [AC_MSG_ERROR([Couldn't find javac in path])])],
  [AS_IF([test -e $JAVA_HOME/bin/javac], [JAVAC=$JAVA_HOME/bin/javac], [AC_MSG_ERROR([Couldn't find $JAVA_HOME/bin/javac])])])

dnl if JAVA HOME is set, locate jar tool in that installation otherwise try to find it in PATH
AS_IF([test "x$JAVA_HOME" == "x"],
  [AC_PATH_PROG([JAR], [jar], [no])
   AS_IF([test "x$JAR" == "xno"], [AC_MSG_ERROR([Couldn't find jar tool in path])])],
  [AS_IF([test -e $JAVA_HOME/bin/jar], [JAR=$JAVA_HOME/bin/jar], [AC_MSG_ERROR([Couldn't find $JAVA_HOME/bin/jar])])])

dnl Check for ant tool to build java source
AC_PATH_PROG([ANT], [ant], [no])
AS_IF([test "x$ANT" == "xno"], [AC_MSG_ERROR([Couldn't find ant tool])])

dnl List of makefiles that automake should generate
AC_CONFIG_FILES([
Makefile
sdk/Makefile
application/Makefile
])

dnl Generate config.status and launch it, run the configuration action
dnl now
AC_OUTPUT

dnl
AC_DEFUN([AX_SUMMARIZE_ENV],
[
BUILD_USER=${USER}
BUILD_ARCH=${host}
BUILD_HOST=${ac_hostname}
BUILD_DATE=$(date +'%F %H:%M')

AC_DEFINE_UNQUOTED([BUILD_USER], "${BUILD_USER}", [The fine user who built the package])
AC_DEFINE_UNQUOTED([BUILD_ARCH], "${BUILD_ARCH}", [Architecture of the build host])
AC_DEFINE_UNQUOTED([BUILD_HOST], "${BUILD_HOST}", [Build host name])
AC_DEFINE_UNQUOTED([BUILD_DATE], "${BUILD_DATE}", [Build date])

AC_SUBST(BUILD_USER)
AC_SUBST(BUILD_ARCH)
AC_SUBST(BUILD_HOST)
AC_SUBST(BUILD_DATE)
AC_SUBST(BUILD_VERSION)
])

AC_DEFUN([AX_SUMMARIZE_CONFIG],
[
echo
echo '  ------------------------------------------------'
echo ' |                  SUMMARY                       |'
echo '  ------------------------------------------------'
echo
echo Configure date................ : $BUILD_DATE
echo Package version............... : $PACKAGE-$VERSION
echo Build architecture............ : $BUILD_ARCH
echo JVM........................... : $JAVA
echo Java compiler................. : $JAVAC
echo Jar tool...................... : $JAR
echo Ant tool...................... : $ANT
echo " "
echo Configure complete, please proceed with:
echo " - 'make'           builds sdk and app"
echo " - 'make uartsdk'   builds sdk only"
echo " - 'make app'       builds app only"
echo " - 'make D=1'       builds sdk and app with extra logs enabled in sdk"
echo " - 'make javadoc'   generates javadocs for sdk"
echo " - 'make install'   installs the progstm32 app,sdk,manpage"
echo
])

AX_SUMMARIZE_ENV
AX_SUMMARIZE_CONFIG

