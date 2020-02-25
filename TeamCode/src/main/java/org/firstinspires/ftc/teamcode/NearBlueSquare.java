package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.lib.AutonomousLibrary;
import org.firstinspires.ftc.teamcode.lib.util.states.SingleState;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@Autonomous(name = "NearBlueSquare")
public class NearBlueSquare extends AutonomousLibrary {//Note: states are backwards, the one at the end runs first
    Servo claw;
    Servo claw2;

    @Override
    public void setupOpMode(){
        //Get the claw servos and open it
        claw = getServo("claw");
        claw2 = getServo("claw2");
        claw.setPosition(0);
        claw2.setPosition(1);

        State strafeLeftToSkybridge = new SingleState(() -> {
            moveRightCentimeters(-85, -0.5);//Strafe left 85 cm at 0.5 speed to the skybridge
        }, "StrafeLeftToSkybridge");

        State releaseStone = new State(() -> {
            //Open the claw servo
            claw.setPosition(0);
            claw.setPosition(1);

            return false;
        }, () ->{
            stateMachine.addState(strafeLeftToSkybridge);
        },1000,"ReleaseStone");

        State strafeRightToFoundation = new SingleState(() -> {
            //Move 185cm right at 0.5 speed past the skybridge, and run releaseStone afterward
            moveRightCentimeters(185, 0.5, releaseStone);
        },"StrafeRightToFoundation");

        State moveBackFromStones = new SingleState(() -> {
            //Move backwards 5cm at 0.5 speed from the stones, and run strafeRightToSkybridge afterward
            moveForwardCentimeters(-5, -0.5, strafeRightToFoundation);
        }, "MoveBackFromStones");

        State grabStone = new State(() -> {
            //Close the claw servo
            claw.setPosition(1);
            claw.setPosition(0);

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
