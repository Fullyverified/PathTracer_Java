import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Render {

    public long whilecounter = 0;
    public long hitcounter = 0;
    public long misscounter = 0;
    public int counter = 0;
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
                        System.out.print("...");
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

    public void computePixels(List<SceneObjects> sceneObjects, Camera cam, int numRays, int numBounces) {
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
                computePrimaryRay(cam, primaryRay, sceneObjects, i, j);
                if (primaryRay[i][j].getHit()) {
                    marchIntersectionLogic(primaryRay, nthRay, sceneObjects, i, j, numRays, numBounces);
                }
            }
        }
        System.out.println("_|");
        drawScreen(cam, primaryRay);
        System.out.println();
        System.out.println("whilecounter: " + whilecounter);
        System.out.println("hitcounter: + " + hitcounter);
        System.out.println("misscounter: + " + misscounter);
        System.out.println("counter: " + counter);
    }

    public void computePrimaryRay(Camera cam, Ray[][] primaryRay, List<SceneObjects> sceneObjects, int i, int j) {

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
        for (SceneObjects sceneObject1 : sceneObjects) {
            if (sceneObject1.objectCulling(primaryRay[i][j])) {
                visibleObjects.add(sceneObject1);
            }
        }

        double distance = 0;
        while (distance <= 50 && primaryRay[i][j].getHit() == false) {
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
                    // get the ID of the collided sphere
                    primaryRay[i][j].setCollidedObject(sceneObject1.getObjectID());
                    // add light amplitude
                    if (sceneObject1.getLuminance() != 0) {
                        primaryRay[i][j].addLightAmplitude(LambertCosineLaw(primaryRay[i][j], sceneObject1));
                    } else {
                        primaryRay[i][j].addLightAmplitude(0);
                    }

                }
                // hit is already false otherwise
            }
            distance = distance + 0.01;
        }
    }

    public void marchIntersectionLogic(Ray[][] primaryRay, Ray[][] nthRay, List<SceneObjects> sceneObjects, int i, int j, int numRays, int numBounces) {

        // create luminanceArray
        double[][] luminanceArray = new double[3][numBounces + 1];
        // initialise the ray
        nthRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
        // BOUNCES PER RAY
        for (int num = 0; num < numRays; num++) {
            // initialise ray pos
            nthRay[i][j].setOrigin(primaryRay[i][j].getPosX(), primaryRay[i][j].getPosY(), primaryRay[i][j].getPosZ());
            nthRay[i][j].setCollidedObject(primaryRay[i][j].getCollidedObject());
            // give the ray a random direction
            randomDirection(nthRay[i][j], sceneObjects);

            // add a non culled objects to a list
            visibleObjects.clear();
            for (SceneObjects sceneObject1 : sceneObjects) {
                if (sceneObject1.objectCulling(nthRay[i][j])) {
                    visibleObjects.add(sceneObject1);
                }
            }

            nthRay[i][j].setHit(false);
            double distance = 0;
            // march ray and check intersections
            while (distance <= 25 && nthRay[i][j].getHit() == false) {
                whilecounter++;
                // march the ray
                nthRay[i][j].marchRay(distance);
                // CHECK INTERSECTIONS for non-culled objects
                for (SceneObjects sceneObject1 : visibleObjects) {
                    // check if the ray intersects with an object
                    if (sceneObject1.intersectionCheck(nthRay[i][j])) {

                        //System.out.println("Ray[" + i + "][" + j + "][" + sceneObject1.getObjectID() + "]");
                        // delete self intersections
                        if (sceneObject1.getObjectID() == primaryRay[i][j].getCollidedObject()) {
                            nthRay[i][j].setHit(false);
                            // end loop
                            distance = 99999;
                            counter++;
                        } else if (sceneObject1.getObjectID() != primaryRay[i][j].getCollidedObject()) {
                            nthRay[i][j].setHit(true);
                            nthRay[i][j].setHitPoint(nthRay[i][j].getPosX(), nthRay[i][j].getPosY(), nthRay[i][j].getPosZ());
                            // get the ID of the collided pointlight
                            nthRay[i][j].setCollidedObject(sceneObject1.getObjectID());
                            hitcounter++;
                            // add brightness
                            if (sceneObject1.getLuminance() != 0) {
                                primaryRay[i][j].addLightAmplitude(LambertCosineLaw2(nthRay[i][j], sceneObject1, 1.0/numRays));
                                //primaryRay[i][j].addLightAmplitude(1.0 / numRays);

                            }
                        }
                    }
                    // if hit = 0, march the ray continue the loop
                    else {
                        nthRay[i][j].setHit(false);
                        //nthRay[i][j].addLightAmplitude(0);
                        misscounter++;
                    }
                }
                distance = distance + 0.01;
            }
        }
    }

    public double LambertCosineLaw(Ray currentRay, SceneObjects sceneObject) {
        sceneObject.calculateNormal(currentRay.getPosX(), currentRay.getPosY(), currentRay.getPosZ());
        currentRay.updateNormalisation();

        // dot product of sphere normal and ray direction
        double costheta = sceneObject.getNormalX() * currentRay.getNormDirX() + sceneObject.getNormalY() * currentRay.getNormDirY() + sceneObject.getNormalZ() * currentRay.getNormDirZ();
        double brightness = sceneObject.getLuminance() * costheta;
        brightness = Math.abs(brightness);

        return brightness;
    }

    public double LambertCosineLaw2(Ray currentRay, SceneObjects sceneObject, double brightness) {
        sceneObject.calculateNormal(currentRay.getPosX(), currentRay.getPosY(), currentRay.getPosZ());
        currentRay.updateNormalisation();

        // dot product of sphere normal and ray direction
        double costheta = sceneObject.getNormalX() * currentRay.getNormDirX() + sceneObject.getNormalY() * currentRay.getNormDirY() + sceneObject.getNormalZ() * currentRay.getNormDirZ();
        brightness = Math.abs(brightness);

        return brightness;
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

    public void randomDirection(Ray nthRay, List<SceneObjects> sceneObjects) {
        double dotproduct = -1;
        double randomDir;
        Random random = new Random();
        for (SceneObjects sceneObject1 : sceneObjects) {
            // check if object ID is identical to the one we intersected with
            if (sceneObject1.getObjectID() == nthRay.getCollidedObject()) {
                // calculate the normal from the surface of the sphere at the point of intersection
                sceneObject1.calculateNormal(nthRay.getHitPointX(), nthRay.getHitPointY(), nthRay.getHitPointZ());

                while (dotproduct < 0) {
                    randomDir = random.nextDouble(2.0) - 1.0;
                    nthRay.setDirX(randomDir);
                    randomDir = random.nextDouble(2.0) - 1.0;
                    nthRay.setDirY(randomDir);
                    randomDir = random.nextDouble(2.0) - 1.0;
                    nthRay.setDirZ(randomDir);
                    // normalise the random direction
                    nthRay.updateNormalisation();
                    dotproduct = sceneObject1.getNormalX() * nthRay.getDirX() + sceneObject1.getNormalY() * nthRay.getDirY() + sceneObject1.getNormalZ() * nthRay.getDirZ();
                }
            }
        }
        // March the ray slightly to move it off of the object it collided with.
        nthRay.updateOrigin(0.2);
        nthRay.marchRay(0);
    }


}