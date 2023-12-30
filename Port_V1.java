package Watering_System_V1;

public class PORT {


    static final byte I2C0 = 0x3C; // OLED Display
    public static final int A0 = 14; // Moisture/Angle sencor
    static final int A2 = 16; // Sound
    static final int A6 = 20; // Sound
    static final int D5 = 5; // Buzzer/Moter
    static final int D6 = 6; // Button
    static final int D2 = 2; // Buzzer
    static final int D4 = 4; // LED
    static final int DELAY = 1000;
    static final int DRY_VALUE = 660;
    static final int WET_VALUE = 540;
    static final int MAX_SENCOR_VALUE = 1023;
    static final int MAX_VOLTAGE = 5;

    public static String BOARD = "/dev/cu.usbserial-0001";
}
