/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class SystemProperties {
	
	private String jtmpdir;
    private final SecurityManager securityManager;
	
    /** 
     * <p>Allocates a new SystemProperties object.</p>
     */
    public SystemProperties() {
        securityManager = System.getSecurityManager();
    }

    /** <p>Gives system/user temp directory as returned by JVM.</p>
     * 
     * @return tmp directory for Java operations.
     * @throws SecurityException if security manager does not allow access to system property.
     */
    public String getJavaIOTmpDir() throws SecurityException {

        if(this.jtmpdir != null) {
            return jtmpdir;
        }

        if(securityManager == null) {
            return System.getProperty("java.io.tmpdir");
        }
        else {
            return (String) AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("java.io.tmpdir");
                }
            });
        }
    }
}
