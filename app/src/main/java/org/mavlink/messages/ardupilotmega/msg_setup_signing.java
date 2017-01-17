/**
 * Generated class : msg_setup_signing
 * DO NOT MODIFY!
 **/
package org.mavlink.messages.ardupilotmega;

import org.mavlink.io.LittleEndianDataInputStream;
import org.mavlink.messages.MAVLinkMessage;

import java.io.IOException;

/**
 * Class msg_setup_signing
 * Setup a MAVLink2 signing key. If called with secret_key of all zero and zero initial_timestamp will disable signing
 **/
public class msg_setup_signing extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_SETUP_SIGNING = 256;
    private static final long serialVersionUID = MAVLINK_MSG_ID_SETUP_SIGNING;
    /**
     * initial timestamp
     */
    public long initial_timestamp;
    /**
     * system id of the target
     */
    public int target_system;
    /**
     * component ID of the target
     */
    public int target_component;
    /**
     * signing key
     */
    public int[] secret_key = new int[32];

    public msg_setup_signing() {
        this(1, 1);
    }

    public msg_setup_signing(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_SETUP_SIGNING;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 42;
    }

    @Override
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        // TODO
    }

    @Override
    public byte[] encode() throws IOException {
        // TODO
        return new byte[0];
    }
}
