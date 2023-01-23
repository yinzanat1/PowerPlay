package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class DriveTrain {
        // properties of the DriveTrain class
    private DcMotorEx[] rightMotor;
    private DcMotorEx[] leftMotor;
    private DcMotorEx[] frontMotor;
    private DcMotorEx[] backMotor;
/*
    private DcMotorEx rightMotorF;
    private DcMotorEx leftMotorF;
    private DcMotorEx rightMotorB;
    private DcMotorEx leftMotorB;
*/
    public DcMotorEx rightMotorF;
    public DcMotorEx leftMotorF;
    public DcMotorEx rightMotorB;
    public DcMotorEx leftMotorB;

    private int driveType;
    private float wheelCircumference = 11.2192926146f; // 96mm diameter wheels circumference in inches
    private float gearRatio = 20;

    private double leftPower = 0.0;
    private double rightPower = 0.0;
    private double frontPower = 0.0;
    private double backPower = 0.0;
    private double leftPowerF = 0.0;
    private double rightPowerF = 0.0;
    private double leftPowerB = 0.0;
    private double rightPowerB = 0.0;

    public boolean timerOn = false;

    //variables for finding where you are
    public Double distanceSavedNS = 0.0;
    public Double distanceNowNS = 0.0;
    public Double elapsedDistanceNS = 0.0;
    public Double distanceSavedEW = 0.0;
    public Double distanceNowEW = 0.0;
    public Double elapsedDistanceEW = 0.0;

    public Double distanceSavedNESW = 0.0;
    public Double distanceNowNESW = 0.0;
    public Double elapsedDistanceNESW = 0.0;

    public Double distanceSavedNWSE = 0.0;
    public Double distanceNowNWSE = 0.0;
    public Double elapsedDistanceNWSE = 0.0;

    public Double xSaved = 0.0;
    public Double ySaved = 0.0;

    ArrayList <Integer> soggyWafflesScheduling = new ArrayList <Integer>();
    int firstValue = -1;
    boolean pressed = false;
    boolean completed = false;
    boolean singlePress = false;

    // constructor
    public DriveTrain (DcMotor motor1, DcMotor motor2) {
        rightMotor[0] = (DcMotorEx)motor1;
        leftMotor[0] = (DcMotorEx)motor2;
        driveType = 0;
    }
    public DriveTrain (DcMotor motor1, DcMotor motor2, int driveTypeIn) {
        rightMotor[0] = (DcMotorEx)motor1;
        leftMotor[0] = (DcMotorEx)motor2;
        driveType = driveTypeIn;
    }
    public DriveTrain (DcMotor[] motor1, DcMotor[] motor2, int driveTypeIn) {
        rightMotor = new DcMotorEx[motor1.length];
        leftMotor = new DcMotorEx[motor2.length];
        for (int ii = 0; ii < motor1.length; ii++) {
            rightMotor[ii] = (DcMotorEx) motor1[ii];
        }
        for (int ii = 0; ii < motor2.length; ii++) {
            leftMotor[ii] = (DcMotorEx) motor2[ii];
        }
        driveType = driveTypeIn;

        distanceSavedEW = 0.0;
        distanceNowEW = 0.0;
        elapsedDistanceEW = 0.0;
        distanceSavedNS = 0.0;
        distanceNowNS = 0.0;
        elapsedDistanceNS = 0.0;
    }
            // added for x drive
    public DriveTrain (DcMotor motor1, DcMotor motor2, DcMotor motor3, DcMotor motor4, int driveTypeIn) {
        driveType = driveTypeIn;
        if (driveType == 2) {
            rightMotor = new DcMotorEx[1];
            frontMotor = new DcMotorEx[1];
            backMotor = new DcMotorEx[1];
            leftMotor = new DcMotorEx[1];
            frontMotor[0] = (DcMotorEx) motor1;
            rightMotor[0] = (DcMotorEx) motor2;
            backMotor[0] = (DcMotorEx) motor3;
            leftMotor[0] = (DcMotorEx) motor4;
        } else if (driveType == 3) {
            rightMotorF = (DcMotorEx) motor1;
            leftMotorF = (DcMotorEx) motor2;
            rightMotorB = (DcMotorEx) motor3;
            leftMotorB = (DcMotorEx) motor4;
            distanceSavedNESW = 0.0;
            distanceNowNWSE = 0.0;
            elapsedDistanceNESW = 0.0;
            distanceSavedNWSE = 0.0;
            distanceNowNESW = 0.0;
            elapsedDistanceNWSE = 0.0;
        }
    }
            // method to drive the robot
    public void setDrivePower (float powerRightY, float powerLeftY, float powerRightX, float powerLeftX) {
        if (driveType == 0) {
            // Tank Drive
            rightPower = powerRightY;
            leftPower = powerLeftY;
        } else if (driveType == 1) {
            //Arcade Drive
            rightPower = powerLeftY + powerLeftX;
            leftPower = powerLeftY - powerLeftX;
        } else if (driveType == -1) {
            // ZK - 2/12/2022 - Zacharian Hybrid Drive (Weird)
            rightPower = powerRightY + powerRightX;
            leftPower = powerLeftY - powerLeftX;
        } else if (driveType == 2) {
            // LB - 9/17/2022 - X-Drive
            double powerVar = 0.25;
            frontPower = powerLeftX + (powerRightX*powerVar);
            rightPower = powerLeftY + (powerRightX*powerVar);
            backPower = powerLeftX - (powerRightX*powerVar);
            leftPower = powerLeftY - (powerRightX*powerVar);
        } else if (driveType == 3) {
                    // left stick moves the robot, right stick rotates the robot (with x, y does nothing)
            //ZK - 9/24/2022 - Mecanum Drive
            double leftX = powerLeftX * 1.1;
            double denominator = Math.max(Math.abs(powerLeftY) + Math.abs(leftX) + Math.abs(powerRightX), 1);
            rightPowerF = (powerLeftY - leftX - powerRightX) / denominator;
            leftPowerF = (powerLeftY + leftX + powerRightX) / denominator;
            rightPowerB = (powerLeftY + leftX - powerRightX) / denominator;
            leftPowerB = (powerLeftY - leftX + powerRightX) / denominator;
        } else {
            //Default to Tank Drive
            rightPower = powerRightY;
            leftPower = powerLeftY;
        }
        setMotorPowers();
    }

    public void dPadDrive (boolean upPressed, boolean downPressed, boolean leftPressed, boolean rightPressed) {
        if (upPressed) {
            rightPowerF = 0.5;
            leftPowerF = 0.5;
            rightPowerB = 0.5;
            leftPowerB = 0.5;
        } else if (downPressed) {
            rightPowerF = -0.5;
            leftPowerF = -0.5;
            rightPowerB = -0.5;
            leftPowerB = -0.5;
        } else if (leftPressed) {
            rightPowerF = 0.5;
            leftPowerF = -0.5;
            rightPowerB = -0.5;
            leftPowerB = 0.5;
        } else if (rightPressed) {
            rightPowerF = -0.5;
            leftPowerF = 0.5;
            rightPowerB = 0.5;
            leftPowerB = -0.5;
        } else {
            rightPowerF = 0;
            leftPowerF = 0;
            rightPowerB = 0;
            leftPowerB = 0;
        }
        setMotorPowers();
    }

    public void setDriveTime (float driveForTime, float drivePowerEW, float drivePowerNS, float drivePowerRot/*, int timeDrivetype*/) {
//        Thread.sleep((long) (driveForTime * 1000));

        Timer timer = new Timer();
        timerOn = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerOn = false;
                rightPowerF = 0;
                rightPowerB = 0;
                leftPowerF = 0;
                leftPowerB = 0;
                setMotorPowers();
            }
        }, (long) (driveForTime * 1000));
        if (drivePowerNS != 0) {
            rightPowerF = drivePowerNS;
            rightPowerB = drivePowerNS;
            leftPowerF = drivePowerNS;
            leftPowerB = drivePowerNS;
        }
        if (drivePowerEW != 0) {
            rightPowerF = -1 * drivePowerEW;
            rightPowerB = drivePowerEW;
            leftPowerF = drivePowerEW;
            leftPowerB = -1 * drivePowerEW;
        }
        if (drivePowerRot != 0) {
            rightPowerF = drivePowerRot;
            rightPowerB = drivePowerRot;
            leftPowerF = -1 * drivePowerRot;
            leftPowerB = -1 * drivePowerRot;
        }
        setMotorPowers();
    }
    public void driveByRevolution (float distance) {
        for (int ii = 0; ii < rightMotor.length; ii++) {
            rightMotor[ii].setTargetPosition(Math.round(rightMotor[ii].getCurrentPosition() + (distance / wheelCircumference) * 28 * gearRatio));
        }
        for (int ii = 0; ii < leftMotor.length; ii++) {
            leftMotor[ii].setTargetPosition(Math.round(leftMotor[ii].getCurrentPosition() + (distance / wheelCircumference) * 28 * gearRatio));
        }
        rightPower = 0.5;
        leftPower = 0.5;
        setMotorPowers();
        checkPosition();
    }

    private void checkPosition () {
        for (int ii = 0; ii < rightMotor.length; ii++) {
        if (rightMotor[ii].getTargetPosition() < rightMotor[ii].getCurrentPosition() || leftMotor[ii].getTargetPosition() < leftMotor[ii].getCurrentPosition()) {
            rightPower = 0;
            leftPower = 0;
            setMotorPowers();
        } else {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkPosition();
                }
            }, 10);
        }
        }
    }

    private void setMotorPowers () {
/*
        for (int ii = 0; ii < rightMotor.length; ii++) {
            rightMotor[ii].setPower(rightPower);
        }
        for (int ii = 0; ii < leftMotor.length; ii++) {
            leftMotor[ii].setPower (leftPower);
        }
        for (int ii = 0; ii < frontMotor.length; ii++) {
            frontMotor[ii].setPower (frontPower);
        }
        for (int ii = 0; ii < backMotor.length; ii++) {
            backMotor[ii].setPower (backPower);
        }
*/
        if (driveType == 3) {
            rightMotorF.setPower(rightPowerF);
            leftMotorF.setPower (leftPowerF);
            rightMotorB.setPower(rightPowerB);
            leftMotorB.setPower (leftPowerB);
        }
    }

    public int soggyWafflesScheduler (boolean upPressed, boolean downPressed, boolean rightPressed, boolean leftPressed, boolean taskCompleteIn) {
        //Moves the schedule down
        if (!completed) {
            if (taskCompleteIn && soggyWafflesScheduling.size() > 0) {
                firstValue = soggyWafflesScheduling.get(0);
                soggyWafflesScheduling.remove(0);
                completed = true;
            }
        } else if (!taskCompleteIn) {
            completed = false;
        }
        pressed = upPressed || downPressed || rightPressed || leftPressed;
        //Adds to the schedule
        if (!singlePress) {
            if (pressed) {
                singlePress = true;
                if (upPressed) {
                    soggyWafflesScheduling.add(0);
                } else if (downPressed) {
                    soggyWafflesScheduling.add(3);
                } else if (rightPressed) {
                    soggyWafflesScheduling.add(1);
                } else if (leftPressed) {
                    soggyWafflesScheduling.add(2);
                }
            }
        } else if (!pressed) {
            singlePress = false;
        }
        //get rid of last two entries in schedule if they counteract each other
        if (soggyWafflesScheduling.size() > 1 && soggyWafflesScheduling.get(soggyWafflesScheduling.size() - 2) + soggyWafflesScheduling.get(soggyWafflesScheduling.size() - 1) == 3) {
            soggyWafflesScheduling.remove(soggyWafflesScheduling.size() - 1);
            soggyWafflesScheduling.remove(soggyWafflesScheduling.size() - 1);
        }
        return firstValue;
 //       return soggyWafflesScheduling.size();
    }

   /* public double returnAverageInchesEW () {
        int position = 0;
        for (int ii = 0; ii < rightMotor.length; ii++) {
            position = position + rightMotor[ii].getCurrentPosition();
        }
        for (int ii = 0; ii < leftMotor.length; ii++) {
            position = position + leftMotor[ii].getCurrentPosition();
        }
        return (((position / (rightMotor.length + leftMotor.length)) / gearRatio) / 28) * wheelCircumference;
    }

    public double returnAverageInchesNS () {
        int position = 0;
        for (int ii = 0; ii < frontMotor.length; ii++) {
            position = position + frontMotor[ii].getCurrentPosition();
        }
        for (int ii = 0; ii < backMotor.length; ii++) {
            position = position + backMotor[ii].getCurrentPosition();
        }
        return (((position / (frontMotor.length + backMotor.length)) / gearRatio) / 28) * wheelCircumference;
    }
*/
    public double returnAverageInchesNWSE () {
        return (((((leftMotorF.getCurrentPosition() + rightMotorB.getCurrentPosition()) / 2) / gearRatio) / 28) * wheelCircumference);
    }

    public double returnAverageInchesNESW () {
        return (((((leftMotorB.getCurrentPosition() + rightMotorF.getCurrentPosition()) / 2) / gearRatio) / 28) * wheelCircumference);
    }

    public void whereAmI (float orientationNow) {
        if (driveType == 2) {
           /* distanceSavedNS = distanceNowNS;
            distanceNowNS = returnAverageInchesNS();
            elapsedDistanceNS = distanceNowNS - distanceSavedNS;
            xSaved = xSaved + (Math.round(((Math.sin(orientationNow) * elapsedDistanceNS) / 0.024)) / 1000.0);
            ySaved = ySaved + (Math.round(((Math.cos(orientationNow) * elapsedDistanceNS) / 0.024)) / -1000.0);
            distanceSavedEW = distanceNowEW;
            distanceNowEW = returnAverageInchesEW();
            elapsedDistanceEW = distanceNowEW - distanceSavedEW;
            ySaved = ySaved + (Math.round(((Math.sin(orientationNow) * elapsedDistanceEW) / 0.024)) / 1000.0);
            xSaved = xSaved + (Math.round(((Math.cos(orientationNow) * elapsedDistanceEW) / 0.024)) / -1000.0); */
        } else if (driveType == 3) {
            distanceSavedNESW = distanceNowNESW;
            distanceNowNESW = returnAverageInchesNESW();
            elapsedDistanceNESW = distanceNowNESW - distanceSavedNESW;

            xSaved = xSaved + (Math.round(((Math.sin(orientationNow - 45) * elapsedDistanceNESW) / 0.024)) / 1000.0);  //use / 0.024 to make the unit tiles
            ySaved = ySaved + (Math.round(((Math.cos(orientationNow - 45) * elapsedDistanceNESW) / 0.024)) / -1000.0);
            distanceSavedNWSE = distanceNowNWSE;
            distanceNowNWSE = returnAverageInchesNWSE();
            elapsedDistanceNWSE = distanceNowNWSE - distanceSavedNWSE;
            ySaved = ySaved + (Math.round(((Math.sin(orientationNow + 45) * elapsedDistanceNWSE) / 0.024)) / 1000.0);
            xSaved = xSaved + (Math.round(((Math.cos(orientationNow + 45) * elapsedDistanceNWSE) / 0.024)) / -1000.0);
        } else {
            /*distanceSavedEW = distanceNowEW;
            distanceNowEW = returnAverageInchesEW();
            elapsedDistanceEW = distanceNowEW - distanceSavedEW;
            ySaved = ySaved + (Math.round(((Math.sin(orientationNow) * elapsedDistanceEW) / 0.024)) / 1000.0);
            xSaved = xSaved + (Math.round(((Math.cos(orientationNow) * elapsedDistanceEW) / 0.024)) / -1000.0); */
        }
    };
/*      FWF - this is causing an error on init of the drive train.
    int numberOfTests = 0;
    double currentAverageRPM = 0;
    double storedPosition = rightMotor[0].getCurrentPosition();
    double currentDistanceMoved = 0;
    public double getMaxRPMs () {
        return 123684;
        / *
        rightPower = 1;
        leftPower = 1;
        setMotorPowers();
        while (numberOfTests < 10){
            currentAverageRPM = ((currentAverageRPM * numberOfTests) + rightMotor[0].getVelocity()) / (numberOfTests + 1);
            numberOfTests++;
        }
        rightPower = 0;
        leftPower = 0;
        setMotorPowers();
        return currentAverageRPM; * /
    }
*/
}
