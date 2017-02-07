package fr.onema.lib.sensor.position.IMU;


import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by strock on 06/02/2017.
 */
public class IMUTest {

    @Test
    public void build() throws Exception {
        msg_scaled_imu msg =new msg_scaled_imu();
        IMU imu=IMU.build(msg);
        assertNotNull(imu);
    }

    @Test
    public void getAccelerometer() throws Exception {
        msg_scaled_imu msg =new msg_scaled_imu();
        msg.xacc=3;
        msg.yacc=4;
        msg.zacc=5;
        IMU imu= IMU.build(msg);
        assertNotNull(imu.getAccelerometer());
        assertEquals(3,imu.getAccelerometer().getxAcceleration());
        assertEquals(4,imu.getAccelerometer().getyAcceleration());
        assertEquals(5,imu.getAccelerometer().getzAcceleration());
    }

    @Test
    public void getGyroscope() throws Exception {
        msg_scaled_imu msg =new msg_scaled_imu();
        msg.xgyro=3;
        msg.ygyro=4;
        msg.zgyro=5;
        IMU imu=IMU.build(msg);
        assertNotNull(imu.getGyroscope());
        assertEquals(3,imu.getGyroscope().getxRotation());
        assertEquals(4,imu.getGyroscope().getyRotation());
        assertEquals(5,imu.getGyroscope().getzRotation());
    }

    @Test
    public void getCompass() throws Exception {
        msg_scaled_imu msg =new msg_scaled_imu();
        msg.xmag=3;
        msg.ymag=4;
        msg.zmag=5;
        IMU imu=  IMU.build(msg);
        assertNotNull(imu.getCompass());
        assertEquals(3,imu.getCompass().getxMagnetic());
        assertEquals(4,imu.getCompass().getyMagnetic());
        assertEquals(5,imu.getCompass().getzMagnetic());
    }
}