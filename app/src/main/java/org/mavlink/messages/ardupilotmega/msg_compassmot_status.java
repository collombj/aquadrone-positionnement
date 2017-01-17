/**
 * Generated class : msg_compassmot_status
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
 * Class msg_compassmot_status
 * Status of compassmot calibration
 **/
public class msg_compassmot_status extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_COMPASSMOT_STATUS = 177;
    private static final long serialVersionUID = MAVLINK_MSG_ID_COMPASSMOT_STATUS;
    /**
     * current (amps)
     */
    public float current;
    /**
     * Motor Compensation X
     */
    public float CompensationX;
    /**
     * Motor Compensation Y
     */
    public float CompensationY;
    /**
     * Motor Compensation Z
     */
    public float CompensationZ;
    /**
     * throttle (percent*10)
     */
    public int throttle;
    /**
     * interference (percent)
     */
    public int interference;

    public msg_compassmot_status() {
        this(1, 1);
    }

    public msg_compassmot_status(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_COMPASSMOT_STATUS;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 20;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        current = (float) dis.readFloat();
        CompensationX = (float) dis.readFloat();
        CompensationY = (float) dis.readFloat();
        CompensationZ = (float) dis.readFloat();
        throttle = (int) dis.readUnsignedShort() & 0x00FFFF;
        interference = (int) dis.readUnsignedShort() & 0x00FFFF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 20];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeFloat(current);
        dos.writeFloat(CompensationX);
        dos.writeFloat(CompensationY);
        dos.writeFloat(CompensationZ);
        dos.writeShort(throttle & 0x00FFFF);
        dos.writeShort(interference & 0x00FFFF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 20);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[26] = crcl;
        buffer[27] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_COMPASSMOT_STATUS : " + "  current=" + current + "  CompensationX=" + CompensationX + "  CompensationY=" + CompensationY + "  CompensationZ=" + CompensationZ + "  throttle=" + throttle + "  interference=" + interference;
    }
}
