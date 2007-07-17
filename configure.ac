#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.59)
AC_INIT([jrrd], [1.0], [http://www.opennms.org])

RELEASE=1
AC_SUBST([RELEASE])

AC_CONFIG_MACRO_DIR([m4])
AC_CONFIG_HEADER([config.h])
AM_INIT_AUTOMAKE([1.9 -Wall -Werror foreign])
AC_ENABLE_STATIC([no])

# Checks for programs.
AC_PROG_CC
AC_PROG_LN_S
AC_PROG_LIBTOOL
AC_PROG_INSTALL
ONMS_FIND_RRDHOME
ONMS_CHECK_JDK([1.4])

AM_RPM_INIT
AM_CONDITIONAL(MAKE_RPMS, test x$make_rpms = xtrue)

PRINT_VAR(JAVA_HOME)
PRINT_VAR(JAVA)
PRINT_VAR(JAVAC)
PRINT_VAR(JAVAH)
PRINT_VAR(JAR)
PRINT_VAR(JNI_INCLUDES)

CPPFLAGS="-I$RRDHOME/include $JNI_INCLUDES $CPPFLAGS"
LDFLAGS="$LDFLAGS -L$RRDHOME/lib"

# Checks for libraries.
AC_SEARCH_LIBS([rrd_create], [rrd_th rrd], [], [AC_MSG_ERROR([cannot find rrd library with LDFLAGS = "$LDFLAGS"])])

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADER([rrd.h], [], [AC_MSG_ERROR([cannot find rrd.h header with CPPFLAGS="$CPPFLAGS"])])
AC_CHECK_HEADER([jni.h], [], [AC_MSG_ERROR([cannot find jni.h header with CPPFLAGS="$CPPFLAGS"])])
AC_CHECK_HEADERS([malloc.h stdlib.h string.h unistd.h rrd.h jni.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_C_CONST

# Checks for library functions.
AC_FUNC_MALLOC

AC_CONFIG_FILES([Makefile jrrd.spec])
AC_OUTPUT
