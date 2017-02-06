/**
 * Generated class : GIMBAL_AXIS_CALIBRATION_REQUIRED
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;

/**
 * Interface GIMBAL_AXIS_CALIBRATION_REQUIRED
 **/
public interface GIMBAL_AXIS_CALIBRATION_REQUIRED {
    /**
     * Whether or not this axis requires calibration is unknown at this time
     */
    public final static int GIMBAL_AXIS_CALIBRATION_REQUIRED_UNKNOWN = 0;
    /**
     * This axis requires calibration
     */
    public final static int GIMBAL_AXIS_CALIBRATION_REQUIRED_TRUE = 1;
    /**
     * This axis does not require calibration
     */
    public final static int GIMBAL_AXIS_CALIBRATION_REQUIRED_FALSE = 2;
}
