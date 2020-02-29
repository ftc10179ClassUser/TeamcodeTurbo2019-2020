package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

public class HighPerformanceWheelController { //This class can be changed for each drive train without breaking our library
    //The configurator that this WheelController is attached to
    Configurator config;

    @Deprecated
    public boolean crispDrive = false;

    @Deprecated
    public void resetLeftEncoder() {
        config.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        config.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Deprecated
    public void resetRightEncoder() {
        config.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        config.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void moveXY(double tx, double ty) { //Take in translateX and translateY
        //Start with how fast we are moving forward
        double frontLeftSpd = -ty;
        double frontRightSpd = -ty;
        double backLeftSpd = -ty;
        double backRightSpd = -ty;

        //Factor in the strafing values
        frontLeftSpd -= tx;
        backLeftSpd += tx;
        frontRightSpd += tx;
        backRightSpd -= tx;

        //Make sure the final output is safe for the motors, or a speed from -1 to 1
        config.frontLeft.setPower(Range.clip(frontLeftSpd, -1, 1));
        config.frontRight.setPower(Range.clip(frontRightSpd, -1, 1));
        config.backLeft.setPower(Range.clip(backLeftSpd, -1, 1));
        config.backRight.setPower(Range.clip(backRightSpd, -1, 1));
    }

    public void moveTurn(double tspeed) {  //Take in turnSpeed
        //Start with the turn values
        double frontLeftSpd = tspeed;
        double backLeftSpd = tspeed;
        double frontRightSpd = -tspeed;
        double backRightSpd = -tspeed;

        //Make sure the final output is safe for the motors, or a speed from -1 to 1
        config.frontLeft.setPower(Range.clip(frontLeftSpd, -1, 1));
        config.frontRight.setPower(Range.clip(frontRightSpd, -1, 1));
        config.backLeft.setPower(Range.clip(backLeftSpd, -1, 1));
        config.backRight.setPower(Range.clip(backRightSpd, -1, 1));
    }

    public void moveXYTurn(double tx, double ty, double tspeed) {
        //Start with how fast we are moving forward
        double frontLeftSpd = -ty;
        double frontRightSpd = -ty;
        double backLeftSpd = -ty;
        double backRightSpd = -ty;

        //Factor in the strafing values
        frontLeftSpd -= tx;
        backLeftSpd += tx;
        frontRightSpd += tx;
        backRightSpd -= tx;

        //Factor in the turning values
        frontLeftSpd += tspeed;
        backLeftSpd += tspeed;
        frontRightSpd -= tspeed;
        backRightSpd -= tspeed;

        //Make sure the final output is safe for the motors, or a speed from -1 to 1
        config.frontLeft.setPower(Range.clip(frontLeftSpd, -1, 1));
        config.frontRight.setPower(Range.clip(frontRightSpd, -1, 1));
        config.backLeft.setPower(Range.clip(backLeftSpd, -1, 1));
        config.backRight.setPower(Range.clip(backRightSpd, -1, 1));
    }

    public void stopWheels() {
        config.frontLeft.setPower(0);
        config.frontRight.setPower(0);
        config.backLeft.setPower(0);
        config.backRight.setPower(0);
    }

    public void runUsingEncoder() {
        config.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        config.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        config.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        config.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void runWithoutEncoder() {
        config.frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        config.frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        config.backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        config.backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    
    public void detectMotorFault() {}

    public HighPerformanceWheelController(Configurator config) {
        //Save the configurator for later.
        this.config = config;

        //Reverse left wheels so the wheels all go forward
        config.frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        config.backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }
}