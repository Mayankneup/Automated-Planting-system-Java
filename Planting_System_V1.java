package MINOR;

import edu.princeton.cs.introcs.StdDraw;
import org.firmata4j.*;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.MonochromeCanvas;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static MINOR.PORT.*;

public class Mayank_Minor {
    public static class BUTTON_LISTERNER implements IODeviceEventListener {
        private final Pin BUTTON;
        private final IODevice BOARD;
        private final SSD1306 theOledObject;
        private final Pin LED;
        private final Pin MOTOR;



        public BUTTON_LISTERNER(Pin BUTTON, IODevice BOARD, SSD1306 aDisplayObject) throws IOException {
            this.BUTTON = BUTTON;
            this.BOARD = BOARD;
            this.LED = BOARD.getPin(D4);
            LED.setMode(Pin.Mode.OUTPUT);
            this.MOTOR = BOARD.getPin(D6);
            MOTOR.setMode(Pin.Mode.OUTPUT);
            theOledObject = aDisplayObject;

        }

        @Override
        public void onPinChange(IOEvent event) {
            if (event.getPin().getIndex() != BUTTON.getIndex()) {
                return;
            }
            if (event.getValue() == 0) {
                try {
                    LED.setValue(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    MOTOR.setValue(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    System.out.println("Event closed");
                    System.exit(0);
                    BOARD.stop();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void onStart(IOEvent event) {
        }

        @Override
        public void onStop(IOEvent event) {
        }

        @Override
        public void onMessageReceive(IOEvent event, String message) {
        }
    }

    public static class CountTask extends TimerTask {
        private long UNFILTERED_MOISTURE;
        private final SSD1306 theOledObject;
        private final Pin MOISTURE;
        private final Pin MOTER;
        private int SAMPLE;
        private IODevice BOARD;
        private final Pin LED;
        private final Pin BOTTON;



        public CountTask(SSD1306 aDisplayObject, Pin MOISTURE, Pin MOTER, Pin LED, Pin BOTTON, int SAMPLE, IODevice BOARD) throws IOException {
            this.BOARD = BOARD;
            theOledObject = aDisplayObject;
            this.SAMPLE = SAMPLE;
            this.MOISTURE = BOARD.getPin(A0);
            this.BOTTON = BOTTON;
            BOTTON.setMode(Pin.Mode.INPUT);
            try {
                MOISTURE.setMode(Pin.Mode.ANALOG);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.MOTER = BOARD.getPin(D6);
            try {
                MOTER.setMode(Pin.Mode.OUTPUT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.LED = BOARD.getPin(D4);
            try {
                MOTER.setMode(Pin.Mode.OUTPUT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        public long getMoistureValues() {
            return UNFILTERED_MOISTURE;
        }

        @Override
        public void run() {
            UNFILTERED_MOISTURE = MOISTURE.getValue();
            double MV = ((double) 5 /1023);
            double MOISTURE_VALUE = MV * UNFILTERED_MOISTURE;
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Moisture Value in Volts: "+String.format("%.2f", MOISTURE_VALUE)+ "V");
            System.out.println("Moisture Value in Sensor: "+UNFILTERED_MOISTURE);
            StdDraw.setXscale(-10, 100);
            StdDraw.setYscale(-30, 1100);
            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.DARK_GRAY);
            StdDraw.line(0, 0, 0, 1030);
            StdDraw.line(100, 0, 100, 1030);
            StdDraw.line(0, 0, 100, 0);
            StdDraw.line(0, 1030, 100, 1030);
            StdDraw.text(50, -30, "Sample Size");
            StdDraw.text(-7, -8, "0 Volts");
            StdDraw.text(-7, 1000, "5 Volts");
            StdDraw.text(-5, 500, "Moisture Value [V]", 90);
            StdDraw.text(50, 1100, "Time Vs Moisture");
            HashMap<Integer, Integer> mypairs = new HashMap<>();
            mypairs.put(SAMPLE, Integer.valueOf((int) UNFILTERED_MOISTURE));
            mypairs.forEach((xValue, yValue) -> StdDraw.text(xValue, yValue, "ø"));
            if (SAMPLE < 100) {
                SAMPLE++;
            } else {
                SAMPLE = 1;
                StdDraw.clear();
                StdDraw.setXscale(-10, 100);
                StdDraw.setYscale(-30, 1100);
                StdDraw.setPenRadius(0.005);
                StdDraw.setPenColor(StdDraw.DARK_GRAY);
                StdDraw.line(0, 0, 0, 1030);
                StdDraw.line(100, 0, 100, 1030);
                StdDraw.line(0, 0, 100, 0);
                StdDraw.line(0, 1030, 100, 1030);
                StdDraw.text(50, -30, "Sample Size");
                StdDraw.text(-7, -8, "0 Volts");
                StdDraw.text(-7, 1000, "5 Volts");
                StdDraw.text(-5, 500, "Moisture Value [V]", 90);
                StdDraw.text(50, 1100, "Time Vs Moisture");
            }

            if (UNFILTERED_MOISTURE > WET_VALUE) {
                theOledObject.getCanvas().drawString(0, 0, "Moisture in");
                theOledObject.getCanvas().drawString(0, 10, "Volts:");
                theOledObject.getCanvas().drawString(0, 20, "Sensor:");
                theOledObject.getCanvas().drawString(60, 10, "V");
                theOledObject.getCanvas().drawString(42, 20, String.valueOf(UNFILTERED_MOISTURE));
                theOledObject.getCanvas().drawString(35, 10, String.valueOf(String.format("%.2f", MOISTURE_VALUE)));
                theOledObject.getCanvas().drawHorizontalLine(0, 30, 100, MonochromeCanvas.Color.BRIGHT);
                theOledObject.getCanvas().drawString(0, 35, "Status:");
                theOledObject.getCanvas().drawString(0, 42,"Needs more water");
                theOledObject.getCanvas().drawString(0, 52, "Status of motor: ON");
                theOledObject.display();

                try {
                    LED.setValue(1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    MOTER.setValue(1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                theOledObject.clear();
            } else {
                theOledObject.getCanvas().drawString(0, 0, "Moisture in");
                theOledObject.getCanvas().drawString(0, 10, "Volts:");
                theOledObject.getCanvas().drawString(0, 20, "Sensor:");
                theOledObject.getCanvas().drawString(60, 10, "V");
                theOledObject.getCanvas().drawString(42, 20, String.valueOf(UNFILTERED_MOISTURE));
                theOledObject.getCanvas().drawString(35, 10, String.valueOf(String.format("%.2f", MOISTURE_VALUE)));
                theOledObject.getCanvas().drawHorizontalLine(0, 30, 100, MonochromeCanvas.Color.BRIGHT);
                theOledObject.getCanvas().drawString(0, 35, "Status:");
                theOledObject.getCanvas().drawString(0, 42,"Soil is wet");
                theOledObject.getCanvas().drawString(0, 52, "Status of motor: OFF");
                theOledObject.display();
                try {
                    LED.setValue(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    MOTER.setValue(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                theOledObject.clear();
            }
            
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        IODevice board = new FirmataDevice(BOARD);
        board.start();
        System.out.println("Board Started for minor project");
        board.ensureInitializationIsDone();
        System.out.println("Initilization is done");
        I2CDevice screen_object = board.getI2CDevice((byte) 0x3C);
        SSD1306 SCREEN = new SSD1306(screen_object, SSD1306.Size.SSD1306_128_64);
        SCREEN.init();

        Pin Moisture = board.getPin(A0);
        Moisture.setMode(Pin.Mode.ANALOG);
        Pin Moter = board.getPin(D6);
        Moter.setMode(Pin.Mode.OUTPUT);
        Pin LED = board.getPin(D4);
        LED.setMode(Pin.Mode.OUTPUT);
        Pin BOTTON = board.getPin(D5);
        BOTTON.setMode(Pin.Mode.INPUT);
        CountTask MINOR_TASK = new CountTask(SCREEN, Moisture, Moter, LED, BOTTON, 0, board);
        Timer MINOR_TASK_TIMER = new Timer();
        MINOR_TASK_TIMER.schedule(MINOR_TASK, 0, DELAY);
        board.addEventListener(new BUTTON_LISTERNER(BOTTON, board, SCREEN));
    }
}
/*
Citation:
JavaTpoint."Two Decimal Places in Java."JavaTpoint,n.d.,www.javatpoint.com/two-decimal-places-java.
 */
