/**
 * Generated class : msg_mag_cal_report
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
 * Class msg_mag_cal_report
 * Reports results of completed compass calibration. Sent until MAG_CAL_ACK received.
 **/
public class msg_mag_cal_report extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_MAG_CAL_REPORT = 192;
    private static final long serialVersionUID = MAVLINK_MSG_ID_MAG_CAL_REPORT;
    /**
     * RMS milligauss residuals
     */
    public float fitness;
    /**
     * X offset
     */
    public float ofs_x;
    /**
     * Y offset
     */
    public float ofs_y;
    /**
     * Z offset
     */
    public float ofs_z;
    /**
     * X diagonal (matrix 11)
     */
    public float diag_x;
    /**
     * Y diagonal (matrix 22)
     */
    public float diag_y;
    /**
     * Z diagonal (matrix 33)
     */
    public float diag_z;
    /**
     * X off-diagonal (matrix 12 and 21)
     */
    public float offdiag_x;
    /**
     * Y off-diagonal (matrix 13 and 31)
     */
    public float offdiag_y;
    /**
     * Z off-diagonal (matrix 32 and 23)
     */
    public float offdiag_z;
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
     * 0=requires a MAV_CMD_DO_ACCEPT_MAG_CAL, 1=saved to parameters
     */
    public int autosaved;

    public msg_mag_cal_report() {
        this(1, 1);
    }

    public msg_mag_cal_report(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_MAG_CAL_REPORT;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 44;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        fitness = (float) dis.readFloat();
        ofs_x = (float) dis.readFloat();
        ofs_y = (float) dis.readFloat();
        ofs_z = (float) dis.readFloat();
        diag_x = (float) dis.readFloat();
        diag_y = (float) dis.readFloat();
        diag_z = (float) dis.readFloat();
        offdiag_x = (float) dis.readFloat();
        offdiag_y = (float) dis.readFloat();
        offdiag_z = (float) dis.readFloat();
        compass_id = (int) dis.readUnsignedByte() & 0x00FF;
        cal_mask = (int) dis.readUnsignedByte() & 0x00FF;
        cal_status = (int) dis.readUnsignedByte() & 0x00FF;
        autosaved = (int) dis.readUnsignedByte() & 0x00FF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 44];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeFloat(fitness);
        dos.writeFloat(ofs_x);
        dos.writeFloat(ofs_y);
        dos.writeFloat(ofs_z);
        dos.writeFloat(diag_x);
        dos.writeFloat(diag_y);
        dos.writeFloat(diag_z);
        dos.writeFloat(offdiag_x);
        dos.writeFloat(offdiag_y);
        dos.writeFloat(offdiag_z);
        dos.writeByte(compass_id & 0x00FF);
        dos.writeByte(cal_mask & 0x00FF);
        dos.writeByte(cal_status & 0x00FF);
        dos.writeByte(autosaved & 0x00FF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 44);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[50] = crcl;
        buffer[51] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_MAG_CAL_REPORT : " + "  fitness=" + fitness + "  ofs_x=" + ofs_x + "  ofs_y=" + ofs_y + "  ofs_z=" + ofs_z + "  diag_x=" + diag_x + "  diag_y=" + diag_y + "  diag_z=" + diag_z + "  offdiag_x=" + offdiag_x + "  offdiag_y=" + offdiag_y + "  offdiag_z=" + offdiag_z + "  compass_id=" + compass_id + "  cal_mask=" + cal_mask + "  cal_status=" + cal_status + "  autosaved=" + autosaved;
    }
}
