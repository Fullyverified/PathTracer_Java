import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class Render implements Runnable {

    public long whilecounter = 0;
    public long hitcounter = 0;
    public long misscounter = 0;
    public int counter = 0;
    public long selfhits = 0;
    public long selfmisses = 0;
    public static int count = 0;
    int loading = 0;

    public List<SceneObjects> visibleObjects = new ArrayList<>();
    List<Double> amplitudes = new ArrayList<>();

    public Render() {
    }



    public void brightnessDistribution(Camera cam, Ray[][] primaryRay) {
        for (int i = 0; i < cam.getResX(); i++) {
            for (int j = 0; j < cam.getResY(); j++) {
                if (primaryRay[i][j].getLightAmplitude() != 0) { // filter out zeros
                    amplitudes.add(primaryRay[i][j].getLightAmplitude());
                }
            }
        }
        Collections.sort(amplitudes);
    }

    double getQuantile(List<Double> data, double quantile) {
        int index = (int) Math.ceil(quantile * data.size()) - 1;
        return data.get(Math.max(index, 0));
    }

    // ... ,,, ::: ;;; XXX *** 000 DDD ### @@@
    public void drawScreenQuantiles(Camera cam, Ray[][] primaryRay) {
        double q1 = getQuantile(amplitudes, 0.05);
        System.out.println("q1: " + q1);
        double q2 = getQuantile(amplitudes, 0.10);
        System.out.println("q2: " + q2);
        double q3 = getQuantile(amplitudes, 0.25);
        System.out.println("q3: " + q3);
        double q4 = getQuantile(amplitudes, 0.35);
        System.out.println("q4: " + q4);
        double q5 = getQuantile(amplitudes, 0.45);
        System.out.println("q5: " + q5);
        double q6 = getQuantile(amplitudes, 0.60);
        System.out.println("q6: " + q6);
        double q7 = getQuantile(amplitudes, 0.70);
        System.out.println("q7: " + q7);
        double q8 = getQuantile(amplitudes, 0.85);
        System.out.println("q8: " + q8);
        double q9 = getQuantile(amplitudes, 0.95);
        System.out.println("q9: " + q9);

        // iterate through each rays hit value and print the output
        System.out.print("|");
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("-|-");
        }
        System.out.println("|");
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getLightAmplitude() >= q9) {
                    System.out.print("@@@");
                } else if (primaryRay[i][j].getLightAmplitude() >= q8) {
                    System.out.print("###");
                } else if (primaryRay[i][j].getLightAmplitude() >= q7) {
                    System.out.print("DDD");
                } else if (primaryRay[i][j].getLightAmplitude() >= q6) {
                    System.out.print("000");
                } else if (primaryRay[i][j].getLightAmplitude() >= q5) {
                    System.out.print("***");
                } else if (primaryRay[i][j].getLightAmplitude() >= q4) {
                    System.out.print("xxx");
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

    public void run() {

    }

    public void computePixels(List<SceneObjects> sceneObjectsList, Camera cam, int numRays, int numBounces) {
        Ray[][] primaryRay = new Ray[(int) cam.getResX()][(int) cam.getResY()];
        Ray[][] nthRay = new Ray[(int) cam.getResX()][(int) cam.getResY()];

        System.out.print("|-");
        for (int l = 0; l < cam.getResY(); l++) {
            System.out.print("---");
        }
        System.out.println("-|");
        System.out.print("|_");
        for (int j = 0; j < cam.getResY(); j++) {
            for (int i = 0; i < cam.getResX(); i++) {
                computePrimaryRay(cam, primaryRay, sceneObjectsList, i, j);
            }
        }
        marchIntersectionLogic(primaryRay, nthRay, sceneObjectsList, numRays, numBounces, cam);

        System.out.println("_|");
        brightnessDistribution(cam, primaryRay);
        drawScreenQuantiles(cam, primaryRay);
    }

    public void computePrimaryRay(Camera cam, Ray[][] primaryRay, List<SceneObjects> sceneObjectsList, int i, int j) {
        primaryRay[i][j] = new Ray(cam.getPosX(), cam.getPosY(), cam.getPosZ());
        // update the rays index to the current pixel
        primaryRay[i][j].setPixelX(i);
        primaryRay[i][j].setPixelY(j);

        // calculate pixel position on the plane
        primaryRay[i][j].setPixelIndexX((((i + 0.5) / cam.getResX()) * 2) - 1);
        primaryRay[i][j].setPixelIndexY(1 - (((j + 0.5) / cam.getResY()) * 2));

        // calculate pixel position in the scene
        primaryRay[i][j].setPixelPosX(primaryRay[i][j].getPixelIndexX() * cam.getCamWidth() / 2);
        primaryRay[i][j].setPixelPosY(primaryRay[i][j].getPixelIndexY() * cam.getCamHeight() / 2);

        // set the primary ray direction
        // D = normCamD + rightvector * ScenePosX + upvector * ScenePosY
        primaryRay[i][j].setDirX(cam.getNormDirX() + cam.getNormRightX() * primaryRay[i][j].getPixelPosX() + cam.getNormUpX() * primaryRay[i][j].getPixelPosY());
        primaryRay[i][j].setDirY(cam.getNormDirY() + cam.getNormRightY() * primaryRay[i][j].getPixelPosX() + cam.getNormUpY() * primaryRay[i][j].getPixelPosY());
        primaryRay[i][j].setDirZ(cam.getNormDirZ() + cam.getNormRightZ() * primaryRay[i][j].getPixelPosX() + cam.getNormUpZ() * primaryRay[i][j].getPixelPosY());
        // update vector normalisation
        primaryRay[i][j].updateNormalisation();

        // while the ray is not intersecting an object and the ray has not marched 100 units
        // create local variable r (the rays step)

        primaryRay[i][j].marchRay(0);
        visibleObjects.clear();
        for (SceneObjects sceneObject1 : sceneObjectsList) {
            if (sceneObject1.objectCulling(primaryRay[i][j])) {
                visibleObjects.add(sceneObject1);
            }
        }
        double distance = 0;
        if (!visibleObjects.isEmpty()) {
            while (distance <= 50 && !primaryRay[i][j].getHit()) {
                // march the ray
                primaryRay[i][j].marchRay(distance);
                // for each object that is hasn't been culled
                for (SceneObjects sceneObject1 : visibleObjects) {
                    // check if the ray intersects the object
                    if (sceneObject1.intersectionCheck(primaryRay[i][j])) {
                        // get the position of the intersection
                        primaryRay[i][j].setHitPoint(primaryRay[i][j].getPosX(), primaryRay[i][j].getPosY(), primaryRay[i][j].getPosZ());
                        // set ray hit to 1
                        primaryRay[i][j].setHit(true);
                        primaryRay[i][j].setHitObject(sceneObject1);
                        // add light amplitude
                        if (sceneObject1.getLuminance() != 0) {
                            primaryRay[i][j].addLightAmplitude(lambertCosineLaw(primaryRay[i][j], sceneObject1, sceneObject1.getLuminance()));
                        }
                    }
                    // hit is already false otherwise
                }
                distance += 0.01;
            }
        }
    }

    public void marchIntersectionLogic(Ray[][] primaryRay, Ray[][] nthRay, List<SceneObjects> sceneObjectsList, int numRays, int numBounces, Camera cam) {
        for (int currentRay = 1; currentRay < numRays; currentRay++) { // sample one ray for each pixel, then move onto the next ray
            for (int j = 0; j < cam.getResY(); j++) {
                for (int i = 0; i < cam.getResX(); i++) {
                    if (primaryRay[i][j].getHit()) {
                        nthRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
                        double[][] luminanceArray = new double[numBounces][4];
                        // BOUNCES PER RAY
                        // initialize ray starting conditions
                        nthRay[i][j].initializeRay(primaryRay[i][j]);
                        for (int currentBounce = 0; currentBounce < numBounces && nthRay[i][j].getHit(); currentBounce++) {
                            if (currentBounce == 0) {
                                // first bounce uses random direction
                                nthRay[i][j].getHitObject().randomDirection(nthRay[i][j]);
                            } else {
                                // second uses a reflection vector
                                nthRay[i][j].getHitObject().reflectionBounce(nthRay[i][j]);
                            }
                            // add all non culled objects to a list
                            visibleObjects.clear();
                            for (SceneObjects sceneObject1 : sceneObjectsList) {
                                if (sceneObject1.objectCulling(nthRay[i][j])) {
                                    visibleObjects.add(sceneObject1);
                                }
                            }
                            nthRay[i][j].setHit(false);
                            double distance = 0;
                            // march ray and check intersections
                            while (distance <= 25 && !nthRay[i][j].getHit() && !visibleObjects.isEmpty()) { // redundant to check !visibleObjects.isEmpty() every time but the code is cleaner
                                // march the ray
                                nthRay[i][j].marchRay(distance);
                                // CHECK INTERSECTIONS for non-culled objects
                                for (SceneObjects sceneObject1 : visibleObjects) {
                                    if (sceneObject1.intersectionCheck(nthRay[i][j])) {
                                        primaryRay[i][j].addNumHits(); // debug
                                        nthRay[i][j].updateHitProperties(sceneObject1);
                                        primaryRay[i][j].addLightAmplitude(lambertCosineLaw(nthRay[i][j], sceneObject1, (sceneObject1.getLuminance() / numRays) / (currentBounce + 1)));
                                        // data structure for storing object luminance, dot product and bounce depth, and boolean hit
                                        luminanceArray[currentBounce][0] = sceneObject1.getLuminance();
                                        luminanceArray[currentBounce][1] = lambertCosineLawTEST(nthRay[i][j], sceneObject1);
                                        luminanceArray[currentBounce][2] = currentBounce + 1;
                                        luminanceArray[currentBounce][3] = 1;
                                    }
                                }
                                distance += 0.1;
                            }
                        }
                        /*double brightness = 0;
                        if (!visibleObjects.isEmpty()) {
                            // sum up values of lightness for each bounce into the scene
                            // ((object brightness * lambertCosineLaw) / nthBounce) / numHits

                            if (primaryRay[i][j].getHit()) {
                                for (int index = luminanceArray.length - 1; index >= 0; index--) {
                                    if (luminanceArray[index][3] == 1) {
                                        brightness = +((Math.abs(luminanceArray[index][0]) + Math.abs(brightness)) * Math.abs(luminanceArray[index][1])) / Math.abs(luminanceArray[index][2]);
                                    }
                                }
                            }
                            primaryRay[i][j].addLightAmplitude(brightness / numRays);
                        }*/
                    }
                }
            }
        }
    }

    public double lambertCosineLaw(Ray currentRay, SceneObjects sceneObject, double brightness) {
        sceneObject.calculateNormal(currentRay);
        currentRay.updateNormalisation();

        // dot product of sphere normal and ray direction
        double costheta = sceneObject.getNormalX() * currentRay.getDirX() + sceneObject.getNormalY() * currentRay.getDirY() + sceneObject.getNormalZ() * currentRay.getDirZ();
        brightness = Math.abs(brightness * costheta);

        if (costheta < 0) {
            return brightness;
        } else {
            return 0;
        }
    }

    public double lambertCosineLawTEST(Ray currentRay, SceneObjects sceneObject) {
        sceneObject.calculateNormal(currentRay);
        currentRay.updateNormalisation();

        // dot product of sphere normal and ray direction
        double costheta = Math.abs(sceneObject.getNormalX() * currentRay.getDirX() + sceneObject.getNormalY() * currentRay.getDirY() + sceneObject.getNormalZ() * currentRay.getDirZ());
        return costheta;
    }

    // prints the brightness value of each pixel
    public void drawScreen(Camera cam, Ray[][] primaryRay) {
        // iterate through each rays hit value and print the output
        System.out.print("|");
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("-|-");
        }
        System.out.println("|");
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getLightAmplitude() >= 20) {
                    System.out.print("###");
                } else if (primaryRay[i][j].getLightAmplitude() >= 15 && primaryRay[i][j].getLightAmplitude() < 20) {
                    System.out.print("XXX");
                } else if (primaryRay[i][j].getLightAmplitude() >= 6 && primaryRay[i][j].getLightAmplitude() < 15) {
                    System.out.print("***");
                } else if (primaryRay[i][j].getLightAmplitude() >= 0.4 && primaryRay[i][j].getLightAmplitude() < 6) {
                    System.out.print(";;;");
                } else if (primaryRay[i][j].getLightAmplitude() > 0 && primaryRay[i][j].getLightAmplitude() < 0.4) {
                    System.out.print("...");
                } else if (primaryRay[i][j].getLightAmplitude() == 0) {
                    System.out.print("   ");
                } else {
                    System.out.print(primaryRay[i][j].getLightAmplitude());
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

    public void debugDrawScreenBrightness(Camera cam, Ray[][] primaryRay) {
        DecimalFormat df = new DecimalFormat("#.00");
        // iterate through each rays hit value and print the output
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("------");
        }
        System.out.println(" ");
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit()) {
                    if (primaryRay[i][j].getLightAmplitude() >= 10) {
                        System.out.print(df.format(primaryRay[i][j].getLightAmplitude()) + "|");
                    } else if (primaryRay[i][j].getLightAmplitude() >= 1.0 && primaryRay[i][j].getLightAmplitude() < 10) {
                        System.out.print("0" + df.format(primaryRay[i][j].getLightAmplitude()) + "|");
                    } else if (primaryRay[i][j].getLightAmplitude() < 1) {
                        System.out.print("00" + df.format(primaryRay[i][j].getLightAmplitude()) + "|");
                    }
                } else {
                    System.out.print("00.00|");
                }
            }
            System.out.println(" ");
        }
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("------");
        }
    }

    public void drawScreenLogarithm(Camera cam, Ray[][] primaryRay) {
        // iterate through each rays hit value and print the output
        System.out.print("|");
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("-|-");
        }
        System.out.println("|");
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit()) {
                    if (primaryRay[i][j].getLightAmplitude() >= 1000) {
                        System.out.print("###");
                    } else if (primaryRay[i][j].getLightAmplitude() >= 100 && primaryRay[i][j].getLightAmplitude() < 1000) {
                        System.out.print("XXX");
                    } else if (primaryRay[i][j].getLightAmplitude() >= 10 && primaryRay[i][j].getLightAmplitude() < 100) {
                        System.out.print("***");
                    } else if (primaryRay[i][j].getLightAmplitude() >= 1 && primaryRay[i][j].getLightAmplitude() < 10) {
                        System.out.print(";;;");
                    } else if (primaryRay[i][j].getLightAmplitude() > 0 && primaryRay[i][j].getLightAmplitude() < 1) {
                        System.out.print("...");
                    } else if (primaryRay[i][j].getLightAmplitude() == 0) {
                        System.out.print("   ");
                    } else {
                        System.out.print(primaryRay[i][j].getLightAmplitude());
                    }
                } else if (!primaryRay[i][j].getHit()) {
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

    public void debugDrawScreenNumHits(Camera cam, Ray[][] primaryRay) {
        DecimalFormat df = new DecimalFormat("#.00");
        // iterate through each rays hit value and print the output
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("------");
        }
        System.out.println(" ");
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit()) {
                    if (primaryRay[i][j].getNumHits() >= 10) {
                        System.out.print(df.format(primaryRay[i][j].getNumHits()) + "|");
                    } else if (primaryRay[i][j].getNumHits() >= 1.0 && primaryRay[i][j].getNumHits() < 10) {
                        System.out.print("0" + df.format(primaryRay[i][j].getNumHits()) + "|");
                    } else if (primaryRay[i][j].getNumHits() < 1) {
                        System.out.print("00" + df.format(primaryRay[i][j].getNumHits()) + "|");
                    }
                } else {
                    System.out.print("00.00|");
                }
            }
            System.out.println(" ");
        }
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("------");
        }
    }
}
