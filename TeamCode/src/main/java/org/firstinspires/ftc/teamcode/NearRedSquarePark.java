package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.lib.AutonomousLibrary;
import org.firstinspires.ftc.teamcode.lib.util.data.PVector;
import org.firstinspires.ftc.teamcode.lib.util.states.SingleState;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@Autonomous(name="NearRedSquarePark")
public class NearRedSquarePark extends AutonomousLibrary {//Note: States run backwards, the one at the bottom runs first
    public void setupOpMode(){

        //Initialize starting position and rotation
        PVector startingPos = new PVector(22.86,83.82);
        initializeOdometry(startingPos,90);

        //Set way points
        PVector underSkybridge = new PVector(96.06, 182.88);

        State strafeRightUnderSkybridge = new SingleState(() -> {//Creates a new SingleState, strafeRightUnderSkybridge
            setTargetXYRot(underSkybridge, 90);//Navigate
        }, "StrafeRightUnderSkybridge");//Name the state StrafeRightUnderSkybridge

        stateMachine.addState(strafeRightUnderSkybridge);//Passes goToStone into the state machine, calling it
    }
}
