import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawScreenASCII {

    List<Double> amplitudes = new ArrayList<>();
    private String loadingString = "";

    public DrawScreenASCII(Camera cam, Ray[][] primaryRay, long frameTime) {
        /*ScheduledExecutorService drawScreenExecutor = Executors.newScheduledThreadPool(1);
        AtomicBoolean updateScreen = new AtomicBoolean(false);

        Runnable screenUpdateTask = () -> {
            updateScreen.set(true);
        };
        drawScreenExecutor.scheduleAtFixedRate(screenUpdateTask, 10, frameTime, TimeUnit.MILLISECONDS);*/
    }

    public void brightnessDistribution(Camera cam, Ray[][] primaryRay) {
        for (int i = 0; i < cam.getResX(); i++) {
            for (int j = 0; j < cam.getResY(); j++) {
                if (primaryRay[i][j].getLightAmplitude() != 0) { // filter out zeros
                    amplitudes.add(primaryRay[i][j].getLightAmplitude());
                }
            }
        }
        amplitudes.add(0.0);
        Collections.sort(amplitudes);
    }

    double getQuantile(List<Double> data, double quantile) {
        int index = (int) Math.ceil(quantile * data.size()) - 1;
        return data.get(Math.max(index, 0));
    }

    // ... ,,, ~~~ ::: ;;; XXX *** 000 DDD ### @@@
    public void drawScreenQuantiles(Camera cam, Ray[][] primaryRay, int loadingProgress) {

        brightnessDistribution(cam, primaryRay);

        System.out.print("|-");
        for (int l = 0; l < 99; l++) {
            System.out.print("-");
        }
        System.out.println("-|");

        double max = Collections.max(amplitudes) * cam.getISO();
        loadingString = "||";
        for (int i = 0; i < loadingProgress; i++) {
            loadingString = loadingString + "|";
        }
        loadingString = loadingString + "||";

        System.out.println(loadingString);
        System.out.println("Max Brightness: " + Collections.max(amplitudes));
        double q1 = (max * 0.08);
        double q2 = (max * 0.16);
        double q3 = (max * 0.24);
        double q4 = (max * 0.32);
        double q5 = (max * 0.40);
        double q6 = (max * 0.48);
        double q7 = (max * 0.56);
        double q8 = (max * 0.64);
        double q9 = (max * 0.72);
        double q10 = (max * 0.80);
        double q11 = (max * 0.88);
        double q12 = (max * 0.95);

        // iterate through each rays hit value and print the output
        System.out.print("|");
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("-|-");
        }
        System.out.println("|");
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getLightAmplitude() >= q12) {
                    System.out.print("@@@");
                } else if (primaryRay[i][j].getLightAmplitude() >= q11) {
                    System.out.print("DDD");
                } else if (primaryRay[i][j].getLightAmplitude() >= q10) {
                    System.out.print("000");
                } else if (primaryRay[i][j].getLightAmplitude() >= q9) {
                    System.out.print("UUU");
                } else if (primaryRay[i][j].getLightAmplitude() >= q8) {
                    System.out.print("###");
                } else if (primaryRay[i][j].getLightAmplitude() >= q7) {
                    System.out.print("ZZZ");
                } else if (primaryRay[i][j].getLightAmplitude() >= q6) {
                    System.out.print("***");
                } else if (primaryRay[i][j].getLightAmplitude() >= q5) {
                    System.out.print("xxx");
                } else if (primaryRay[i][j].getLightAmplitude() >= q4) {
                    System.out.print("~~~");
                } else if (primaryRay[i][j].getLightAmplitude() >= q3) {
                    System.out.print(";;;");
                } else if (primaryRay[i][j].getLightAmplitude() >= q2) {
                    System.out.print(":::");
                } else if (primaryRay[i][j].getLightAmplitude() >= q1) {
                    System.out.print(",,,");
                } else if (primaryRay[i][j].getLightAmplitude() > 0) {
                    System.out.print("...");
                } else if (primaryRay[i][j].getLightAmplitude() == 0) {
                    System.out.print("   ");
                }
            }
            System.out.println("|");
        }
        System.out.print("|");
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("---");
        }
        System.out.println("|");
    }

}
