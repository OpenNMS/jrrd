#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.59)
AC_INIT([jrrd], [1.0], [http://www.opennms.org])
AC_CONFIG_MACRO_DIR([m4])
AC_CONFIG_HEADER([config.h])

# Checks for programs.
AC_PROG_CC
AC_PROG_INSTALL
ONMS_FIND_RRDHOME
ONMS_CHECK_JDK([1.5])

PRINT_VAR(JAVA_HOME)
PRINT_VAR(JAVA)
PRINT_VAR(JAVAC)
PRINT_VAR(JAVAH)
PRINT_VAR(JAR)
PRINT_VAR(JNI_INCLUDES)


CPPFLAGS="-I$RRDHOME/include $CPPFLAGS"
LDFLAGS="$LDFLAGS -L$RRDHOME/lib"

# Checks for libraries.
AC_SEARCH_LIBS([rrd_create], [rrd_th rrd], [], [AC_MSG_ERROR([cannot find rrd library with LDFLAGS = "$LDFLAGS"])])

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([malloc.h stdlib.h string.h unistd.h])
AC_CHECK_HEADER([rrd.h], [], [AC_MSG_ERROR([cannot find rrd.h header with CPPFLAGS="$CPPFLAGS"])])

# Checks for typedefs, structures, and compiler characteristics.
AC_C_CONST

# Checks for library functions.
AC_FUNC_MALLOC

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
