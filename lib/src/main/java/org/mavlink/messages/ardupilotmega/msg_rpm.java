/**
 * Generated class : msg_rpm
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
 * Class msg_rpm
 * RPM sensor output
 **/
public class msg_rpm extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_RPM = 226;
    private static final long serialVersionUID = MAVLINK_MSG_ID_RPM;
    /**
     * RPM Sensor1
     */
    public float rpm1;
    /**
     * RPM Sensor2
     */
    public float rpm2;

    public msg_rpm() {
        this(1, 1);
    }

    public msg_rpm(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_RPM;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 8;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        rpm1 = (float) dis.readFloat();
        rpm2 = (float) dis.readFloat();
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
        dos.writeFloat(rpm1);
        dos.writeFloat(rpm2);
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
        return "MAVLINK_MSG_ID_RPM : " + "  rpm1=" + rpm1 + "  rpm2=" + rpm2;
    }
}
