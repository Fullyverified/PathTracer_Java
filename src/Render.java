import com.sun.security.jgss.GSSUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Render {

    public long whilecounter = 0;
    public long hitcounter = 0;
    public long misscounter = 0;
    public int counter = 0;
    public long selfhits = 0;
    public long selfmisses = 0;
    public List<SceneObjects> visibleObjects = new ArrayList<>();

    public static int count = 0;

    public Render() {
    }

    // ... ,,, ::: ;;; !!! ||| \\\ *** %%% $$$ ### @@@
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
                if (primaryRay[i][j].getHit()) {
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

    // prints the brightness value of each pixel
    public void debugDrawScreen(Camera cam, Ray[][] primaryRay, Ray[][] secondRay) {
        // iterate through each rays hit value and print the output
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("------");
        }
        System.out.println(" ");
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit()) {
                    DecimalFormat df = new DecimalFormat("#.00");

                    if (secondRay[i][j].getLightAmplitude() >= 10) {
                        System.out.print(df.format(secondRay[i][j].getLightAmplitude()) + "|");
                    } else if (secondRay[i][j].getLightAmplitude() >= 1.0 && secondRay[i][j].getLightAmplitude() < 10) {
                        System.out.print("0" + df.format(secondRay[i][j].getLightAmplitude()) + "|");
                    } else if (secondRay[i][j].getLightAmplitude() < 1) {
                        System.out.print("00" + df.format(secondRay[i][j].getLightAmplitude()) + "|");
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
            System.out.print("___");
            for (int i = 0; i < cam.getResX(); i++) {
                computePrimaryRay(cam, primaryRay, sceneObjectsList, i, j);
                if (primaryRay[i][j].getHit()) {
                    marchIntersectionLogic(primaryRay, nthRay, sceneObjectsList, i, j, numRays, numBounces);
                }
            }
        }
        System.out.println("_|");
        drawScreen(cam, primaryRay);
        System.out.println("whilecounter: " + whilecounter);
        System.out.println("hitcounter: + " + hitcounter);
        System.out.println("misscounter: + " + misscounter);
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
                        primaryRay[i][j].addLightAmplitude(LambertCosineLaw(primaryRay[i][j], sceneObject1));
                    } else {
                        primaryRay[i][j].addLightAmplitude(0);
                    }
                }
                // hit is already false otherwise
            }
            distance += 0.01;
        }
    }

    public void marchIntersectionLogic(Ray[][] primaryRay, Ray[][] nthRay, List<SceneObjects> sceneObjectsList, int i, int j, int numRays, int numBounces) {
        nthRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());

        // BOUNCES PER RAY
        for (int num = 0; num < numRays; num++) {
            // initialise ray pos
            nthRay[i][j].setOrigin(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
            // give the ray a random direction
            nthRay[i][j].marchRay(0);
            nthRay[i][j].setHitPoint(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());

            primaryRay[i][j].getHitObject().randomDirection(nthRay[i][j]);
            // march the ray a tiny amount to move it off the sphere
            nthRay[i][j].updateOrigin(0.15);


            // add a non culled objects to a list
            visibleObjects.clear();
            for (SceneObjects sceneObject1 : sceneObjectsList) {
                if (sceneObject1.objectCulling(nthRay[i][j])) {
                    visibleObjects.add(sceneObject1);
                }
            }


            nthRay[i][j].setHit(false);
            double distance = 0;
            // march ray and check intersections
            while (distance <= 25 && !nthRay[i][j].getHit()) {
                whilecounter++;
                // march the ray
                nthRay[i][j].marchRay(distance);
                // CHECK INTERSECTIONS for non-culled objects
                for (SceneObjects sceneObject1 : visibleObjects) {
                    // check if the ray intersects with an object
                    if (sceneObject1.intersectionCheck(nthRay[i][j])) {

                        nthRay[i][j].setHit(true);
                        nthRay[i][j].setHitPoint(nthRay[i][j].getPosX(), nthRay[i][j].getPosY(), nthRay[i][j].getPosZ());
                        nthRay[i][j].setHitObject(sceneObject1);

                        hitcounter++;
                        // add brightness
                        //if (sceneObject1.getLuminance() != 0) {
                            primaryRay[i][j].addLightAmplitude(LambertCosineLaw2(nthRay[i][j], sceneObject1, sceneObject1.getLuminance() / numRays));
                        //}
                    }
                    // if hit = 0, march the ray continue the loop
                    else {
                        misscounter++;
                    }
                }
                distance += 0.1;
            }
        }
    }

    public double LambertCosineLaw(Ray currentRay, SceneObjects sceneObject) {
        sceneObject.calculateNormal(currentRay.getPosX(), currentRay.getPosY(), currentRay.getPosZ());
        currentRay.updateNormalisation();

        // dot product of sphere normal and ray direction
        double costheta = sceneObject.getNormalX() * currentRay.getDirX() + sceneObject.getNormalY() * currentRay.getDirY() + sceneObject.getNormalZ() * currentRay.getDirZ();
        double brightness = sceneObject.getLuminance() * costheta;
        brightness = Math.abs(brightness);

        if (costheta < 0) {
            return brightness;
        } else {
            return 0;
        }
        //return brightness;
    }

    public double LambertCosineLaw2(Ray currentRay, SceneObjects sceneObject, double brightness) {
        sceneObject.calculateNormal(currentRay.getPosX(), currentRay.getPosY(), currentRay.getPosZ());
        currentRay.updateNormalisation();

        // dot product of sphere normal and ray direction
        double costheta = sceneObject.getNormalX() * currentRay.getDirX() + sceneObject.getNormalY() * currentRay.getDirY() + sceneObject.getNormalZ() * currentRay.getDirZ();
        brightness = Math.abs(brightness * costheta);

        if (costheta < 0) {
            return brightness;
        } else {
            return 0;
        }
        //return brightness;
    }

    // find normal and calculate lighting
    public void BRDFLighting(Ray currentRay, SceneObjects sceneObject, double distance, int depth, double[][] luminanceArray) {
        sceneObject.calculateNormal(currentRay.getPosX(), currentRay.getPosY(), currentRay.getPosZ());
        double objectDirX = sceneObject.getPosX() - currentRay.getPosX();
        double objectDirY = sceneObject.getPosY() - currentRay.getPosY();
        double objectDirZ = sceneObject.getPosZ() - currentRay.getPosZ();

        double length = Math.sqrt(objectDirX * objectDirX + objectDirY * objectDirY + objectDirZ * objectDirZ);

        objectDirX = objectDirX / length;
        objectDirY = objectDirY / length;
        objectDirZ = objectDirZ / length;

        double dotProduct = sceneObject.getNormalX() * objectDirX + sceneObject.getNormalY() * objectDirY + sceneObject.getNormalZ() * objectDirZ;

        //System.out.println("dotproduct = " + dotProduct);

        if (dotProduct < 0) {
            dotProduct = 0;
        }
        // data structure to store the properties of the event

        luminanceArray[0][depth] = dotProduct;
        luminanceArray[1][depth] = sceneObject.getLuminance();
        luminanceArray[2][depth] = distance;
    }

    public void sumBrightness(Ray[][] primaryRay, Ray[][] nthRay, int numBounces, double distance, double[][] luminanceArray, int i, int j) {
        double dotProduct, currentBrightness, brightness = 0;
        for (int l = numBounces - 1; l >= 0; l--) {
            // 0 = dotproduct, 1 = brightness, 2 = distance
            dotProduct = nthRay[i][j].getLuminanceArray()[0][l];
            luminanceArray[0][l] = dotProduct;
            //System.out.println("dot product1: " + dotProduct);

            //currentBrightness = nthRay[i][j].getLuminanceArray()[1][l];
            currentBrightness = luminanceArray[1][l];

            //distance = nthRay[i][j].getLuminanceArray()[2][l];
            distance = luminanceArray[2][l];

            brightness = (brightness + currentBrightness) * dotProduct;
        }
        //System.out.println("brightess: " + brightness);
        primaryRay[i][j].addLightAmplitude(brightness);
    }

}