/**
 * Generated class : EKF_STATUS_FLAGS
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;

/**
 * Interface EKF_STATUS_FLAGS
 * Flags in EKF_STATUS message
 **/
public interface EKF_STATUS_FLAGS {
    /**
     * set if EKF's attitude estimate is good
     */
    public final static int EKF_ATTITUDE = 1;
    /**
     * set if EKF's horizontal velocity estimate is good
     */
    public final static int EKF_VELOCITY_HORIZ = 2;
    /**
     * set if EKF's vertical velocity estimate is good
     */
    public final static int EKF_VELOCITY_VERT = 4;
    /**
     * set if EKF's horizontal position (relative) estimate is good
     */
    public final static int EKF_POS_HORIZ_REL = 8;
    /**
     * set if EKF's horizontal position (absolute) estimate is good
     */
    public final static int EKF_POS_HORIZ_ABS = 16;
    /**
     * set if EKF's vertical position (absolute) estimate is good
     */
    public final static int EKF_POS_VERT_ABS = 32;
    /**
     * set if EKF's vertical position (above ground) estimate is good
     */
    public final static int EKF_POS_VERT_AGL = 64;
    /**
     * EKF is in constant position mode and does not know it's absolute or relative position
     */
    public final static int EKF_CONST_POS_MODE = 128;
    /**
     * set if EKF's predicted horizontal position (relative) estimate is good
     */
    public final static int EKF_PRED_POS_HORIZ_REL = 256;
    /**
     * set if EKF's predicted horizontal position (absolute) estimate is good
     */
    public final static int EKF_PRED_POS_HORIZ_ABS = 512;
}
