/**
 * Generated class : GOPRO_CAPTURE_MODE
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;

/**
 * Interface GOPRO_CAPTURE_MODE
 **/
public interface GOPRO_CAPTURE_MODE {
    /**
     * Video mode
     */
    public final static int GOPRO_CAPTURE_MODE_VIDEO = 0;
    /**
     * Photo mode
     */
    public final static int GOPRO_CAPTURE_MODE_PHOTO = 1;
    /**
     * Burst mode, hero 3+ only
     */
    public final static int GOPRO_CAPTURE_MODE_BURST = 2;
    /**
     * Time lapse mode, hero 3+ only
     */
    public final static int GOPRO_CAPTURE_MODE_TIME_LAPSE = 3;
    /**
     * Multi shot mode, hero 4 only
     */
    public final static int GOPRO_CAPTURE_MODE_MULTI_SHOT = 4;
    /**
     * Playback mode, hero 4 only, silver only except when LCD or HDMI is connected to black
     */
    public final static int GOPRO_CAPTURE_MODE_PLAYBACK = 5;
    /**
     * Playback mode, hero 4 only
     */
    public final static int GOPRO_CAPTURE_MODE_SETUP = 6;
    /**
     * Mode not yet known
     */
    public final static int GOPRO_CAPTURE_MODE_UNKNOWN = 255;
}
