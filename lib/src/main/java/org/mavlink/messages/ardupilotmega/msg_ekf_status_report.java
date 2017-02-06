/**
 * Generated class : msg_ekf_status_report
 * DO NOT MODIFY!
 **/
package org.mavlink.messages.ardupilotmega;

import org.mavlink.IMAVLinkCRC;
import org.mavlink.MAVLinkCRC;
import org.mavlink.io.LittleEndianDataInputStream;
import org.mavlink.io.LittleEndianDataOutputStream;
import org.mavlink.messages.MAVLinkMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class msg_ekf_status_report
 * EKF Status message including flags and variances
 **/
public class msg_ekf_status_report extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_EKF_STATUS_REPORT = 193;
    private static final long serialVersionUID = MAVLINK_MSG_ID_EKF_STATUS_REPORT;
    /**
     * Velocity variance
     */
    public float velocity_variance;
    /**
     * Horizontal Position variance
     */
    public float pos_horiz_variance;
    /**
     * Vertical Position variance
     */
    public float pos_vert_variance;
    /**
     * Compass variance
     */
    public float compass_variance;
    /**
     * Terrain Altitude variance
     */
    public float terrain_alt_variance;
    /**
     * Flags
     */
    public int flags;

    public msg_ekf_status_report() {
        this(1, 1);
    }

    public msg_ekf_status_report(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_EKF_STATUS_REPORT;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 22;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        velocity_variance = (float) dis.readFloat();
        pos_horiz_variance = (float) dis.readFloat();
        pos_vert_variance = (float) dis.readFloat();
        compass_variance = (float) dis.readFloat();
        terrain_alt_variance = (float) dis.readFloat();
        flags = (int) dis.readUnsignedShort() & 0x00FFFF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 22];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeFloat(velocity_variance);
        dos.writeFloat(pos_horiz_variance);
        dos.writeFloat(pos_vert_variance);
        dos.writeFloat(compass_variance);
        dos.writeFloat(terrain_alt_variance);
        dos.writeShort(flags & 0x00FFFF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 22);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[28] = crcl;
        buffer[29] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_EKF_STATUS_REPORT : " + "  velocity_variance=" + velocity_variance + "  pos_horiz_variance=" + pos_horiz_variance + "  pos_vert_variance=" + pos_vert_variance + "  compass_variance=" + compass_variance + "  terrain_alt_variance=" + terrain_alt_variance + "  flags=" + flags;
    }
}
