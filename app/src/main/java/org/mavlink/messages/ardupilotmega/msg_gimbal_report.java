/**
 * Generated class : msg_gimbal_report
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
 * Class msg_gimbal_report
 * 3 axis gimbal mesuraments
 **/
public class msg_gimbal_report extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_GIMBAL_REPORT = 200;
    private static final long serialVersionUID = MAVLINK_MSG_ID_GIMBAL_REPORT;
    /**
     * Time since last update (seconds)
     */
    public float delta_time;
    /**
     * Delta angle X (radians)
     */
    public float delta_angle_x;
    /**
     * Delta angle Y (radians)
     */
    public float delta_angle_y;
    /**
     * Delta angle X (radians)
     */
    public float delta_angle_z;
    /**
     * Delta velocity X (m/s)
     */
    public float delta_velocity_x;
    /**
     * Delta velocity Y (m/s)
     */
    public float delta_velocity_y;
    /**
     * Delta velocity Z (m/s)
     */
    public float delta_velocity_z;
    /**
     * Joint ROLL (radians)
     */
    public float joint_roll;
    /**
     * Joint EL (radians)
     */
    public float joint_el;
    /**
     * Joint AZ (radians)
     */
    public float joint_az;
    /**
     * System ID
     */
    public int target_system;
    /**
     * Component ID
     */
    public int target_component;

    public msg_gimbal_report() {
        this(1, 1);
    }

    public msg_gimbal_report(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_GIMBAL_REPORT;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 42;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        delta_time = (float) dis.readFloat();
        delta_angle_x = (float) dis.readFloat();
        delta_angle_y = (float) dis.readFloat();
        delta_angle_z = (float) dis.readFloat();
        delta_velocity_x = (float) dis.readFloat();
        delta_velocity_y = (float) dis.readFloat();
        delta_velocity_z = (float) dis.readFloat();
        joint_roll = (float) dis.readFloat();
        joint_el = (float) dis.readFloat();
        joint_az = (float) dis.readFloat();
        target_system = (int) dis.readUnsignedByte() & 0x00FF;
        target_component = (int) dis.readUnsignedByte() & 0x00FF;
    }

    /**
     * Encode message with raw data and other informations
     */
    public byte[] encode() throws IOException {
        byte[] buffer = new byte[8 + 42];
        LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
        dos.writeByte((byte) 0xFE);
        dos.writeByte(length & 0x00FF);
        dos.writeByte(sequence & 0x00FF);
        dos.writeByte(sysId & 0x00FF);
        dos.writeByte(componentId & 0x00FF);
        dos.writeByte(messageType & 0x00FF);
        dos.writeFloat(delta_time);
        dos.writeFloat(delta_angle_x);
        dos.writeFloat(delta_angle_y);
        dos.writeFloat(delta_angle_z);
        dos.writeFloat(delta_velocity_x);
        dos.writeFloat(delta_velocity_y);
        dos.writeFloat(delta_velocity_z);
        dos.writeFloat(joint_roll);
        dos.writeFloat(joint_el);
        dos.writeFloat(joint_az);
        dos.writeByte(target_system & 0x00FF);
        dos.writeByte(target_component & 0x00FF);
        dos.flush();
        byte[] tmp = dos.toByteArray();
        for (int b = 0; b < tmp.length; b++) buffer[b] = tmp[b];
        int crc = MAVLinkCRC.crc_calculate_encode(buffer, 42);
        crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
        byte crcl = (byte) (crc & 0x00FF);
        byte crch = (byte) ((crc >> 8) & 0x00FF);
        buffer[48] = crcl;
        buffer[49] = crch;
        dos.close();
        return buffer;
    }

    public String toString() {
        return "MAVLINK_MSG_ID_GIMBAL_REPORT : " + "  delta_time=" + delta_time + "  delta_angle_x=" + delta_angle_x + "  delta_angle_y=" + delta_angle_y + "  delta_angle_z=" + delta_angle_z + "  delta_velocity_x=" + delta_velocity_x + "  delta_velocity_y=" + delta_velocity_y + "  delta_velocity_z=" + delta_velocity_z + "  joint_roll=" + joint_roll + "  joint_el=" + joint_el + "  joint_az=" + joint_az + "  target_system=" + target_system + "  target_component=" + target_component;
    }
}
