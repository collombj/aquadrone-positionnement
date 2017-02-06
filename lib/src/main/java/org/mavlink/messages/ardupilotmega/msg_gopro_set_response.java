/**
 * Generated class : msg_gopro_set_response
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
 * Class msg_gopro_set_response
 * Response from a GOPRO_COMMAND set request
 **/
public class msg_gopro_set_response extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_GOPRO_SET_RESPONSE = 219;
    private static final long serialVersionUID = MAVLINK_MSG_ID_GOPRO_SET_RESPONSE;
    /**
     * Command ID
     */
    public int cmd_id;
    /**
     * Status
     */
    public int status;

    public msg_gopro_set_response() {
        this(1, 1);
    }

    public msg_gopro_set_response(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_GOPRO_SET_RESPONSE;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 2;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        cmd_id = (int) dis.readUnsignedByte() & 0x00FF;
        status = (int) dis.readUnsignedByte() & 0x00FF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 2];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeByte(cmd_id & 0x00FF);
        dos.writeByte(status & 0x00FF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 2);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[8] = crcl;
        buffer[9] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_GOPRO_SET_RESPONSE : " + "  cmd_id=" + cmd_id + "  status=" + status;
    }
}
