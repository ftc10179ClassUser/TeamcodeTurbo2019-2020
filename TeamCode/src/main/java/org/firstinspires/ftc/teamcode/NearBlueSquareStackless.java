package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.lib.AutonomousLibrary;
import org.firstinspires.ftc.teamcode.lib.util.states.SingleState;
import org.firstinspires.ftc.teamcode.lib.util.states.StartState;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@Autonomous(name="NearBlueSquareStackless")
public class NearBlueSquareStackless extends AutonomousLibrary {//Note: states are backwards, the one at the end runs first
    Servo claw;

    @Override
    public void setupOpMode(){
        //Get the claw servo and open it
        claw = getServo("claw");
        claw.setPosition(0);

        State strafeLeftToSkybridge = new SingleState(() -> {
            moveRightCentimeters(-22, -0.5);//Strafe left 22 cm at 0.5 speed to the skybridge
        }, "StrafeLeftToSkybridge");

        State releaseStone = new State(() -> {
            claw.setPosition(0);//Open the claw servo
            return false;
        }, () ->{
            stateMachine.addState(strafeLeftToSkybridge);
        },1000,"ReleaseStone");

        State strafeRightPastSkybridge = new SingleState(() -> {
            //Move 122cm right at 0.5 speed past the skybridge, and run releaseStone afterward
            moveRightCentimeters(122, 0.5, releaseStone);
        },"StrafeRightPastSkybridge");

        State moveBackFromStones = new SingleState(() -> {
            //Move backwards 5cm at 0.5 speed from the stones, and run strafeRightToSkybridge afterward
            moveForwardCentimeters(-5, -0.5, strafeRightPastSkybridge);
        }, "MoveBackFromStones");

        State grabStone = new State(() -> {
            claw.setPosition(1);//Close the claw servo
            return false;
        }, () -> {//When this state is done
            stateMachine.addState(moveBackFromStones);//Run moveBackFromStones
        }, 2000, "GrabStone");

        State moveForwardToStone = new SingleState(() -> {
            //Move forward 75cm at 0.5 speed to the stone, and run grabStone afterward
            moveForwardCentimeters(75, 0.5, grabStone);
        }, "MoveForwardToStone");

        State strafeRightToAlign = new SingleState(() -> {
            //Strafe right 7cm at 0.5 speed to align with a stone, and run moveForwardToStone afterward
            moveRightCentimeters(7, 0.5, moveForwardToStone);
        }, "StrafeRightToAlign");

        stateMachine.addState(strafeRightToAlign);//After we set up the states, we add the first one to our state machine
    }
}