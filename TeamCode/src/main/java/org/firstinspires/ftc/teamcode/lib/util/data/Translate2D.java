package org.firstinspires.ftc.teamcode.lib.util.data;

public class Translate2D {
    public PVector position = new PVector(0,0);
    public double theta = 0;

    public Translate2D(PVector position, double theta) {
        this.position = position.copy();
        this.theta = theta;
    }

    public Translate2D(double x, double y, double theta) {
        this.position = new PVector(x,y);
        this.theta = theta;
    }

    public double getX() {
        return position.x;
    }

    public double getY() {
        return position.y;
    }

    public double getTheta() {
        return theta;
    }

    public String toString() {
        String format_command = "%8.2f";
        return String.format(format_command,position.x) + ",  " + String.format(format_command,position.y) + ", " + String.format(format_command,theta) + "";
    }
}