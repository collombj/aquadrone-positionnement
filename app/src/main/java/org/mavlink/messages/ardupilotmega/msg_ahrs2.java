/**
 * Generated class : msg_ahrs2
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
 * Class msg_ahrs2
 * Status of secondary AHRS filter if available
 **/
public class msg_ahrs2 extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_AHRS2 = 178;
    private static final long serialVersionUID = MAVLINK_MSG_ID_AHRS2;
    /**
     * Roll angle (rad)
     */
    public float roll;
    /**
     * Pitch angle (rad)
     */
    public float pitch;
    /**
     * Yaw angle (rad)
     */
    public float yaw;
    /**
     * Altitude (MSL)
     */
    public float altitude;
    /**
     * Latitude in degrees * 1E7
     */
    public long lat;
    /**
     * Longitude in degrees * 1E7
     */
    public long lng;

    public msg_ahrs2() {
        this(1, 1);
    }

    public msg_ahrs2(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_AHRS2;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 24;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        roll = (float) dis.readFloat();
        pitch = (float) dis.readFloat();
        yaw = (float) dis.readFloat();
        altitude = (float) dis.readFloat();
        lat = (int) dis.readInt();
        lng = (int) dis.readInt();
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 24];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeFloat(roll);
        dos.writeFloat(pitch);
        dos.writeFloat(yaw);
        dos.writeFloat(altitude);
        dos.writeInt((int) (lat & 0x00FFFFFFFF));
        dos.writeInt((int) (lng & 0x00FFFFFFFF));
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 24);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[30] = crcl;
        buffer[31] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_AHRS2 : " + "  roll=" + roll + "  pitch=" + pitch + "  yaw=" + yaw + "  altitude=" + altitude + "  lat=" + lat + "  lng=" + lng;
    }
}
