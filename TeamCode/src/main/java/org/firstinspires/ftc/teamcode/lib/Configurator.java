package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.lib.util.debug.TimeTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Disabled
public abstract class Configurator extends OpMode{
    //Our robot's motors
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    StackTraceElement[] stackTrace = null;

    //Debug mode, one place to enable all debug logging; enabled by pressing ABXY on Driver 1.
    private boolean debugMode = false;

    //These are stored for the user
    public HighPerformanceStateMachine stateMachine;
    public HighPerformanceWheelController wheelController;

    ElapsedTime timer = new ElapsedTime();
    List<Double> avgTime = new ArrayList<>();

    //Get an IMU safely / initialize even if it has errors
    public BNO055IMU getIMU(String name) {
        try {
            return hardwareMap.get(BNO055IMU.class, name);
        } catch (Exception e) {
            telemetry.addLine("CFG: Could not find IMU \"" + name + "\", add to config.");
            try {
                //Most of this parameter setup code is only for the IMU to set up our preferences
                BNO055IMU imu = hardwareMap.get(BNO055IMU.class, name);
                BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

                parameters.mode = BNO055IMU.SensorMode.IMU;
                parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
                parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
                parameters.loggingEnabled = false;

                imu.initialize(parameters);

                return BNO055IMU.class.newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    //Get a DcMotor safely
    public DcMotor getDcMotor(String name) {
        try {
            return hardwareMap.dcMotor.get(name);
        } catch (Exception e) {
            telemetry.addLine("CFG: Could not find motor \"" + name + "\", add to config.");
            return null;
        }
    }

    //Get a servo safely
    public Servo getServo(String name) {
        try {
            return hardwareMap.servo.get(name);
        } catch (Exception e) {
            telemetry.addLine("CFG: Could not find servo \"" + name + "\", add to config.");
            try {
                return Servo.class.newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    //Get a touch sensor safely
    public TouchSensor getTouchSensor(String name) {
        try {
            return hardwareMap.touchSensor.get(name);
        } catch (Exception e) {
            telemetry.addLine("CFG: Could not find touchSensor \"" + name + "\", add to config.");
            try {
                return TouchSensor.class.newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }


    @Override
    public void init() { //On init, setup motors, stateMachine, and wheelController
        frontLeft = getDcMotor("frontLeft");
        frontRight = getDcMotor("frontRight");
        backLeft = getDcMotor("backLeft");
        backRight = getDcMotor("backRight");

        stateMachine = new HighPerformanceStateMachine(this);
        wheelController = new HighPerformanceWheelController(this);

        try {
            setupOpMode();
        } catch (Exception e) {
            stackTrace = e.getStackTrace();
            telemetry.addLine("YAY! User code threw an exception, press start for more info.");
        }
    }

    @Override
    public void init_loop() {
        if (gamepad1.a && gamepad1.b && gamepad1.x && gamepad1.y) debugMode = true;
    }

    //This is where the code for autonomous goes
    public abstract void setupOpMode();

    @Override
    public void loop() {
        if (!stateMachine.paused) telemetry.clearAll();
        if (stackTrace == null) {
            try {
                if (getDebugMode() && !stateMachine.paused) timer.reset();
                if (gamepad1.a && gamepad1.b && gamepad1.x && gamepad1.y) debugMode = true;
                wheelController.detectMotorFault();
                stateMachine.runStates();
                if (getDebugMode() && !stateMachine.paused) {
                    if (avgTime.size() > 50) {
                        avgTime.remove(0);
                    }
                    avgTime.add(timer.seconds());
                    telemetry.addLine("CFG: Runtime was " + Math.round(timer.seconds() * 1000) + "ms");
                    double total = 0;
                    for (double d : avgTime) {
                        total += d;
                    }
                    telemetry.addData("CFG: Avg Runtime was", Math.round(total / avgTime.size() * 1000) + "ms");
                }
            } catch (Exception e) {
                stackTrace = e.getStackTrace();
            }
        } else {
            wheelController.stopWheels();
            telemetry.addLine("YAY! User code threw an exception!");
            telemetry.addLine("-- Begin Stack Trace --");
            for (StackTraceElement s : stackTrace) {
                telemetry.addLine("File: " + s.getFileName() + " Class: " + s.getClassName() + " Method: " + s.getMethodName() + " (" + s.getLineNumber() + ")");
            }
            telemetry.addLine("-- End Stack Trace --");
        }
        if (!stateMachine.paused) telemetry.update();
    }

    //Safely access debug mode so that it cannot be set anywhere but here
    public boolean getDebugMode() {
        return debugMode;
    }
}
