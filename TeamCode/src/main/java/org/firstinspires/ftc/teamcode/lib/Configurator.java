package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Disabled
public abstract class Configurator extends OpMode{
    //Our robot's motors
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;

    //Debug mode, one place to enable all debug logging. Can be enabled by pressing ABXY on driver A.
    private boolean debugMode = false;

    //These values used for motor fault checking
    private int oldFrontLeftEncoder = 999999;
    private int oldFrontRightEncoder = 999999;
    private int oldBackLeftEncoder = 999999;
    private int oldBackRightEncoder = 999999;
    private boolean faultOccured = false;

    //These are stored for the user
    public StateMachine stateMachine;
    public WheelController wheelController;

    //Get an IMU safely / initialize even if it has errors
    public BNO055IMU getIMU(String name) {
        try {
            return hardwareMap.get(BNO055IMU.class, name);
        } catch (Exception e) {
            telemetry.addLine("CFG: Could not find IMU \"" + name + "\", add to config.");
            try {
                //Most of this stuff is only for the IMU, it just sets up our preferences
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
            try {
                return DcMotor.class.newInstance();
            } catch (Exception ex) {
                return null;
            }
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

    //On init, setup motors, stateMachine, and wheelController
    @Override
    public void init() {
        frontLeft = getDcMotor("frontLeft");
        frontRight = getDcMotor("frontRight");
        backLeft = getDcMotor("backLeft");
        backRight = getDcMotor("backRight");

        stateMachine = new StateMachine(this);
        wheelController = new WheelController(this);

        //Run the user code
        setupOpMode();
    }

    //Check for debug mode
    @Override
    public void init_loop() {
        if (gamepad1.a && gamepad1.b && gamepad1.x && gamepad1.y) debugMode = true;
    }

    //This is where the code for auto goes
    public abstract void setupOpMode();

    //During loop, check for debug mode, detect faults, and run the states
    @Override
    public void loop() {
        if (gamepad1.a && gamepad1.b && gamepad1.x && gamepad1.y) debugMode = true;
        detectMotorFault();
        stateMachine.runStates();
    }


    public void detectMotorFault() {
        if (faultOccured) telemetry.addLine("A motor fault occurred!");
        //Check if the front left motor is powered but the encoder is not moving
        if (frontLeft.getPower() != 0 && oldFrontLeftEncoder == frontLeft.getCurrentPosition()) {
            //If so, there is an encoder issue so we disable encoders
            faultOccured = true;
            frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        //Do the same for the other motors
        if (frontRight.getPower() != 0 && oldFrontRightEncoder == frontRight.getCurrentPosition()) {
            faultOccured = true;
            frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        if (backLeft.getPower() != 0 && oldBackLeftEncoder == backLeft.getCurrentPosition()) {
            faultOccured = true;
            backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        if (backRight.getPower() != 0 && oldBackRightEncoder == backRight.getCurrentPosition()) {
            faultOccured = true;
            backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    //Safely access debug mode, so that it cannot be set anywhere but here
    public boolean getDebugMode() {
        return debugMode;
    }

    //Useful for autonomous, uses the fault checking items
    public boolean getMotorsMoving() {
        return (oldFrontLeftEncoder != frontLeft.getCurrentPosition() || oldFrontRightEncoder != frontRight.getCurrentPosition() || oldBackLeftEncoder != backLeft.getCurrentPosition() || oldBackRightEncoder != backRight.getCurrentPosition());
    }
}
