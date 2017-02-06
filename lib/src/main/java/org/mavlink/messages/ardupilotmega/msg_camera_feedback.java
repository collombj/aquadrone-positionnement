/**
 * Generated class : msg_camera_feedback
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
 * Class msg_camera_feedback
 * Camera Capture Feedback
 **/
public class msg_camera_feedback extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_CAMERA_FEEDBACK = 180;
    private static final long serialVersionUID = MAVLINK_MSG_ID_CAMERA_FEEDBACK;
    /**
     * Image timestamp (microseconds since UNIX epoch), as passed in by CAMERA_STATUS message (or autopilot if no CCB)
     */
    public long time_usec;
    /**
     * Latitude in (deg * 1E7)
     */
    public long lat;
    /**
     * Longitude in (deg * 1E7)
     */
    public long lng;
    /**
     * Altitude Absolute (meters AMSL)
     */
    public float alt_msl;
    /**
     * Altitude Relative (meters above HOME location)
     */
    public float alt_rel;
    /**
     * Camera Roll angle (earth frame, degrees, +-180)
     */
    public float roll;
    /**
     * Camera Pitch angle (earth frame, degrees, +-180)
     */
    public float pitch;
    /**
     * Camera Yaw (earth frame, degrees, 0-360, true)
     */
    public float yaw;
    /**
     * Focal Length (mm)
     */
    public float foc_len;
    /**
     * Image index
     */
    public int img_idx;
    /**
     * System ID
     */
    public int target_system;
    /**
     * Camera ID
     */
    public int cam_idx;
    /**
     * See CAMERA_FEEDBACK_FLAGS enum for definition of the bitmask
     */
    public int flags;

    public msg_camera_feedback() {
        this(1, 1);
    }

    public msg_camera_feedback(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_CAMERA_FEEDBACK;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 45;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        time_usec = (long) dis.readLong();
        lat = (int) dis.readInt();
        lng = (int) dis.readInt();
        alt_msl = (float) dis.readFloat();
        alt_rel = (float) dis.readFloat();
        roll = (float) dis.readFloat();
        pitch = (float) dis.readFloat();
        yaw = (float) dis.readFloat();
        foc_len = (float) dis.readFloat();
        img_idx = (int) dis.readUnsignedShort() & 0x00FFFF;
        target_system = (int) dis.readUnsignedByte() & 0x00FF;
        cam_idx = (int) dis.readUnsignedByte() & 0x00FF;
        flags = (int) dis.readUnsignedByte() & 0x00FF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 45];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeLong(time_usec);
        dos.writeInt((int) (lat & 0x00FFFFFFFF));
        dos.writeInt((int) (lng & 0x00FFFFFFFF));
        dos.writeFloat(alt_msl);
        dos.writeFloat(alt_rel);
        dos.writeFloat(roll);
        dos.writeFloat(pitch);
        dos.writeFloat(yaw);
        dos.writeFloat(foc_len);
        dos.writeShort(img_idx & 0x00FFFF);
        dos.writeByte(target_system & 0x00FF);
        dos.writeByte(cam_idx & 0x00FF);
        dos.writeByte(flags & 0x00FF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 45);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[51] = crcl;
        buffer[52] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_CAMERA_FEEDBACK : " + "  time_usec=" + time_usec + "  lat=" + lat + "  lng=" + lng + "  alt_msl=" + alt_msl + "  alt_rel=" + alt_rel + "  roll=" + roll + "  pitch=" + pitch + "  yaw=" + yaw + "  foc_len=" + foc_len + "  img_idx=" + img_idx + "  target_system=" + target_system + "  cam_idx=" + cam_idx + "  flags=" + flags;
    }
}
