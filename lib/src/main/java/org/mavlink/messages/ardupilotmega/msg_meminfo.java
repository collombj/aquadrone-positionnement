/**
 * Generated class : msg_meminfo
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
 * Class msg_meminfo
 * state of APM memory
 **/
public class msg_meminfo extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_MEMINFO = 152;
    private static final long serialVersionUID = MAVLINK_MSG_ID_MEMINFO;
    /**
     * free memory (32 bit)
     */
    public long freemem32;
    /**
     * heap top
     */
    public int brkval;
    /**
     * free memory
     */
    public int freemem;

    public msg_meminfo() {
        this(1, 1);
    }

    public msg_meminfo(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_MEMINFO;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 8;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        freemem32 = (int) dis.readInt() & 0x00FFFFFFFF;
        brkval = (int) dis.readUnsignedShort() & 0x00FFFF;
        freemem = (int) dis.readUnsignedShort() & 0x00FFFF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 8];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeInt((int) (freemem32 & 0x00FFFFFFFF));
        dos.writeShort(brkval & 0x00FFFF);
        dos.writeShort(freemem & 0x00FFFF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 8);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[14] = crcl;
        buffer[15] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_MEMINFO : " + "  freemem32=" + freemem32 + "  brkval=" + brkval + "  freemem=" + freemem;
    }
}
