/**
 * Generated class : msg_battery2
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
 * Class msg_battery2
 * 2nd Battery status
 **/
public class msg_battery2 extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_BATTERY2 = 181;
    private static final long serialVersionUID = MAVLINK_MSG_ID_BATTERY2;
    /**
     * voltage in millivolts
     */
    public int voltage;
    /**
     * Battery current, in 10*milliamperes (1 = 10 milliampere), -1: autopilot does not measure the current
     */
    public int current_battery;

    public msg_battery2() {
        this(1, 1);
    }

    public msg_battery2(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_BATTERY2;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 4;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        voltage = (int) dis.readUnsignedShort() & 0x00FFFF;
        current_battery = (int) dis.readShort();
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 4];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeShort(voltage & 0x00FFFF);
        dos.writeShort(current_battery & 0x00FFFF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 4);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[10] = crcl;
        buffer[11] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_BATTERY2 : " + "  voltage=" + voltage + "  current_battery=" + current_battery;
    }
}
