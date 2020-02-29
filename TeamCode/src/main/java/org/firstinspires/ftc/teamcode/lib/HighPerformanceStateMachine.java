package org.firstinspires.ftc.teamcode.lib;

import org.firstinspires.ftc.teamcode.lib.util.states.State;

import java.util.ArrayList;

public class HighPerformanceStateMachine {
    int maxStates = 10;
    State[] states; //All of the current running states
    State[] statesToAdd; //States that need to be added to the list of running states
    int runningStates = 0;
    int addingStates = 0;
    Configurator config; //Configurator instance that this StateMachine is attached to
    public boolean paused; //Ask if the state machine is paused
    boolean firstPause = true;

    HighPerformanceStateMachine(Configurator config) {
        //Save the config & initialize arrays
        this.config = config;
        statesToAdd = new State[maxStates];
        states = new State[maxStates];
    }

    public void addState(State state) { //Add a state safely
        statesToAdd[++addingStates] = state;
    }

    void runStates() {
        if (!paused) {
            //Add states before for thread safety
            for (int i = 0; i < addingStates; i++) {
                states[++runningStates] = statesToAdd[i];
            }
            addingStates = 0;

            //Loop through each state
            int[] statesToRemove = new int[maxStates];
            int removingState = 0;
            for (int i = 0; i < runningStates; i++) {
                if (states[i].execute()) {
                    statesToRemove[++removingState] = i; //Run the state and remove if it wants to be
                }
            }

            //Remove states after for thread safety
            for (int i = 0; i < removingState; i++) {
                states[statesToRemove[i]] = null;

                for (int j = 0; j < runningStates; j++) {
                    if (states[j] == null) {
                        for (int k = j+1; k < runningStates; k++) {
                            states[k-1] = states[k];
                        }
                        runningStates--;
                        break;
                    }
                }
            }
        }
    }
}