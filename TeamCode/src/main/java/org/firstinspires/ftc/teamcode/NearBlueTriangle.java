package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.lib.AutonomousLibrary;
import org.firstinspires.ftc.teamcode.lib.util.data.PVector;
import org.firstinspires.ftc.teamcode.lib.util.states.StartState;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@Autonomous(name="NearBlueTriangle")
public class NearBlueTriangle extends AutonomousLibrary {//Note: States run backwards, so the one at the bottom runs first
    //Declare servos
    Servo foundationGrabber;
    Servo foundationGrabber2;

    @Override
    public void setupOpMode() {
        //Get the foundation grabber servos and set initialization position
        foundationGrabber = getServo("foundationGrabber");
        foundationGrabber2 = getServo("foundationGrabber2");
        foundationGrabber.setPosition(0);
        foundationGrabber2.setPosition(1);

        //Set starting position and rotation
        initializeOdometry(new PVector(342.9, 281.94), 270);

        //Initialize waypoints
        PVector foundation = new PVector(99.06, 311.76);
        PVector loadingZone = new PVector(22.86, 311.76);
        PVector underSkybridge = new PVector(96.06, 182.88);

        State strafeToBridge = new StartState(() -> {//Create a new StartState, strafeToBridge
            setTargetXYRot(underSkybridge, 270);//Navigate
        }, () -> true, () -> {
        }, "StrafeRightToBridge");//Name the state StrafeRightToBridge

        State releasePlatform = new State(() -> {//Create a new State, releasePlatform
            //Let go of the foundation
            foundationGrabber.setPosition(0);
            foundationGrabber2.setPosition(1);
            return false;
        }, () -> { //When the state is done
            stateMachine.addState(strafeToBridge); //Run this state
        }, 1000, "ReleasePlatform");//Name the state ReleasePlatform and run for 1 second

        State pullBackFoundation = new StartState(() -> {//Create a new StartState, pullBackFoundation
            setTargetXYRot(loadingZone, 270, releasePlatform);//Pull foundation into loading zone and run releasePlatform afterward
        }, () -> true, () -> {
        }, "MoveBackwardFromPlatform");//Name the state MoveBackwardFromPlatform

        State grabPlatform = new State(() -> {//Create a new State, grabPlatform
            //Grab the foundation
            foundationGrabber.setPosition(1);
            foundationGrabber2.setPosition(0);
            return false;
        }, () -> { //When the state is done
            stateMachine.addState(pullBackFoundation); //Run pullBackFoundation
        }, 1000, "GrabPlatform");//Name this state GrabPlatform and run for 1 second

        State moveToPlatform = new StartState(() -> {//Create a new StartState, moveToPlatform
            setTargetXYRot(foundation, 270, grabPlatform); //Move the the foundation and run grabPlatform afterward
        }, () -> true, () -> {
        }, "MoveForwardToPlatform");//Name this state MoveForwardToPlatform

        stateMachine.addState(moveToPlatform); //After we setup the states, add the first one to our stateMachine
    }
}
