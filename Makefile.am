ACLOCAL_AMFLAGS = -I m4

lib_LTLIBRARIES = libjrrd.la
libjrrd_la_SOURCES = rrd_jinterface.c
libjrrd_la_LDFLAGS = -module

MAINTAINERCLEANFILES = aclocal.m4 config.guess config.sub configure depcomp install-sh ltmain.sh Makefile.in missing config.h.in *~

%.class: %.java
	$(JAVAC) $<

%.jar:
	$(JAR) cvf $@ $<

rrd_jinterface.c: org_opennms_netmgt_rrd_rrdtool_Interface.h

org_opennms_netmgt_rrd_rrdtool_Interface.h: org/opennms/netmgt/rrd/rrdtool/Interface.class
	$(JAVAH) -classpath . org.opennms.netmgt.rrd.rrdtool.Interface

jrrd.jar: org/opennms/netmgt/rrd/rrdtool/Interface.class





