/**
 * Generated class : msg_led_control
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
 * Class msg_led_control
 * Control vehicle LEDs
 **/
public class msg_led_control extends MAVLinkMessage {
    public static final int MAVLINK_MSG_ID_LED_CONTROL = 186;
    private static final long serialVersionUID = MAVLINK_MSG_ID_LED_CONTROL;
    /**
     * System ID
     */
    public int target_system;
    /**
     * Component ID
     */
    public int target_component;
    /**
     * Instance (LED instance to control or 255 for all LEDs)
     */
    public int instance;
    /**
     * Pattern (see LED_PATTERN_ENUM)
     */
    public int pattern;
    /**
     * Custom Byte Length
     */
    public int custom_len;
    /**
     * Custom Bytes
     */
    public int[] custom_bytes = new int[24];

    public msg_led_control() {
        this(1, 1);
    }

    public msg_led_control(int sysId, int componentId) {
        messageType = MAVLINK_MSG_ID_LED_CONTROL;
        this.sysId = sysId;
        this.componentId = componentId;
        length = 29;
    }

    /**
     * Decode message with raw data
     */
    public void decode(LittleEndianDataInputStream dis) throws IOException {
        target_system = (int) dis.readUnsignedByte() & 0x00FF;
        target_component = (int) dis.readUnsignedByte() & 0x00FF;
        instance = (int) dis.readUnsignedByte() & 0x00FF;
        pattern = (int) dis.readUnsignedByte() & 0x00FF;
        custom_len = (int) dis.readUnsignedByte() & 0x00FF;
        for (int i = 0; i < 24; i++) {
            custom_bytes[i] = (int) dis.readUnsignedByte() & 0x00FF;
        }
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
        dos.writeByte(target_system & 0x00FF);
        dos.writeByte(target_component & 0x00FF);
        dos.writeByte(instance & 0x00FF);
        dos.writeByte(pattern & 0x00FF);
        dos.writeByte(custom_len & 0x00FF);
        for (int i = 0; i < 24; i++) {
            dos.writeByte(custom_bytes[i] & 0x00FF);
        }
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
        return "MAVLINK_MSG_ID_LED_CONTROL : " + "  target_system=" + target_system + "  target_component=" + target_component + "  instance=" + instance + "  pattern=" + pattern + "  custom_len=" + custom_len + "  custom_bytes=" + custom_bytes;
    }
}
