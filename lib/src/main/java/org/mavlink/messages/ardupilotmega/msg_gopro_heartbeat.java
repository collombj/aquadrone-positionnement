/**
 * Generated class : msg_gopro_heartbeat
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
 * Class msg_gopro_heartbeat
 * Heartbeat from a HeroBus attached GoPro
 **/
public class msg_gopro_heartbeat extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_GOPRO_HEARTBEAT = 215;
    private static final long serialVersionUID = MAVLINK_MSG_ID_GOPRO_HEARTBEAT;
    /**
     * Status
     */
    public int status;
    /**
     * Current capture mode
     */
    public int capture_mode;
    /**
     * additional status bits
     */
    public int flags;

    public msg_gopro_heartbeat() {
        this(1, 1);
    }

    public msg_gopro_heartbeat(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_GOPRO_HEARTBEAT;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 3;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        status = (int) dis.readUnsignedByte() & 0x00FF;
        capture_mode = (int) dis.readUnsignedByte() & 0x00FF;
        flags = (int) dis.readUnsignedByte() & 0x00FF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 3];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeByte(status & 0x00FF);
        dos.writeByte(capture_mode & 0x00FF);
        dos.writeByte(flags & 0x00FF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 3);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[9] = crcl;
        buffer[10] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_GOPRO_HEARTBEAT : " + "  status=" + status + "  capture_mode=" + capture_mode + "  flags=" + flags;
    }
}
