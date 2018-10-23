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

package flash.stm32.core;

/**
 * <p>
 * The interface ICmdProgressListener should be implemented by a class who wish
 * to know how many bytes of data has been sent or received when communicating
 * with stm32.
 * </p>
 * 
 * <p>
 * The graphical user interface applications may want to show progress for
 * example using a progress bar to inform user about how much data have been
 * sent to receiver end. Such applications can use this interface for this
 * purpose.
 * </p>
 * 
 * @author Rishi Gupta
 */
public interface ICmdProgressListener {

    /**
     * <p>
     * The class implementing this interface is expected to override
     * onDataWriteProgressUpdate() method. This method gets called whenever a block
     * is sent to stm32.
     * </p>
     * 
     * <p>
     * This method should return as early as possible. Application might schedule
     * GUI update for future.
     * </p>
     * 
     * @param totalBytesSentTillNow
     *            total number of data bytes that has been sent to stm32 till now
     * @param totalBytesToWrite
     *            total number of bytes to be sent to stm32
     */
    public abstract void onDataWriteProgressUpdate(int totalBytesSentTillNow, int totalBytesToWrite);

    /**
     * <p>
     * The class implementing this interface is expected to override
     * onDataReadProgressUpdate() method. This method gets called whenever a block
     * of data has been read from stm32.
     * </p>
     * 
     * <p>
     * This method should return as early as possible. Application might schedule
     * GUI update for future.
     * </p>
     * 
     * @param totalBytesReadTillNow
     *            total number of data bytes that has been read from stm32 till now
     * @param totalNumBytesToRead
     *            total number of bytes to be read from stm32
     */
    public abstract void onDataReadProgressUpdate(int totalBytesReadTillNow, int totalNumBytesToRead);
}
