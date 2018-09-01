/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart.internal;

public final class SystemProperties {
	
    private final SecurityManager securityManager;
	
    /** 
     * <p>Allocates a new SystemProperties object.</p>
     */
    public SystemProperties() {
        securityManager = System.getSecurityManager();
    }

}
