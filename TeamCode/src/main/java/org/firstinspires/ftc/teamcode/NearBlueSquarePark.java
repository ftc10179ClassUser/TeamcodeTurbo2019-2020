package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.lib.AutonomousLibrary;
import org.firstinspires.ftc.teamcode.lib.util.data.PVector;
import org.firstinspires.ftc.teamcode.lib.util.states.SingleState;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

@Autonomous(name="NearBlueSquarePark")
public class NearBlueSquarePark extends AutonomousLibrary {//Note: States run backwards, the one at the bottom runs first
    public void setupOpMode(){

        //Initialize starting position and rotation
        PVector startingPos = new PVector(342.9,83.82);
        initializeOdometry(startingPos,270);

        //Set way points
        PVector underSkybridge = new PVector(269.7, 182.88);

        State strafeLeftUnderSkybridge = new SingleState(() -> {//Creates a new SingleState, strafeLeftUnderSkybridge
            setTargetXYRot(underSkybridge, 270);//Navigate
        }, "StrafeLeftUnderSkybridge");//Name the state StrafeLeftUnderSkybridge

        stateMachine.addState(strafeLeftUnderSkybridge);//Passes goToStone into the state machine, calling it
    }
}
