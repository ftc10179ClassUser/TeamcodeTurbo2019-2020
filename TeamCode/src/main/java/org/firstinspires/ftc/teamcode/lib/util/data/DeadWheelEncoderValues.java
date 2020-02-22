package org.firstinspires.ftc.teamcode.lib.util.data;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.lib.Configurator;
import org.firstinspires.ftc.teamcode.lib.DeadWheelOdometer;

public class DeadWheelEncoderValues {
    public double left = 0;
    public double center = 0;
    public double right = 0;

    public DeadWheelEncoderValues() {
    }

    public DeadWheelEncoderValues(double left, double center, double right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    // Derive current odometry values directly from configurator/encoder values.
    public DeadWheelEncoderValues(Configurator configurator) {
        DcMotor leftOdometer = configurator.getDcMotor("leftOdometer");
        DcMotor centerOdometer = configurator.getDcMotor("centerOdometer");
        DcMotor rightOdometer = configurator.getDcMotor("rightOdometer");

        this.left = leftOdometer.getCurrentPosition();
        this.center = centerOdometer.getCurrentPosition();
        this.right = rightOdometer.getCurrentPosition();
    }

    public DeadWheelEncoderValues subtractAndReturn(DeadWheelEncoderValues deadWheelEncoderValues) {
        return new DeadWheelEncoderValues(
                left - deadWheelEncoderValues.left,
                center - deadWheelEncoderValues.center,
                right - deadWheelEncoderValues.right);
    }

    public DeadWheelEncoderValues addAndReturn(DeadWheelEncoderValues deadWheelEncoderValues) {
        return new DeadWheelEncoderValues(
                left + deadWheelEncoderValues.left,
                center + deadWheelEncoderValues.center,
                right + deadWheelEncoderValues.right);
    }

    public String toString() {
        String format_command = "%8.1f";
        return String.format(format_command,left) + ",  " + String.format(format_command,center) + ", " + String.format(format_command,right) + "";
    }

    public DeadWheelEncoderValues copy() {
        return new DeadWheelEncoderValues(this.left,this.center,this.right);
    }
}
