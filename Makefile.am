ACLOCAL_AMFLAGS = -I m4

classdir=classes
VPATH += classes

lib_LTLIBRARIES = libjrrd.la
libjrrd_la_SOURCES = rrd_jinterface.c
libjrrd_la_LDFLAGS = -module

MAINTAINERCLEANFILES = aclocal.m4 config.guess config.sub configure depcomp install-sh ltmain.sh Makefile.in missing config.h.in *~

.java.class:
	-mkdir -p $(classdir)
	$(JAVAC) -d $(classdir) $<

.jar:
	$(JAR) cvf $@ $<

rrd_jinterface.c: org_opennms_netmgt_rrd_rrdtool_Interface.h

org_opennms_netmgt_rrd_rrdtool_Interface.h: org/opennms/netmgt/rrd/rrdtool/Interface.class
	$(JAVAH) -classpath $(classdir) org.opennms.netmgt.rrd.rrdtool.Interface

jrrd.jar: org/opennms/netmgt/rrd/rrdtool/Interface.class





