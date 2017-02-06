/**
 * Generated class : msg_ahrs3
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
 * Class msg_ahrs3
 * Status of third AHRS filter if available. This is for ANU research group (Ali and Sean)
 **/
public class msg_ahrs3 extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_AHRS3 = 182;
    private static final long serialVersionUID = MAVLINK_MSG_ID_AHRS3;
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
    /**
     * test variable1
     */
    public float v1;
    /**
     * test variable2
     */
    public float v2;
    /**
     * test variable3
     */
    public float v3;
    /**
     * test variable4
     */
    public float v4;

    public msg_ahrs3() {
        this(1, 1);
    }

    public msg_ahrs3(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_AHRS3;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 40;
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
        v1 = (float) dis.readFloat();
        v2 = (float) dis.readFloat();
        v3 = (float) dis.readFloat();
        v4 = (float) dis.readFloat();
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 40];
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
        dos.writeFloat(v1);
        dos.writeFloat(v2);
        dos.writeFloat(v3);
        dos.writeFloat(v4);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 40);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[46] = crcl;
        buffer[47] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_AHRS3 : " + "  roll=" + roll + "  pitch=" + pitch + "  yaw=" + yaw + "  altitude=" + altitude + "  lat=" + lat + "  lng=" + lng + "  v1=" + v1 + "  v2=" + v2 + "  v3=" + v3 + "  v4=" + v4;
    }
}
