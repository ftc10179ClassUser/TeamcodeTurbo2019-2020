package org.firstinspires.ftc.teamcode.lib;

import org.firstinspires.ftc.teamcode.lib.util.debug.TimeTracker;
import org.firstinspires.ftc.teamcode.lib.util.states.State;

import java.util.ArrayList;

public class StateMachine {
    ArrayList<State> states; //All of the current running states
    ArrayList<State> statesToAdd; //States that need to be added to the list of running states
    Configurator config; //Configurator instance that this StateMachine is attached to
    boolean paused; //Ask if the state machine is paused
    boolean firstPause = true;
    String[] stackTrace = null;

    StateMachine(Configurator config) {
        //Save the config & initialize arrays
        this.config = config;
        statesToAdd = new ArrayList<>();
        states = new ArrayList<>();
    }

    public void addState(State state) { //Add a state safely
        statesToAdd.add(state);
    }

    void runStates() {
        if (stackTrace == null) {
            //Be able to pause the states if we're in debug mode
            if (config.getDebugMode() && config.gamepad1.x && config.gamepad1.y) {
                config.wheelController.stopWheels();
                if (firstPause) {
                    paused = !paused;
                }
                firstPause = false;
            } else {
                firstPause = true;
            }

            if (!paused) {
                //Add states before for thread safety
                for (State state : statesToAdd) {
                    if (config.getDebugMode()) state.debugMode = true;
                    states.add(0, state);
                }
                statesToAdd.clear();

                //Loop through each state
                ArrayList<State> statesToRemove = new ArrayList<>();
                for (int i = 0; i < states.size(); i++) {
                    if (config.getDebugMode()/* && states.get(i).getStateName() != "Hidden"*/) { //Show in telemetry unless its hidden
                        config.telemetry.addLine(
                                "-- " + states.get(i).getStateName() + "(avg = " + states.get(i).getAvgRuntime() + "ms) --"
                        );
                    }

                    try {
                        if (states.get(i).execute()) {
                            statesToRemove.add(states.get(i)); //Run the state and remove if it wants to be
                        }
                    } catch (Exception e) {
                        StackTraceElement[] elements = e.getStackTrace();
                        stackTrace = new String[elements.length];
                        for (int j = 0; j < elements.length; j++) {
                            stackTrace[j] = "Class: " + elements[j].getClassName() + " Method: " + elements[j].getMethodName() + " (" + elements[j].getLineNumber() + ")";
                        }
                    }
                }

                //Remove states after for thread safety
                for (State state : statesToRemove) {
                    states.remove(state);
                }
            }
        } else {
            config.telemetry.addLine("YAY! User code threw an exception!");
            config.telemetry.addLine("-- Begin Stack Trace --");
            for (String s : stackTrace) {
                config.telemetry.addLine();
            }
            config.telemetry.addLine("-- End Stack Trace --");
        }
    }
}