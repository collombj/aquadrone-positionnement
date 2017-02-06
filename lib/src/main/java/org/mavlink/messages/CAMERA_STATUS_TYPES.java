/**
 * Generated class : CAMERA_STATUS_TYPES
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;

/**
 * Interface CAMERA_STATUS_TYPES
 **/
public interface CAMERA_STATUS_TYPES {
    /**
     * Camera heartbeat, announce camera component ID at 1hz
     */
    public final static int CAMERA_STATUS_TYPE_HEARTBEAT = 0;
    /**
     * Camera image triggered
     */
    public final static int CAMERA_STATUS_TYPE_TRIGGER = 1;
    /**
     * Camera connection lost
     */
    public final static int CAMERA_STATUS_TYPE_DISCONNECT = 2;
    /**
     * Camera unknown error
     */
    public final static int CAMERA_STATUS_TYPE_ERROR = 3;
    /**
     * Camera battery low. Parameter p1 shows reported voltage
     */
    public final static int CAMERA_STATUS_TYPE_LOWBATT = 4;
    /**
     * Camera storage low. Parameter p1 shows reported shots remaining
     */
    public final static int CAMERA_STATUS_TYPE_LOWSTORE = 5;
    /**
     * Camera storage low. Parameter p1 shows reported video minutes remaining
     */
    public final static int CAMERA_STATUS_TYPE_LOWSTOREV = 6;
}
