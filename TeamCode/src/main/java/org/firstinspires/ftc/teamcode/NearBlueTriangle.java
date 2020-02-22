package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.lib.AutonomousLibrary;
import org.firstinspires.ftc.teamcode.lib.util.states.StartState;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@Autonomous(name="NearBlueTriangle")
public class NearBlueTriangle extends AutonomousLibrary {//Note: states are backwards, the one at the end runs first
    Servo foundationGrabber;
    Servo foundationGrabber2;

    @Override
    public void setupOpMode() {
        foundationGrabber = getServo("foundationGrabber"); //Get the foundation grabber servo
        foundationGrabber.setPosition(0);
        foundationGrabber2 = getServo("foundationGrabber2"); //Get the foundation grabber servo
        foundationGrabber2.setPosition(1);

        State strafeRightToBridge = new StartState(() -> {
            moveRightCentimeters(-120, -0.5); //Strafe left 120cm at 0.5 speed to the bridge
        }, () -> true, () -> {}, "StrafeRightToBridge");

        State releasePlatform = new State(() -> {
            foundationGrabber.setPosition(0); //Open the foundation grabber servo
            foundationGrabber2.setPosition(1);
            return false;
        }, () -> { //When the state is done
            stateMachine.addState(strafeRightToBridge); //Run strafeRightToBridge
        }, 1000, "ReleasePlatform");

        State moveBackwardFromPlatform = new StartState(() -> {
            //Move backward 110cm at 0.5 speed to the loading zone, and run releasePlatform afterward
            moveForwardCentimeters(110, 0.5, releasePlatform);
        }, () -> true, () -> {},"MoveBackwardFromPlatform");

        State grabPlatform = new State(() -> {
            foundationGrabber.setPosition(1); //Close the foundation grabber servo
            foundationGrabber2.setPosition(0);
            return false;
        }, () -> { //When the state is done
            stateMachine.addState(moveBackwardFromPlatform); //Run moveBackwardFromPlatform
        }, 1000, "GrabPlatform");

        State moveForwardToPlatform = new StartState(() -> {
            //Move forward 70cm at 0.5 speed to the foundation, and run grabPlatform afterward
            moveForwardCentimeters(-70, -0.5, grabPlatform);
        }, () -> true, () -> {}, "MoveForwardToPlatform");

        State strafeAlign = new StartState(() -> {
            //Align the bot with the foundation where the foundation will stay in the loading zone while the bot navigates
            wheelController.moveXY(0, 0.1);
            moveRightCentimeters(40.5, 0.5, moveForwardToPlatform);
        }, () -> true, () -> {}, "StrafeAlign");

        State moveOffWall = new StartState(() -> {
            //Move forward 8cm at 0.5 speed to avoid future friction with the wall, and run strafeAlign afterward
            moveForwardCentimeters(-8, -0.5, strafeAlign);
        }, () -> true, () -> {}, "MoveForwardToPlatform");

        stateMachine.addState(moveOffWall);//After we set up the states, we add the first one to our state machine
    }
}