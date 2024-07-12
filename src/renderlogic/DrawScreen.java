package renderlogic;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sceneobjects.*;

public class DrawScreen extends JPanel {

    List<Double> amplitudesRed = new ArrayList<>();
    List<Double> amplitudesGreen = new ArrayList<>();
    List<Double> amplitudesBlue = new ArrayList<>();

    private boolean denoise;
    private double screenWidth, screenHeight;
    private double scalingFactor;
    private int outputWidth, outputHeight;
    private int internalWidth, internalHeight;
    private BufferedImage image;
    private JFrame window = new JFrame("Path Tracer - " + "Resolution: " + Main.RenderResolutionX + "x" + (Main.RenderResolutionX / ((double) Main.aspectX / (double) Main.aspectY)) + " - Rays: " + Main.raysPerPixel + " - Bounces: " + Main.bouncesPerRay + " - Ray Step Size: " + Main.secondaryRayStep + " - Denoising: " + Main.denoise);
    private double brightnessFactor = 0;
    private boolean toneMapping = false;

    private float lineSpacing = 1f;
    private int fontSize = 12;
    private JTextPane areaASCII;

    public DrawScreen(int width, int height, Camera cam) {
        internalWidth = width;
        internalHeight = height;

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth(); // get screen width and height
        screenHeight = gd.getDisplayMode().getHeight();

        scalingFactor = (screenHeight * 0.90) / internalHeight;
        outputWidth = (int) (internalWidth * scalingFactor);
        outputHeight = (int) (internalHeight * scalingFactor);

        // initialize the canvas with specified width and height

        if (Main.ASCIIMode == false) {
            image = new BufferedImage(outputWidth + 5, outputHeight, BufferedImage.TYPE_INT_RGB);
            window.add(this);
            window.setSize(outputWidth, outputHeight);
            window.setVisible(true);
        } else if (Main.ASCIIMode == true) {
            areaASCII = new JTextPane();
            setLayout(new BorderLayout());
            int fontSize = (int) (scalingFactor * 0.85);
            areaASCII.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
            areaASCII.setForeground(Color.getHSBColor((float) 0.3, (float) 0.37, (float) 0.61)); // matrix green
            areaASCII.setBackground(Color.BLACK);
            add(new JScrollPane(areaASCII), BorderLayout.CENTER);
            window.add(this);
            window.setSize(outputWidth, outputHeight);
            window.setVisible(true);
        }
        // general stuff
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void updateResolution(int width, int height, boolean ASCII) {
        internalWidth = width;
        internalHeight = height;

        scalingFactor = screenHeight / internalHeight * 0.85;
        scalingFactor = (scalingFactor * 2) / 2;

        outputWidth = (int) (internalWidth * scalingFactor);
        outputHeight = (int) (internalHeight * scalingFactor);

        // Initialize the canvas with specified width and height
        image = new BufferedImage(outputWidth + 1, outputHeight, BufferedImage.TYPE_INT_RGB);
        window.setSize(outputWidth, outputHeight);
        window.add(this);
    }

    public double[] maxAmplitudeColour(Ray[][] primaryRay) {
        amplitudesRed.clear();
        amplitudesGreen.clear();
        amplitudesBlue.clear();

        for (int i = 0; i < internalWidth; i++) {
            for (int j = 0; j < internalHeight; j++) {
                if (primaryRay[i][j].getAvgRed() != 0) { // filter out zeros
                    amplitudesRed.add(primaryRay[i][j].getAvgRed());
                }
                if (primaryRay[i][j].getAvgBlue() != 0) { // filter out zeros
                    amplitudesGreen.add(primaryRay[i][j].getAvgGreen());
                }
                if (primaryRay[i][j].getAvgGreen() != 0) { // filter out zeros
                    amplitudesBlue.add(primaryRay[i][j].getAvgBlue());
                }
            }
        }
        amplitudesRed.add(0.0);
        amplitudesGreen.add(0.0);
        amplitudesBlue.add(0.0);

        // return the max of the red, green, or blue light amplitdues
        double maxRed = Collections.max(amplitudesRed) / amplitudesRed.size();
        double maxGreen = Collections.max(amplitudesGreen) / amplitudesGreen.size();
        double maxBlue = Collections.max(amplitudesBlue) / amplitudesBlue.size();
        double maxAbsolute = Math.max(maxBlue, Math.max(maxRed, maxGreen));
        return new double[]{maxRed, maxGreen, maxBlue, maxAbsolute};}

    public void drawFrameRGB(Ray[][] primaryRay, Camera cam) {

        if (Main.ASCIIMode == false) {
            if (Main.reinhardToneMapping == true) {
                double[] maxRGB = maxAmplitudeColour(primaryRay);

                for (int y = 0; y < internalHeight; y++) {
                    for (int x = 0; x < internalWidth; x++) {
                        double red, green, blue;
                        if (Main.denoise == true) {
                            double[] RGB = denoise(x, y, primaryRay, brightnessFactor);
                            red = RGB[0];
                            green = RGB[1];
                            blue = RGB[2];
                        } else {
                            red = (primaryRay[x][y].getAvgRed());
                            green = (primaryRay[x][y].getAvgGreen());
                            blue = (primaryRay[x][y].getAvgBlue());
                        }

                        double[] RGB = {red, green, blue};
                        double mappedLuminance = extendedReinhardToneMapping(RGB, maxRGB);
                        // scale RGB values based on mapped luminance
                        red = red * mappedLuminance;
                        green = green * mappedLuminance;
                        blue = blue * mappedLuminance;

                        // scale to 255 (8-bit)
                        red *= 255 * cam.getISO();
                        green *= 255 * cam.getISO();
                        blue *= 255 * cam.getISO();

                        // clamp values between 0-255 for 8-bit colour space
                        red = Math.min(255, red);
                        green = Math.min(255, green);
                        blue = Math.min(255, blue);

                        int red255 = (int) red;
                        int green255 = (int) green;
                        int blue255 = (int) blue;

                        // first 8 bits are alpha, next 8 red, next 8 green, final 8 blue
                        int rgb = (red255 << 16) | (green255 << 8) | blue255;

                        // draw to screen and upscale
                        for (int i = 0; i < scalingFactor; i++) {
                            for (int k = 0; k < scalingFactor; k++) {
                                image.setRGB((int) (x * scalingFactor + i), (int) (y * scalingFactor + k), rgb);
                            }
                        }
                    }

                }
                repaint(); // update image
            } else if (toneMapping == false) {
                brightnessFactor = 255 * 0.5 / (maxAmplitudeColour(primaryRay)[3] * cam.getISO()); // convert absolute brightness to 8 bit colour space
                for (int y = 0; y < internalHeight; y++) {
                    for (int x = 0; x < internalWidth; x++) {
                        int red, green, blue;
                        if (Main.denoise == true) {
                            double[] RGB = denoise(x, y, primaryRay, brightnessFactor);
                            red = (int) RGB[0];
                            green = (int) RGB[1];
                            blue = (int) RGB[2];
                        } else {
                            red = (int) (primaryRay[x][y].getAvgRed() * brightnessFactor);
                            green = (int) (primaryRay[x][y].getAvgGreen() * brightnessFactor);
                            blue = (int) (primaryRay[x][y].getAvgBlue() * brightnessFactor);
                        }
                        // clamp values between 0-255 for 8-bit colour space
                        red = Math.min(255, red);
                        green = Math.min(255, green);
                        blue = Math.min(255, blue);

                        // first 8 bits are alpha, next 8 red, next 8 green, final 8 blue
                        int rgb = (red << 16) | (green << 8) | blue;

                        for (int i = 0; i < scalingFactor; i++) {
                            for (int k = 0; k < scalingFactor; k++) {
                                image.setRGB((int) (x * scalingFactor + i), (int) (y * scalingFactor + k), rgb);
                            }
                        }
                    }
                }
                repaint(); // update image
            }
        } else if (Main.ASCIIMode == true) {
            // ASCII CODE HERE
            StringBuilder stringBuffer = new StringBuilder();

            double max = maxAmplitudeColour(primaryRay)[3] * cam.getISO();
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

            // internal resolution
            for (int y = 0; y < internalHeight; y++) {
                for (int x = 0; x < internalWidth; x++) {

                    double red = primaryRay[x][y].getAvgRed();
                    double green = primaryRay[x][y].getAvgGreen();
                    double blue = primaryRay[x][y].getAvgBlue();

                    double amplitude = Math.max(Math.max(red, blue), green);

                    if (amplitude >= q12) {
                        stringBuffer.append("@@@");
                    } else if (amplitude >= q11) {
                        stringBuffer.append("DDD");
                    } else if (amplitude >= q10) {
                        stringBuffer.append("000");
                    } else if (amplitude >= q9) {
                        stringBuffer.append("UUU");
                    } else if (amplitude >= q8) {
                        stringBuffer.append("###");
                    } else if (amplitude >= q7) {
                        stringBuffer.append("ZZZ");
                    } else if (amplitude >= q6) {
                        stringBuffer.append("***");
                    } else if (amplitude >= q5) {
                        stringBuffer.append("xxx");
                    } else if (amplitude >= q4) {
                        stringBuffer.append("~~~");
                    } else if (amplitude >= q3) {
                        stringBuffer.append(";;;");
                    } else if (amplitude >= q2) {
                        stringBuffer.append(":::");
                    } else if (amplitude >= q1) {
                        stringBuffer.append(",,,");
                    } else if (amplitude > 0) {
                        stringBuffer.append("...");
                    } else if (amplitude == 0) {
                        stringBuffer.append("   ");
                    }
                }
                stringBuffer.append("\n");
            }
            areaASCII.setText(stringBuffer.toString()); // update ASCII output
        }
    }

    public double extendedReinhardToneMapping(double[] RGB, double[] maxRGB) {
        double red = RGB[0];
        double green = RGB[1];
        double blue = RGB[2];
        double maxRed = maxRGB[0];
        double maxGreen = maxRGB[1];
        double maxBlue = maxRGB[2];

        // convert RGB to pure luminance
        double luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue;
        //luminance = red + green + blue;
        double maxLuminance = 0.2126 * maxRed + 0.7152 * maxGreen + 0.0722 * maxBlue;
        //maxLuminance = maxRed + maxGreen + maxBlue;
        // extended reinhart tone mapping
        double mappedLuminance = (luminance * (1 + (luminance / (maxLuminance * maxLuminance)))) / (1 + luminance);
        return mappedLuminance;
    }

    public double[] denoise(int x, int y, Ray[][] primaryRay, double brightnessFactor) {
        double primaryWeight = Main.denoiseWeight;
        double secondaryWeight = 1 - primaryWeight;
        double[] RGB = new double[3];
        if (x != 0 && y != 0 && x != internalWidth - 1 && y != internalHeight - 1) {
            double primaryRed = primaryRay[x][y].getAvgRed();
            double upRed = primaryRay[x][y + 1].getAvgRed();
            double downRed = primaryRay[x][y - 1].getAvgRed();
            double leftRed = primaryRay[x - 1][y].getAvgRed();
            double rightRed = primaryRay[x + 1][y].getAvgRed();
            double avgRed = (upRed + downRed + leftRed + rightRed) / 4;
            primaryRed = (primaryRed * primaryWeight) + (avgRed * secondaryWeight);
            primaryRed *= brightnessFactor;
            RGB[0] = primaryRed;

            double primaryGreen = primaryRay[x][y].getAvgGreen();
            double upGreen = primaryRay[x][y + 1].getAvgGreen();
            double downGreen = primaryRay[x][y - 1].getAvgGreen();
            double leftGreen = primaryRay[x - 1][y].getAvgGreen();
            double rightGreen = primaryRay[x + 1][y].getAvgGreen();
            double avgGreen = (upGreen + downGreen + leftGreen + rightGreen) / 4;
            primaryGreen = (primaryGreen * primaryWeight) + (avgGreen * secondaryWeight);
            primaryGreen *= brightnessFactor;
            RGB[1] = primaryGreen;

            double primaryBlue = primaryRay[x][y].getAvgBlue();
            double upBlue = primaryRay[x][y + 1].getAvgBlue();
            double downBlue = primaryRay[x][y - 1].getAvgBlue();
            double leftBlue = primaryRay[x - 1][y].getAvgBlue();
            double rightBlue = primaryRay[x + 1][y].getAvgBlue();
            double avgBlue = (upBlue + downBlue + leftBlue + rightBlue) / 4;
            primaryBlue = (primaryBlue * primaryWeight) + (avgBlue * secondaryWeight);
            primaryBlue *= brightnessFactor;
            RGB[2] = primaryBlue;
        } else {
            RGB[0] = primaryRay[x][y].getAvgRed() * brightnessFactor;
            RGB[1] = primaryRay[x][y].getAvgGreen() * brightnessFactor;
            RGB[2] = primaryRay[x][y].getAvgBlue() * brightnessFactor;
        }
        return RGB;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

}