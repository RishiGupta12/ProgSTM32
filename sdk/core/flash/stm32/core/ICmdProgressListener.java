/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

public interface ICmdProgressListener {

    public abstract void onDataWriteProgressUpdate(long numBlock, int percentOfBlocksSent);
    
    
}
