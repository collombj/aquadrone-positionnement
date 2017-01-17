/**
 * Generated class : msg_mag_cal_progress
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
 * Class msg_mag_cal_progress
 * Reports progress of compass calibration.
 **/
public class msg_mag_cal_progress extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_MAG_CAL_PROGRESS = 191;
    private static final long serialVersionUID = MAVLINK_MSG_ID_MAG_CAL_PROGRESS;
    /**
     * Body frame direction vector for display
     */
    public float direction_x;
    /**
     * Body frame direction vector for display
     */
    public float direction_y;
    /**
     * Body frame direction vector for display
     */
    public float direction_z;
    /**
     * Compass being calibrated
     */
    public int compass_id;
    /**
     * Bitmask of compasses being calibrated
     */
    public int cal_mask;
    /**
     * Status (see MAG_CAL_STATUS enum)
     */
    public int cal_status;
    /**
     * Attempt number
     */
    public int attempt;
    /**
     * Completion percentage
     */
    public int completion_pct;
    /**
     * Bitmask of sphere sections (see http://en.wikipedia.org/wiki/Geodesic_grid)
     */
    public int[] completion_mask = new int[10];

    public msg_mag_cal_progress() {
        this(1, 1);
    }

    public msg_mag_cal_progress(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_MAG_CAL_PROGRESS;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 27;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        direction_x = (float) dis.readFloat();
        direction_y = (float) dis.readFloat();
        direction_z = (float) dis.readFloat();
        compass_id = (int) dis.readUnsignedByte() & 0x00FF;
        cal_mask = (int) dis.readUnsignedByte() & 0x00FF;
        cal_status = (int) dis.readUnsignedByte() & 0x00FF;
        attempt = (int) dis.readUnsignedByte() & 0x00FF;
        completion_pct = (int) dis.readUnsignedByte() & 0x00FF;
        for (int i = 0; i < 10; i++) {
            completion_mask[i] = (int) dis.readUnsignedByte() & 0x00FF;
        }
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 27];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeFloat(direction_x);
        dos.writeFloat(direction_y);
        dos.writeFloat(direction_z);
        dos.writeByte(compass_id & 0x00FF);
        dos.writeByte(cal_mask & 0x00FF);
        dos.writeByte(cal_status & 0x00FF);
        dos.writeByte(attempt & 0x00FF);
        dos.writeByte(completion_pct & 0x00FF);
        for (int i = 0; i < 10; i++) {
            dos.writeByte(completion_mask[i] & 0x00FF);
        }
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 27);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[33] = crcl;
        buffer[34] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_MAG_CAL_PROGRESS : " + "  direction_x=" + direction_x + "  direction_y=" + direction_y + "  direction_z=" + direction_z + "  compass_id=" + compass_id + "  cal_mask=" + cal_mask + "  cal_status=" + cal_status + "  attempt=" + attempt + "  completion_pct=" + completion_pct + "  completion_mask=" + completion_mask;
    }
}
