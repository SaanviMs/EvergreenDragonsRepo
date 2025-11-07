package org.firstinspires.ftc.teamcode.RoadRunner;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.RoadRunner.MecanumDrive;

@Autonomous(name = "Auto: Limelight Motif Scan", group = "vision")
public class LimelightMotifAuto extends LinearOpMode {

    private Limelight3A limelight;
    private MecanumDrive drive;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize hardware
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
       //drive = new MecanumDrive(hardwareMap);

        // Turn on LED and set pipeline for AprilTags
        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry.addLine("Limelight initialized — waiting for tag...");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        int tagID = -1;
        LLResult result;

        // Try for up to 3 seconds to find a tag
        double startTime = getRuntime();
        while (opModeIsActive() && getRuntime() - startTime < 3.0) {
            result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                telemetry.addData("Detected Tag ID", tagID);
                telemetry.update();
                break;
            } else {
                telemetry.addLine("No tag yet...");
                telemetry.update();
            }
        }

        // Default pattern if not found
        if (tagID == -1) {
            telemetry.addLine("No AprilTag detected! Defaulting to Blue Center.");
            tagID = 21; // default to center
        }

        // Determine pattern + drive target
        String pattern = "Unknown";
        Pose2d targetPose = new Pose2d(0, 0, 0);

        switch (tagID) {
            case 21:
                pattern = "Green Purple Purple";
                targetPose = new Pose2d(35.5, -30, Math.toRadians(180));
                break;
            case 22:
                pattern = "Purple Green Purple";
                targetPose = new Pose2d(35, -30, Math.toRadians(180));
                break;
            case 23:
                pattern = "Purple Purple Green";
                targetPose = new Pose2d(50, -30, Math.toRadians(180));
                break;
            default:
                pattern = "Default Center";
                targetPose = new Pose2d(35, 0, Math.toRadians(0));
        }

        telemetry.addData("Motif Pattern", pattern);
        telemetry.addData("Driving to", targetPose);
        telemetry.update();

        // Use Road Runner to drive to that location
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(64, -8, 0)) //drives to first pixel mark
                        .strafeTo(targetPose.component1())


                        .build()
        );

        telemetry.addLine("Arrived at target motif!");
        telemetry.update();

        // optional: perform collection or scoring here
        sleep(1000);
    }
}
