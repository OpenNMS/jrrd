project(jrrd)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -I${CMAKE_CURRENT_SOURCE_DIR} -I${CMAKE_CURRENT_BINARY_DIR}")
set(CMAKE_MODULE_PATH "${CMAKE_SOURCE_DIR}/m4")

include(CheckIncludeFiles)
include(CheckTypeSize)
include(CMakeJavaInformation)
include(CMakeDetermineJavaCompiler)
include(TestBigEndian)
include(CPack)

set(CPACK_PACKAGE_DESCRIPTION_SUMMARY "Java RRDtool Interface")
set(CPACK_PACKAGE_VENDOR "The OpenNMS Group, Inc.")
set(CPACK_PACKAGE_DESCRIPTION_FILE "${CMAKE_CURRENT_SOURCE_DIR}/README")
set(CPACK_RESOURCE_FILE_LICENSE "${CMAKE_CURRENT_SOURCE_DIR}/COPYRIGHT")
set(CPACK_PACKAGE_VERSION_MAJOR "1")
set(CPACK_PACKAGE_VERSION_MINOR "0")
set(CPACK_PACKAGE_VERSION_PATCH "6")

find_package(Java REQUIRED)
find_package(JNI REQUIRED)
find_package(Rrdtool REQUIRED)
get_filename_component(JAVABIN_PATH ${JAVA_COMPILE} PATH)

include_directories(${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH}/win32 ${JAVA_INCLUDE_PATH}/linux ${RRDTOOL_INCLUDE_DIR})

check_include_files(getopt.h HAVE_GETOPT_H)
check_include_files(malloc.h HAVE_MALLOC_H)
check_include_files(stdlib.h HAVE_STDLIB_H)
check_include_files(string.h HAVE_STRING_H)
check_include_files(unistd.h HAVE_UNISTD_H)

set(JAVA_SOURCE_FILES org/opennms/netmgt/rrd/rrdtool/Interface.java)
set(JAVA_CLASS_FILES ${CMAKE_CURRENT_BINARY_DIR}/org/opennms/netmgt/rrd/rrdtool/Interface.class)

add_custom_command(OUTPUT ${JAVA_CLASS_FILES} COMMAND ${JAVABIN_PATH}/javac -d ${CMAKE_CURRENT_BINARY_DIR} -classpath ${CMAKE_CURRENT_BINARY_DIR} ${JAVA_SOURCE_FILES} DEPENDS ${JAVA_SOURCE_FILES} WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})

add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/org_opennms_netmgt_rrd_rrdtool_Interface.h COMMAND ${JAVABIN_PATH}/javah -d ${CMAKE_CURRENT_BINARY_DIR} -classpath ${CMAKE_CURRENT_BINARY_DIR} org.opennms.netmgt.rrd.rrdtool.Interface DEPENDS ${JAVA_CLASS_FILES} jrrd.jar WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR})

configure_file(config.h.cmake ${CMAKE_CURRENT_BINARY_DIR}/config.h)
include_directories(${JAVA_INCLUDE_PATH})

add_library(jrrd MODULE rrd_jinterface.c ${CMAKE_CURRENT_BINARY_DIR}/org_opennms_netmgt_rrd_rrdtool_Interface.h)

if(APPLE)
        set_target_properties(jrrd PROPERTIES SUFFIX .jnilib)
endif(APPLE)

target_link_libraries(jrrd ${RRDTOOL_LIBRARIES})

add_custom_target(jrrd.jar ${JAVA_ARCHIVE} -cvf jrrd.jar org DEPENDS ${JAVA_CLASS_FILES} WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR})

