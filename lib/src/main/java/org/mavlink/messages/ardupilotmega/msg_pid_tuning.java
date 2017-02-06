/**
 * Generated class : msg_pid_tuning
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
 * Class msg_pid_tuning
 * PID tuning information
 **/
public class msg_pid_tuning extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_PID_TUNING = 194;
    private static final long serialVersionUID = MAVLINK_MSG_ID_PID_TUNING;
    /**
     * desired rate (degrees/s)
     */
    public float desired;
    /**
     * achieved rate (degrees/s)
     */
    public float achieved;
    /**
     * FF component
     */
    public float FF;
    /**
     * P component
     */
    public float P;
    /**
     * I component
     */
    public float I;
    /**
     * D component
     */
    public float D;
    /**
     * axis
     */
    public int axis;

    public msg_pid_tuning() {
        this(1, 1);
    }

    public msg_pid_tuning(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_PID_TUNING;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 25;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        desired = (float) dis.readFloat();
        achieved = (float) dis.readFloat();
        FF = (float) dis.readFloat();
        P = (float) dis.readFloat();
        I = (float) dis.readFloat();
        D = (float) dis.readFloat();
        axis = (int) dis.readUnsignedByte() & 0x00FF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 25];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeFloat(desired);
        dos.writeFloat(achieved);
        dos.writeFloat(FF);
        dos.writeFloat(P);
        dos.writeFloat(I);
        dos.writeFloat(D);
        dos.writeByte(axis & 0x00FF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 25);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[31] = crcl;
        buffer[32] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_PID_TUNING : " + "  desired=" + desired + "  achieved=" + achieved + "  FF=" + FF + "  P=" + P + "  I=" + I + "  D=" + D + "  axis=" + axis;
    }
}
