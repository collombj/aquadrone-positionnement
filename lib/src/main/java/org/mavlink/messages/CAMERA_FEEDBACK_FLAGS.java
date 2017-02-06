/**
 * Generated class : CAMERA_FEEDBACK_FLAGS
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;

/**
 * Interface CAMERA_FEEDBACK_FLAGS
 **/
public interface CAMERA_FEEDBACK_FLAGS {
    /**
     * Shooting photos, not video
     */
    public final static int CAMERA_FEEDBACK_PHOTO = 0;
    /**
     * Shooting video, not stills
     */
    public final static int CAMERA_FEEDBACK_VIDEO = 1;
    /**
     * Unable to achieve requested exposure (e.g. shutter speed too low)
     */
    public final static int CAMERA_FEEDBACK_BADEXPOSURE = 2;
    /**
     * Closed loop feedback from camera, we know for sure it has successfully taken a picture
     */
    public final static int CAMERA_FEEDBACK_CLOSEDLOOP = 3;
    /**
     * Open loop camera, an image trigger has been requested but we can't know for sure it has successfully taken a picture
     */
    public final static int CAMERA_FEEDBACK_OPENLOOP = 4;
}
