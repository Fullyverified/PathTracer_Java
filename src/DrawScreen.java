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

    private float lineSpacing = 1f;
    private int fontSize = 12;
    private boolean ASCII = true;
    private JTextPane areaASCII;

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
                if (primaryRay[i][j].getRed() != 0) { // filter out zeros
                    amplitudesRed.add(primaryRay[i][j].getRed());
                }
                if (primaryRay[i][j].getGreen() != 0) { // filter out zeros
                    amplitudesGreen.add(primaryRay[i][j].getGreen());
                }
                if (primaryRay[i][j].getBlue() != 0) { // filter out zeros
                    amplitudesBlue.add(primaryRay[i][j].getBlue());
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
        double factor = 255 / (maxAmplitudeColour(primaryRay) * cam.getISO()); // convert absolute brightness to 8 bit colour space
        for (int y = 0; y < internalHeight; y++) {
            for (int x = 0; x < internalWidth; x++) {

                // convert brightness of red green and blue to 8 bit colour space
                int red =  (int) (primaryRay[x][y].getRed() * factor);
                int green = (int) (primaryRay[x][y].getGreen() * factor);
                int blue = (int) (primaryRay[x][y].getBlue() * factor);

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

}