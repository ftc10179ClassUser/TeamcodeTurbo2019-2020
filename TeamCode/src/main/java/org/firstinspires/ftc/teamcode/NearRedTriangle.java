package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.lib.AutonomousLibrary;
import org.firstinspires.ftc.teamcode.lib.util.data.PVector;
import org.firstinspires.ftc.teamcode.lib.util.states.StartState;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@Autonomous(name="NearRedTriangle")
public class NearRedTriangle extends AutonomousLibrary { //Note: the states are backwards, the one at the end runs first
    Servo foundationGrabber;
    Servo foundationGrabber2;

    @Override
    public void setupOpMode() {
        foundationGrabber = getServo("foundationGrabber"); //Get the foundation grabber servo
        foundationGrabber2 = getServo("foundationGrabber2");
        foundationGrabber.setPosition(0);
        foundationGrabber2.setPosition(1);

        //Set starting position and rotation
        initializeOdometry(new PVector(342.9, 281.94), 90);

        //Initialize waypoints
        PVector foundation = new PVector(266.7,311.76);
        PVector loadingZone = new PVector(342.9,311.76);
        PVector underSkybridge = new PVector(269.7, 182.88);

        State strafeToBridge = new StartState(() -> {//Create a new StartState, strafeToBridge
            setTargetXYRot(underSkybridge, 90);//Navigate
        }, () -> true, () -> {}, "StrafeRightToBridge");//Name the state StrafeRightToBridge

        State releasePlatform = new State(() -> {//Create a new State, releasePlatform
            //Pull up the foundation grabber servo
            foundationGrabber.setPosition(0);
            foundationGrabber2.setPosition(1);
            return false;
        }, () -> { //When the state is done
            stateMachine.addState(strafeToBridge); //Pass strafeToBridge into the state machine
        }, 1000, "ReleasePlatform");//Name the state ReleasePlatform and run for 1 second

        State pullBackFoundation = new StartState(() -> {//Create a new StartState, pullBackFoundation
            setTargetXYRot(loadingZone, 90, releasePlatform);//Move foundation into the loading zone and run releasePlatform afterward
        }, () -> true, () -> {},"MoveBackwardFromPlatform");//Name the state MoveBackwardFromPlatform

        State grabPlatform = new State(() -> {//Create a new State, grabPlatform
            //Push down the foundation grabber servo
            foundationGrabber.setPosition(1);
            foundationGrabber2.setPosition(0);
            return false;
        }, () -> { //When the state is done
            stateMachine.addState(pullBackFoundation); //Pass pullBackFoundation into the state machine
        }, 1000, "GrabPlatform");//Name the state GrabPlatform and run for 1 second

        State moveToPlatform = new StartState(() -> {//Create a new StartState, moveToPlatform
            setTargetXYRot(foundation, 90, grabPlatform);//Move to platform and run grabPlatform afterward
        }, () -> true, () -> {}, "MoveForwardToPlatform");//Name the state MoveForwardToPlatform

        stateMachine.addState(moveToPlatform); //After we setup the states, add the first one to our stateMachine
    }
}