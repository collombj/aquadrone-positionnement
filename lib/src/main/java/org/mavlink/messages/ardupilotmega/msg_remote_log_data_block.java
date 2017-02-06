/**
 * Generated class : msg_remote_log_data_block
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
 * Class msg_remote_log_data_block
 * Send a block of log data to remote location
 **/
public class msg_remote_log_data_block extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK = 184;
    private static final long serialVersionUID = MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK;
    /**
     * log data block sequence number
     */
    public long seqno;
    /**
     * System ID
     */
    public int target_system;
    /**
     * Component ID
     */
    public int target_component;
    /**
     * log data block
     */
    public int[] data = new int[200];

    public msg_remote_log_data_block() {
        this(1, 1);
    }

    public msg_remote_log_data_block(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 206;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        seqno = (int) dis.readInt() & 0x00FFFFFFFF;
        target_system = (int) dis.readUnsignedByte() & 0x00FF;
        target_component = (int) dis.readUnsignedByte() & 0x00FF;
        for (int i = 0; i < 200; i++) {
            data[i] = (int) dis.readUnsignedByte() & 0x00FF;
        }
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 206];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeInt((int) (seqno & 0x00FFFFFFFF));
        dos.writeByte(target_system & 0x00FF);
        dos.writeByte(target_component & 0x00FF);
        for (int i = 0; i < 200; i++) {
            dos.writeByte(data[i] & 0x00FF);
        }
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 206);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[212] = crcl;
        buffer[213] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK : " + "  seqno=" + seqno + "  target_system=" + target_system + "  target_component=" + target_component + "  data=" + data;
    }
}
