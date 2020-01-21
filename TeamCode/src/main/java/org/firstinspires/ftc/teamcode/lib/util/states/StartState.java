package org.firstinspires.ftc.teamcode.lib.util.states;

import org.firstinspires.ftc.teamcode.lib.util.command.BooleanCommand;
import org.firstinspires.ftc.teamcode.lib.util.command.Command;

import static java.lang.System.currentTimeMillis;

//A modified state that runs something on it's first time
public class StartState extends State {
    private Command programOnStart; //What runs when the state is started

    //Most basic state, just a command to run indefinitely
    public StartState(Command programOnStart, BooleanCommand program, Command programOnStop) {
        super(program, programOnStop);
        this.programOnStart = programOnStart;
    }

    //A state with a command that runs for a period of time
    public StartState(Command programOnStart, BooleanCommand program, Command programOnStop, long millisToRun) {
        super(program, programOnStop, millisToRun);
        this.programOnStart = programOnStart;
    }

    //Named version of above
    public StartState(Command programOnStart, BooleanCommand program, Command programOnStop, String stateName) {
        super(program, programOnStop, stateName);
        this.programOnStart = programOnStart;
    }

    //Named version of above
    public StartState(Command programOnStart, BooleanCommand program, Command programOnStop, long millisToRun, String stateName) {
        super(program, programOnStop, millisToRun, stateName);
        this.programOnStart = programOnStart;
    }

    public static StartState blank() {
        return new StartState(() -> {}, () -> {return true;}, () -> {}, "Hidden");
    }

    public String getStateName() { //Prevent state name modification
        return stateName;
    }

    @Override
    public boolean execute() {
        boolean stop = false;
        if (firstRun) { //If this is the first time running
            firstRun = false;
            if (debugMode) timeTracker.start();
            programOnStart.execute(); //Run the program on start
            if (debugMode) timeTracker.end("execute");
            timeStarted = currentTimeMillis(); //Then set the current time is when it started
        }
        if (debugMode) timeTracker.start();
        stop = (!firstRun && currentTimeMillis() >= timeStarted + millisToRun) || program.execute(); //True if the state wants to be deactivated or time ran out
        if (stop) programOnStop.execute(); //Run what's on stop if we are stopping
        if (debugMode) timeTracker.end("execute");
        return stop;
    }
}