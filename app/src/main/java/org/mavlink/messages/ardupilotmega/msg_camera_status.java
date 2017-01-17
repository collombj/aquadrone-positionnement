/**
 * Generated class : msg_camera_status
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
 * Class msg_camera_status
 * Camera Event
 **/
public class msg_camera_status extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_CAMERA_STATUS = 179;
    private static final long serialVersionUID = MAVLINK_MSG_ID_CAMERA_STATUS;
    /**
     * Image timestamp (microseconds since UNIX epoch, according to camera clock)
     */
    public long time_usec;
    /**
     * Parameter 1 (meaning depends on event, see CAMERA_STATUS_TYPES enum)
     */
    public float p1;
    /**
     * Parameter 2 (meaning depends on event, see CAMERA_STATUS_TYPES enum)
     */
    public float p2;
    /**
     * Parameter 3 (meaning depends on event, see CAMERA_STATUS_TYPES enum)
     */
    public float p3;
    /**
     * Parameter 4 (meaning depends on event, see CAMERA_STATUS_TYPES enum)
     */
    public float p4;
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
     * See CAMERA_STATUS_TYPES enum for definition of the bitmask
     */
    public int event_id;

    public msg_camera_status() {
        this(1, 1);
    }

    public msg_camera_status(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_CAMERA_STATUS;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 29;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        time_usec = (long) dis.readLong();
        p1 = (float) dis.readFloat();
        p2 = (float) dis.readFloat();
        p3 = (float) dis.readFloat();
        p4 = (float) dis.readFloat();
        img_idx = (int) dis.readUnsignedShort() & 0x00FFFF;
        target_system = (int) dis.readUnsignedByte() & 0x00FF;
        cam_idx = (int) dis.readUnsignedByte() & 0x00FF;
        event_id = (int) dis.readUnsignedByte() & 0x00FF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 29];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeLong(time_usec);
        dos.writeFloat(p1);
        dos.writeFloat(p2);
        dos.writeFloat(p3);
        dos.writeFloat(p4);
        dos.writeShort(img_idx & 0x00FFFF);
        dos.writeByte(target_system & 0x00FF);
        dos.writeByte(cam_idx & 0x00FF);
        dos.writeByte(event_id & 0x00FF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 29);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[35] = crcl;
        buffer[36] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_CAMERA_STATUS : " + "  time_usec=" + time_usec + "  p1=" + p1 + "  p2=" + p2 + "  p3=" + p3 + "  p4=" + p4 + "  img_idx=" + img_idx + "  target_system=" + target_system + "  cam_idx=" + cam_idx + "  event_id=" + event_id;
    }
}
