package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.lib.Configurator;
import org.firstinspires.ftc.teamcode.lib.MecanumOdometer;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@TeleOp(name="TeleOp", group="default")

public class TurtlesTeleOp extends Configurator {

    //Declare hardware
    DcMotor armMotor;
    Servo claw;
    Servo foundationGrabber;
    TouchSensor armToucher;

    //Initialize values
    double slowMode = 1;
    boolean slowModeJustSwapped = false;
    boolean armLimit = true;

    @Override
    public void setupOpMode() {

        //Initialize hardware
        armMotor = getDcMotor("armMotor");
        claw = getServo("claw");
        foundationGrabber = getServo("foundationGrabber");
        foundationGrabber.setPosition(1);
        armToucher = getTouchSensor("armToucher");
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stateMachine.addState(new State(() -> { //Create a new state

            //If Driver 1 presses either of the bumpers on his or her gamepad, engage slow mode
            if ((gamepad1.right_bumper || gamepad1.left_bumper) && !slowModeJustSwapped) {
                slowMode = (slowMode == 1) ? 2 : 1;
            }
            slowModeJustSwapped = (gamepad1.right_bumper || gamepad1.left_bumper);

            //Update wheel speeds based on controller input and slow mode
            wheelController.moveXYTurn(
                    gamepad1.right_stick_x / slowMode,
                    -gamepad1.left_stick_y / slowMode,
                    -gamepad1.left_stick_x / slowMode * 0.8
            );

            //Update armSpeed based on input and its current position, slowing down if it is high enough
            double armSpeed = -gamepad2.left_stick_y;
            if (armMotor.getCurrentPosition() <= 0 && armLimit)
                armSpeed = Range.clip(armSpeed, 0, 1);
            armMotor.setPower(Range.clip(armSpeed + 0.05, -1, 1));
            telemetry.addData("armMotor", armMotor.getCurrentPosition());//Output the position of armMotor to the Driver Station

            if (gamepad1.b) wheelController.runWithoutEncoder();//If Driver 1 presses b, do not use encoders
            if (gamepad1.a) wheelController.runUsingEncoder();//If Driver 1 presses a, use encoders
            if (gamepad2.left_bumper) armLimit = false;//If Driver 2 presses the left bumper, don't use arm's slow mode
            if (gamepad2.right_bumper) armLimit = true;//If Driver 2 presses the right bumper, use arm's slow mode

            if (gamepad2.a) claw.setPosition(0);//If Driver 2 presses a, open the claw servo on the arm
            if (gamepad2.b) claw.setPosition(1);//If Driver 2 presses b, close the claw servo on the arm
            if (gamepad2.x) foundationGrabber.setPosition(1);//If Driver 2 presses x, open the foundation grabber servo
            if (gamepad2.y) foundationGrabber.setPosition(0);//If Driver 2 presses y, close the foundation grabber servo

            return false;
        }, () -> {}, "TeleOp")); //Don't run anything on stop, and name it TeleOp
    }
}