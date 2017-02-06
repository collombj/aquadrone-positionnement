/**
 * Generated class : MAV_REMOTE_LOG_DATA_BLOCK_COMMANDS
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;

/**
 * Interface MAV_REMOTE_LOG_DATA_BLOCK_COMMANDS
 * Special ACK block numbers control activation of dataflash log streaming
 **/
public interface MAV_REMOTE_LOG_DATA_BLOCK_COMMANDS {
    /**
     * UAV to stop sending DataFlash blocks
     */
    public final static int MAV_REMOTE_LOG_DATA_BLOCK_STOP = 2147483645;
    /**
     * UAV to start sending DataFlash blocks
     */
    public final static int MAV_REMOTE_LOG_DATA_BLOCK_START = 2147483646;
}
