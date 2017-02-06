/**
 * Generated class : LED_CONTROL_PATTERN
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;

/**
 * Interface LED_CONTROL_PATTERN
 **/
public interface LED_CONTROL_PATTERN {
    /**
     * LED patterns off (return control to regular vehicle control)
     */
    public final static int LED_CONTROL_PATTERN_OFF = 0;
    /**
     * LEDs show pattern during firmware update
     */
    public final static int LED_CONTROL_PATTERN_FIRMWAREUPDATE = 1;
    /**
     * Custom Pattern using custom bytes fields
     */
    public final static int LED_CONTROL_PATTERN_CUSTOM = 255;
}
