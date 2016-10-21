/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2002-2015 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified 
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Modifications:
 *
 * 2007 May 21: Better logging around loading of the shared library, format
 *              code, and eliminate m_loaded member. - dj@opennms.org
 * 2003 Jan 31: Cleaned up some unused imports.
 *
 * Orignal code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact: 
 *      OpenNMS Licensing       <license@opennms.org>
 *      http://www.opennms.org/
 *      http://www.opennms.com/
 */
package org.opennms.netmgt.rrd.rrdtool;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This is a singleton class which provides an interface through which RRD
 * (Round Robin Database) functions (rrd_create(), rrd_update(), and others) can
 * be accessed from Java code.
 * 
 * <pre>
 * The native method 'launch()' takes a single argument which is a 
 *  RRD command string of similar format to what RRDtool takes.  Please
 *  note the following examples:
 *  
 *  	&quot;create test.rrd --start N DS:ifOctetsIn:COUNTER:600:U:U \
 *        	RRA:AVERAGE:0.5:1:24&quot;
 * 
 *  	&quot;update test.rrd --template:ifOctetsIn N:123456789&quot;
 *  
 *  Refer to www.rrdtool.org for additional examples and information on 
 *  the format of rrdtool command strings.
 *  
 *  Currently only 'create', 'update', and 'fetch' commands are supported.
 * </pre>
 * 
 * @author <A HREF="mailto:mike@opennms.org">Mike </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * 
 * @version 1.1.1.1
 * 
 */
public final class Interface {
    private static final String LIBRARY_NAME = "jrrd";
    private static final String PROPERTY_NAME = "opennms.library.jrrd";

    /**
     * The singleton instance of the interface
     */
    private static Interface s_singleton = null;

    /**
     * Native method implemented in C which provides an interface to the
     * lower-level RRD functions
     * 
     * WARNING: The RRD C api (rrd_update(), rrd_create(), etc...) relies on
     * getopt() & therefore is not thread safe. This method is therefore
     * synchronized in order to prevent more than one thread access at a time.
     * 
     * @param cmd
     *            RRDtool style command string to be executed. Currently
     *            supported RRD commands are: 'create' - calls rrd_create()
     *            'update' - calls rrd_update() 'fetch' - calls rrd_fetch()
     * 
     * @return array of Java String objects
     * 
     * In the case of rrd_fetch() the returned String array has the following
     * characteristics:
     * 
     * String[0] = error text if command failed or NULL if successful String[1] =
     * for 'fetch' cmd: data source names String[2..n] = for 'fetch' cmd:
     * retrieved data rows for each interval between start and end parms
     */
    public static synchronized native String[] launch(String cmd);

    /**
     * Load the jrrd library and create the singleton instance of the interface.
     * 
     * @throws SecurityException
     *             if we don't have permission to load the library
     * @throws UnsatisfiedLinkError
     *             if the library doesn't exist
     */
    public static synchronized void init() throws SecurityException, UnsatisfiedLinkError {
        if (isLoaded()) {
            // init already called - return
            // to reload, reload() will need to be called
            return;
        }

        setInstance(new Interface());
    }

    private static boolean isLoaded() {
        return s_singleton != null;
    }

    /**
     * Reload the jrrd library and create the singleton instance of the
     * interface.
     * 
     * @throws SecurityException
     *             if we don't have permission to load the library
     * @throws UnsatisfiedLinkError
     *             if the library doesn't exist
     */
    public static synchronized void reload() throws SecurityException, UnsatisfiedLinkError {
        setInstance(null);

        init();
    }

    /**
     * Constructor. Responsible for loading the jrrd shared/dynamic link library
     * which contains the implementation of the 'launch()' native method.
     * 
     * @throws SecurityException
     *             if we don't have permission to load the library
     * @throws UnsatisfiedLinkError
     *             if the library doesn't exist
     */
    private Interface() throws SecurityException, UnsatisfiedLinkError {
        String jniPath = System.getProperty(PROPERTY_NAME);
        try {
            debug("System property '" + PROPERTY_NAME + "' set to '" + System.getProperty(PROPERTY_NAME) + ".  Attempting to load " + LIBRARY_NAME + " library from this location.");
            System.load(jniPath);
        } catch (final Throwable t) {
            debug("System property '" + PROPERTY_NAME + "' not set.  Attempting to find library.");
            System.loadLibrary(LIBRARY_NAME);
        }
        info("Successfully loaded " + LIBRARY_NAME + " library.");
    }

    private static void loadLibrary() {
        final Set<String> searchPaths = new LinkedHashSet<String>();

        if (System.getProperty("java.library.path") != null) {
            for (final String entry : System.getProperty("java.library.path").split(File.pathSeparator)) {
                searchPaths.add(entry);
            }
        }

        for (final String entry : new String[] {
                "/usr/lib64/jni",
                "/usr/lib64",
                "/usr/local/lib64",
                "/usr/lib/jni",
                "/usr/lib",
                "/usr/local/lib"
        }) {
            searchPaths.add(entry);
        }

        for (final String path : searchPaths) {
            for (final String prefix : new String[] { "", "lib" }) {
                for (final String suffix : new String[] { ".jnilib", ".dylib", ".so" }) {
                    final File f = new File(path + File.separator + prefix + LIBRARY_NAME + suffix);
                    if (f.exists()) {
                        try {
                            System.load(f.getCanonicalPath());
                            return;
                        } catch (final Throwable t) {
                            // failed, keep looping and hope for a match
                        }
                    }
                }
            }
        }
        debug("Unable to locate '" + LIBRARY_NAME + "' in common paths.  Attempting System.loadLibrary() as a last resort.");
        System.loadLibrary(LIBRARY_NAME);
    }

    /**
     * Return the singleton instance of this class.
     * 
     * @return The current instance.
     * 
     * @throws java.lang.IllegalStateException
     *             Thrown if the interface has not yet been initialized.
     */
    public static synchronized Interface getInstance() {
        assertState(isLoaded(), "The RRD JNI interface has not been initialized");

        return s_singleton;
    }

    public static synchronized void setInstance(Interface instance) {
        s_singleton = instance;
    }

    public static void debug(String msg) {
        System.err.println("[DEBUG] "+msg);
    }

    public static void info(String msg) {
        System.err.println("[INFO] "+msg);
    }

    public static void assertState(boolean check, String msg) {
        if (!check) {
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Debug purposes only
     */
    public static void main(String[] argv) {
        try {
            // initialize the interface
            Interface.reload();

            // build create command
            // Build RRD create command prefix
            String filename = argv[0];
            System.out.println("filename=" + filename);
            String cmd = "create \"" + filename + "\" --start N" + " --step 900 DS:test:COUNTER:900:0:100 RRA:MIN:0.5:1:1000";

            // issue rrd command
            System.out.println("issuing RRD cmd: " + cmd);
            Interface.launch(cmd);
            System.out.println("command completed.");
        } catch (Throwable t) {
            System.err.println("unexpected error, reason: " + t);
            t.printStackTrace();
        }
    }
}
