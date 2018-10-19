/* 
 * This file is part of progstm32.
 * 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 * 
 * The progstm32 is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 * 
 * The progstm32 is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation,Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package flash.stm32.core.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * <p>
 * Central place to get system specific properties.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class SystemProperties {

    private String jtmpdir;
    private final SecurityManager securityManager;

    /**
     * <p>
     * Allocates a new SystemProperties object.
     * </p>
     */
    public SystemProperties() {
        securityManager = System.getSecurityManager();
    }

    /**
     * <p>
     * Gives system/user temp directory as returned by JVM.
     * </p>
     * 
     * @return tmp directory for Java operations.
     * @throws SecurityException
     *             if security manager does not allow access to system property.
     */
    public String getJavaIOTmpDir() throws SecurityException {

        if (this.jtmpdir != null) {
            return jtmpdir;
        }

        if (securityManager == null) {
            return System.getProperty("java.io.tmpdir");
        } else {
            return (String) AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("java.io.tmpdir");
                }
            });
        }
    }
}
