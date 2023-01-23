package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.bosch.BNO055IMU;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Random;
import android.content.Context;
import java.util.Date;
import android.os.Environment;

@TeleOp
public class PowerPlay extends LinearOpMode{
    private DcMotorEx northEastMotor;
    private DcMotorEx southEastMotor;
    private DcMotorEx southWestMotor;
    private DcMotorEx northWestMotor;
    private DcMotorEx chainTop;
    private DcMotorEx chainBottom;
    private BNO055IMU emuIMU1;
    private BNO055IMU emuIMU2;
    private Servo claw;
    private Servo flipper;
    private Long eTime =0L;

    @Override
    public void runOpMode() {
        Context context = hardwareMap.appContext;
        String configFileName = "MotorData.csv";

        northEastMotor = hardwareMap.get(DcMotorEx.class, "northeastMotor");
        northWestMotor = hardwareMap.get(DcMotorEx.class, "northwestMotor");
        southEastMotor = hardwareMap.get(DcMotorEx.class, "southeastMotor");
        southWestMotor = hardwareMap.get(DcMotorEx.class, "southwestMotor");
        southWestMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        northWestMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        DriveTrain myDrive = new DriveTrain(northWestMotor, northEastMotor, southWestMotor, southEastMotor, 3);

        //initiates chain box
        chainTop = hardwareMap.get(DcMotorEx.class, "chainTop");
        chainBottom = hardwareMap.get(DcMotorEx.class, "chainBottom");
        chainTop.setDirection(DcMotorSimple.Direction.REVERSE);
        chainBottom.setDirection(DcMotorSimple.Direction.REVERSE);
        ChainBox boxerDoggo = new ChainBox(chainTop, chainBottom);

        //initiates claw
        claw = hardwareMap.get(Servo.class, "claw1");
        boolean clawFlag = false;

        // cone flipper
        flipper = hardwareMap.get(Servo.class, "flipper");
        boolean flipFlag = false;

        //Initiates two IMUs for the control and expansion hubs
        emuIMU1 = hardwareMap.get(BNO055IMU.class, "imu1");
        emuIMU2 = hardwareMap.get(BNO055IMU.class, "imu2");
        MxHeroXYZ tuxEmu1 = new MxHeroXYZ(emuIMU1);
        MxHeroXYZ tuxEmu2 = new MxHeroXYZ(emuIMU2);

        Date date = new Date();
        telemetry.addData("start", "start is started");
        OutputStreamWriter dataFile = null;
        try {
            dataFile = new OutputStreamWriter(context.openFileOutput(configFileName, Context.MODE_PRIVATE));
            telemetry.addData("close", "file is opened " + configFileName);
            telemetry.addData("file location", "file is in: " + context.getFilesDir());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            telemetry.addData("error", "error creating the file");
        }
        try {
            dataFile.write("helllo");
            telemetry.addData("file", "file is written");
        } catch (Exception e) {
            telemetry.addData("error", "error writing the file");
        }
        try {
            dataFile.close();
            telemetry.addData("close", "file is closed");
        } catch (IOException e) {
            e.printStackTrace();
            telemetry.addData("error", "error closing the file");
        }
telemetry.update();
        waitForStart();
        myDrive.xSaved = 0.0;
        myDrive.ySaved = 0.0;
        eTime = date.getTime();
        telemetry.speak("I Have the Power! Rangers");
        telemetry.update();

        while (opModeIsActive()) {
            //myDrive.setDrivePower(gamepad1.right_stick_y, gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
            myDrive.dPadDrive(gamepad1.dpad_up, gamepad1.dpad_down, gamepad1.dpad_left, gamepad1.dpad_right);
            if (gamepad2.left_bumper) {
                boxerDoggo.runChainBox(gamepad2.right_stick_y);
            } else {
                boxerDoggo.clawUpDown(gamepad2.right_stick_y);
            }

            if (gamepad2.a) {
                if (!clawFlag) {
                    clawFlag = true;
                    claw.setPosition(1 - claw.getPosition());
                }
            } else {
                clawFlag = false;
            }

            if (gamepad2.b) {
                if (!flipFlag) {
                    flipFlag = true;
                    flipper.setPosition(1 - flipper.getPosition());
                }
            } else {
                flipFlag = false;
            }

            /**/
            try {
                dataFile.write(Long.toString(date.getTime())+","+
                        Integer.toString(myDrive.leftMotorF.getCurrentPosition())+","+
                        Integer.toString(myDrive.rightMotorF.getCurrentPosition())+","+
                        Integer.toString(myDrive.leftMotorB.getCurrentPosition())+","+
                        Integer.toString(myDrive.rightMotorB.getCurrentPosition())+
                        "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
/**/
            telemetry.addLine(Long.toString(date.getTime())+","+
                    Integer.toString(myDrive.leftMotorF.getCurrentPosition())+","+
                    Integer.toString(myDrive.rightMotorF.getCurrentPosition())+","+
                    Integer.toString(myDrive.leftMotorB.getCurrentPosition())+","+
                    Integer.toString(myDrive.rightMotorB.getCurrentPosition()));
            telemetry.addData("file location", "file is in: " + context.getFilesDir());
            telemetry.update();
        }
        try {
            dataFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

