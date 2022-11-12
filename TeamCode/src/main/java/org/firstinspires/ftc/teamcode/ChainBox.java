package org.firstinspires.ftc.teamcode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class ChainBox {
    public DcMotorEx chainBoxTop;
    public DcMotorEx chainBoxBottom;
    // constructor
    public ChainBox (DcMotor motor1, DcMotor motor2) {
        chainBoxTop = (DcMotorEx)motor1;
        chainBoxBottom = (DcMotorEx)motor2;
    }
    //basic motor run
    public void runChainBox (float chainY, float clawY) {
        chainBoxTop.setPower(0.5 * (chainY - clawY));
        chainBoxBottom.setPower(0.5 * (chainY + clawY));
    }
}
