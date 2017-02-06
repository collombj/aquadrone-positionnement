/**
 * Generated class : GOPRO_HEARTBEAT_STATUS
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;

/**
 * Interface GOPRO_HEARTBEAT_STATUS
 **/
public interface GOPRO_HEARTBEAT_STATUS {
    /**
     * No GoPro connected
     */
    public final static int GOPRO_HEARTBEAT_STATUS_DISCONNECTED = 0;
    /**
     * The detected GoPro is not HeroBus compatible
     */
    public final static int GOPRO_HEARTBEAT_STATUS_INCOMPATIBLE = 1;
    /**
     * A HeroBus compatible GoPro is connected
     */
    public final static int GOPRO_HEARTBEAT_STATUS_CONNECTED = 2;
    /**
     * An unrecoverable error was encountered with the connected GoPro, it may require a power cycle
     */
    public final static int GOPRO_HEARTBEAT_STATUS_ERROR = 3;
}
