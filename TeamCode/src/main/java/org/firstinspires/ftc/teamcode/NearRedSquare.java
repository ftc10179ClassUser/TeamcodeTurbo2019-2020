package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.lib.AutonomousLibrary;
import org.firstinspires.ftc.teamcode.lib.util.states.SingleState;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@Autonomous(name="NearRedSquare")
public class NearRedSquare extends AutonomousLibrary {//Note: states are backwards, the one at the end runs first
    Servo claw;
    Servo claw2;

    @Override
    public void setupOpMode(){
        //Get the claw servos and open it
        claw = getServo("claw");
        claw2 = getServo("claw2");
        claw.setPosition(0);
        claw2.setPosition(1);

        State strafeRightToSkybridge = new SingleState(() -> {
            moveRightCentimeters(85, 0.5);//Strafe right 85 cm at 0.5 speed to the skybridge
        }, "StrafeRightToSkybridge");

        State releaseStone = new State(() -> {
            claw.setPosition(0);//Open the claw servo
            return false;
        }, () ->{
            stateMachine.addState(strafeRightToSkybridge);
        },1000,"ReleaseStone");

        State strafeLeftToFoundation = new SingleState(() -> {
            //Move 185cm left at 0.5 speed to the foundation, and run releaseStone afterward
            moveRightCentimeters(-185, -0.5, releaseStone);
        },"StrafeToFoundationSkybridge");

        State moveBackFromStones = new SingleState(() -> {
            //Move backwards 5cm at 0.5 speed from the stones, and run strafeRightToSkybridge afterward
            moveForwardCentimeters(-5, -0.5, strafeLeftToFoundation);
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

        State strafeLeftToAlign = new SingleState(() -> {
            //Strafe left 7cm at 0.5 speed to align with a stone, and run moveForwardToStone afterward
            moveRightCentimeters(7, 0.5, moveForwardToStone);
        }, "StrafeLeftToAlign");

        stateMachine.addState(strafeLeftToAlign);//After we set up the states, we add the first one to our state machine
    }
}
