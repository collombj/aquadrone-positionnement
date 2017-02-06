/**
 * Generated class : msg_gopro_set_request
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
 * Class msg_gopro_set_request
 * Request to set a GOPRO_COMMAND with a desired
 **/
public class msg_gopro_set_request extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_GOPRO_SET_REQUEST = 218;
    private static final long serialVersionUID = MAVLINK_MSG_ID_GOPRO_SET_REQUEST;
    /**
     * System ID
     */
    public int target_system;
    /**
     * Component ID
     */
    public int target_component;
    /**
     * Command ID
     */
    public int cmd_id;
    /**
     * Value
     */
    public int[] value = new int[4];

    public msg_gopro_set_request() {
        this(1, 1);
    }

    public msg_gopro_set_request(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_GOPRO_SET_REQUEST;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 7;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        target_system = (int) dis.readUnsignedByte() & 0x00FF;
        target_component = (int) dis.readUnsignedByte() & 0x00FF;
        cmd_id = (int) dis.readUnsignedByte() & 0x00FF;
        for (int i = 0; i < 4; i++) {
            value[i] = (int) dis.readUnsignedByte() & 0x00FF;
        }
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 7];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeByte(target_system & 0x00FF);
        dos.writeByte(target_component & 0x00FF);
        dos.writeByte(cmd_id & 0x00FF);
        for (int i = 0; i < 4; i++) {
            dos.writeByte(value[i] & 0x00FF);
        }
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 7);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[13] = crcl;
        buffer[14] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_GOPRO_SET_REQUEST : " + "  target_system=" + target_system + "  target_component=" + target_component + "  cmd_id=" + cmd_id + "  value=" + value;
    }
}
