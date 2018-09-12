/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

import flash.stm32.core.internal.SystemProperties;

/**
 * <p>
 * Base class representing a communication interface.
 * </p>
 * 
 * @author Rishi Gupta
 */
public class CommunicationInterface {

    public final SystemProperties sprop;

    public CommunicationInterface() {

        sprop = new SystemProperties();
    }
}
