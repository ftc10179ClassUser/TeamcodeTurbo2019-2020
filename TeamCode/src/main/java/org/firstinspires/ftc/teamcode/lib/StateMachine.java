package org.firstinspires.ftc.teamcode.lib;

import org.firstinspires.ftc.teamcode.lib.util.states.State;

import java.util.ArrayList;

public class StateMachine {
    ArrayList<State> states; //All of the current running states
    ArrayList<State> statesToAdd; //States that need to be added to the list of running states
    Configurator config; //Configurator instance that this StateMachine is attached to

    StateMachine(Configurator config) { //Save config & initialize array lists
        this.config = config;
        statesToAdd = new ArrayList();
        states = new ArrayList();
    }

    public void addState(State state) { //Add a state safely
        statesToAdd.add(state);
    }

    void runStates() { //Segmented into three parts so multiple things aren't done at once
        //Add states before
        for (State state: statesToAdd) {
            if (config.getDebugMode()) state.debugMode = true;
            states.add(0, state);
        }
        statesToAdd.clear();

        //Loop through and run each state efficiently
        ArrayList<State> statesToRemove = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            //If its not hidden & debug mode is on, print the state
            if (config.getDebugMode() && states.get(i).getStateName() != "Hidden") {
                config.telemetry.addLine(
                    "State: " + states.get(i).getStateName() + "(" + states.get(i).getAvgRuntime() + "ms)"
                );
            }
            //Run the state and be ready to remove it if it wants to be removed
            if (states.get(i).execute()) statesToRemove.add(states.get(i));
        }

        //Remove after
        for (State state: statesToRemove) {
            states.remove(state);
        }
    }
}