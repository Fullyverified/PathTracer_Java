import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawScreen extends JPanel {

    List<Double> amplitudes = new ArrayList<>();
    List<Double> amplitudesRed = new ArrayList<>();
    List<Double> amplitudesGreen = new ArrayList<>();
    List<Double> amplitudesBlue = new ArrayList<>();


    private double screenWidth, screenHeight;
    private double scalingFactor;
    private int outputWidth, outputHeight;
    private int internalWidth, internalHeight;
    private BufferedImage image;
    private JFrame window = new JFrame("Path Tracer");
    private double brightnessFactor = 0;

    private float lineSpacing = 1f;
    private int fontSize = 12;
    private boolean ASCII = true;
    private JTextPane areaASCII;

    private long framesDrawn = 0;

    public DrawScreen(int width, int height, boolean ASCII) {
        this.ASCII = ASCII;
        internalWidth = width;
        internalHeight = height;

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth(); // get screen width and height
        screenHeight = gd.getDisplayMode().getHeight();

        scalingFactor = (screenHeight * 0.90) / internalHeight;
        outputWidth = (int) (internalWidth * scalingFactor);
        outputHeight = (int) (internalHeight * scalingFactor);


        // initialize the canvas with specified width and height

        if (ASCII == false) {
            image = new BufferedImage(outputWidth + 5, outputHeight, BufferedImage.TYPE_INT_RGB);
            window.add(this);
            window.setSize(outputWidth, outputHeight);
            window.setVisible(true);
        } else if (ASCII == true) {
            areaASCII = new JTextPane();
            setLayout(new BorderLayout());
            int fontSize = (int) (8 * scalingFactor);
            areaASCII.setFont(new Font("Monospaced", Font.PLAIN, 9));
            areaASCII.setForeground(Color.WHITE);
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
        this.ASCII = ASCII;
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

    public double maxAmplitudeColour(Ray[][] primaryRay) {
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
        return Math.max(Math.max(Collections.max(amplitudesRed), Collections.max(amplitudesGreen)), Collections.max(amplitudesBlue));
    }

    public void drawFrameRGB(Ray[][] primaryRay, Camera cam) {

        if (ASCII == false) {
            framesDrawn++;
            brightnessFactor = 255 / (maxAmplitudeColour(primaryRay) * cam.getISO()); // convert absolute brightness to 8 bit colour space
            for (int y = 0; y < internalHeight; y++) {
                for (int x = 0; x < internalWidth; x++) {

                    // convert brightness of red green and blue to 8 bit colour space
                    int red = (int) (primaryRay[x][y].getAvgRed() * brightnessFactor);
                    if (red > 255) {
                        red = 255;
                    }
                    int green = (int) (primaryRay[x][y].getAvgGreen() * brightnessFactor);
                    if (green > 255) {
                        green = 255;
                    }
                    int blue = (int) (primaryRay[x][y].getAvgBlue() * brightnessFactor);
                    if (blue > 255) {
                        blue = 255;
                    }

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
        } else if (ASCII == true) {
            /*// ASCII CODE HERE
            StringBuilder stringBuffer = new StringBuilder();

            double max = maxAmplitudeColour(primaryRay);
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
                    if (primaryRay[x][y].getLightAmplitude() >= q12) {
                        stringBuffer.append("@@@");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q11) {
                        stringBuffer.append("DDD");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q10) {
                        stringBuffer.append("000");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q9) {
                        stringBuffer.append("UUU");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q8) {
                        stringBuffer.append("###");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q7) {
                        stringBuffer.append("ZZZ");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q6) {
                        stringBuffer.append("***");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q5) {
                        stringBuffer.append("xxx");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q4) {
                        stringBuffer.append("~~~");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q3) {
                        stringBuffer.append(";;;");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q2) {
                        stringBuffer.append(":::");
                    } else if (primaryRay[x][y].getLightAmplitude() >= q1) {
                        stringBuffer.append(",,,");
                    } else if (primaryRay[x][y].getLightAmplitude() > 0) {
                        stringBuffer.append("...");
                    } else if (primaryRay[x][y].getLightAmplitude() == 0) {
                        stringBuffer.append("   ");
                    }
                }
                stringBuffer.append("\n");
            }
            textArea.setText(stringBuffer.toString()); // update ASCII output
            ((CardLayout) cardPanel.getLayout()).show(cardPanel, "ASCII"); */
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

}