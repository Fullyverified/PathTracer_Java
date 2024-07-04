import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

public class DrawScreen extends JPanel {

    List<Double> amplitudes = new ArrayList<>();

    int scalingFactor = 5;

    private BufferedImage image;

    public DrawScreen(int width, int height) {
        // Initialize the canvas with specified width and height
        image = new BufferedImage(width*scalingFactor, height*scalingFactor, BufferedImage.TYPE_INT_RGB);

        JFrame window = new JFrame("Path Tracer");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(this);
        window.setSize(width*scalingFactor, height*scalingFactor);
        window.setVisible(true);
    }

    public double maxBrightness(Camera cam, Ray[][] primaryRay) {
        amplitudes.clear();
        for (int i = 0; i < cam.getResX(); i++) {
            for (int j = 0; j < cam.getResY(); j++) {
                if (primaryRay[i][j].getLightAmplitude() != 0) { // filter out zeros
                    amplitudes.add(primaryRay[i][j].getLightAmplitude());
                }
            }
        }
        amplitudes.add(0.0);
        return Collections.max(amplitudes);
    }

    public void updateImage(int width, int height, Ray[][] primaryRay, Camera cam){
        double factor = 255 / maxBrightness(cam, primaryRay);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double brightness = primaryRay[x][y].getLightAmplitude() * factor;
                int red = (int) brightness;
                int green = (int) brightness;
                int blue = (int) brightness;

                int rgb = (red << 16) | (green << 8) | blue;

                for (int i = 0; i < scalingFactor; i++) {
                    for (int k = 0; k < scalingFactor; k++) {
                        image.setRGB(x*scalingFactor + i, y*scalingFactor + k, rgb);
                    }
                }
            }
        }
        repaint(); // update image
    }

    public void randomColours(int width, int height) {
        // Paint each pixel with a different color
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Example color pattern: create a gradient
                int red = (x * 255) / width;
                int green = (y * 255) / height;
                int blue = ((x + y) * 255) / (width + height);

                int color = (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, color);
            }
        }
    }
    public void setPixelColor(int x, int y, Color color) {
        image.setRGB(x, y, color.getRGB());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

}