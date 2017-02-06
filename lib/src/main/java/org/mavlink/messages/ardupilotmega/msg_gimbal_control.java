/**
 * Generated class : msg_gimbal_control
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
 * Class msg_gimbal_control
 * Control message for rate gimbal
 **/
public class msg_gimbal_control extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_GIMBAL_CONTROL = 201;
    private static final long serialVersionUID = MAVLINK_MSG_ID_GIMBAL_CONTROL;
    /**
     * Demanded angular rate X (rad/s)
     */
    public float demanded_rate_x;
    /**
     * Demanded angular rate Y (rad/s)
     */
    public float demanded_rate_y;
    /**
     * Demanded angular rate Z (rad/s)
     */
    public float demanded_rate_z;
    /**
     * System ID
     */
    public int target_system;
    /**
     * Component ID
     */
    public int target_component;

    public msg_gimbal_control() {
        this(1, 1);
    }

    public msg_gimbal_control(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_GIMBAL_CONTROL;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 14;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        demanded_rate_x = (float) dis.readFloat();
        demanded_rate_y = (float) dis.readFloat();
        demanded_rate_z = (float) dis.readFloat();
        target_system = (int) dis.readUnsignedByte() & 0x00FF;
        target_component = (int) dis.readUnsignedByte() & 0x00FF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 14];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeFloat(demanded_rate_x);
        dos.writeFloat(demanded_rate_y);
        dos.writeFloat(demanded_rate_z);
        dos.writeByte(target_system & 0x00FF);
        dos.writeByte(target_component & 0x00FF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 14);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[20] = crcl;
        buffer[21] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_GIMBAL_CONTROL : " + "  demanded_rate_x=" + demanded_rate_x + "  demanded_rate_y=" + demanded_rate_y + "  demanded_rate_z=" + demanded_rate_z + "  target_system=" + target_system + "  target_component=" + target_component;
    }
}
