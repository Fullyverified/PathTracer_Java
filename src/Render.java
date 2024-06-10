import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Render {

    public long whilecounter = 0;
    public long hitcounter = 0;
    public long misscounter = 0;
    public int counter = 0;
    List<SceneObjects> visibleObjects = new ArrayList<>();

    public Render() {
    }

    // ... ,,, ::: ;;; !!! ||| \\\ *** %%% $$$ ### @@@
    public void drawScreen(Camera cam, Ray[][] primaryRay) {
        // iterate through each rays hit value and print the output
        System.out.print("|");
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("---");
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
        for (int l = 0; l < cam.getResX(); l++) {
            System.out.print("---");
        }
        System.out.println("-|");

        for (int j = 0; j < cam.getResX(); j++) {
            System.out.print("___");
            for (int i = 0; i < cam.getResX(); i++) {
                computePrimaryRay(cam, primaryRay, sceneObjects, i, j);
                if (primaryRay[i][j].getHit()) {
                    marchIntersectionLogic(primaryRay, nthRay, sceneObjects, i, j, numRays, numBounces);
                }
            }
        }
        System.out.println();
        drawScreen(cam, primaryRay);

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
                //if (sceneObject1.objectCulling(primaryRay[i][j])) {
                    // check if the ray intersects the object
                    if (sceneObject1.intersectionCheck(primaryRay[i][j])) {
                        // get the position of the intersection
                        primaryRay[i][j].setHitPoint(primaryRay[i][j].getPosX(), primaryRay[i][j].getPosY(), primaryRay[i][j].getPosZ());
                        // set ray hit to 1
                        primaryRay[i][j].setHit(true);
                        // get the ID of the collided sphere
                        primaryRay[i][j].setCollidedObject(sceneObject1.getObjectID());
                        primaryRay[i][j].addLightAmplitude(sceneObject1.getLuminance());
                    }
                    // hit is already false otherwise
                //}
            }
            distance = distance + 0.01;
        }
    }

    public void marchIntersectionLogic(Ray[][] primaryRay, Ray[][] nthRay, List<SceneObjects> sceneObjects, int i, int j, int numRays, int numBounces) {

        // create luminanceArray
        double[][] luminanceArray = new double[3][numBounces + 1];

        // initialise the ray
        nthRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
        nthRay[i][j].setCollidedObject(primaryRay[i][j].getCollidedObject());
        nthRay[i][j].setOrigin(primaryRay[i][j].getPosX(), primaryRay[i][j].getPosY(), primaryRay[i][j].getPosZ());

        // BOUNCES PER RAY
        for (int num = 0; num < numRays; num++) {
            nthRay[i][j].setOrigin(primaryRay[i][j].getPosX(), primaryRay[i][j].getPosY(), primaryRay[i][j].getPosZ());

            // give the ray a random direction
            randomDirection(nthRay[i][j], sceneObjects);

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
                // march the ray
                nthRay[i][j].marchRay(distance);
                // CHECK INTERSECTIONS
                for (SceneObjects sceneObject2 : sceneObjects) {
                    // check the discriminant of the ray for the sphere
                    if (sceneObject2.objectCulling(nthRay[i][j])) {
                        // check if the ray intersects with an object
                        if (sceneObject2.intersectionCheck(nthRay[i][j])) {
                            nthRay[i][j].setHit(true);
                            nthRay[i][j].setHitPoint(nthRay[i][j].getPosX(), nthRay[i][j].getPosY(), nthRay[i][j].getPosZ());

                            // find the dot product of the object normal and the angle of incident

                            // calculate lighting
                            //BRDFLighting(nthRay[i][j], sceneObject2, distance, num, luminanceArray);
                            // get the ID of the collided pointlight
                            nthRay[i][j].setCollidedObject(sceneObject2.getObjectID());

                            if ((sceneObject2) instanceof SphereLight) {
                                primaryRay[i][j].addLightAmplitude(0.1);
                            } else if ((sceneObject2) instanceof Sphere) {
                                primaryRay[i][j].addLightAmplitude(0);
                            }
                        }
                        // if hit = 0, march the ray continue the loop
                        else {
                            nthRay[i][j].setHit(false);
                            nthRay[i][j].addLightAmplitude(0);
                        }
                    }
                }
                distance = distance + 0.01;
            }
        }
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
                    dotproduct = (sceneObject1.getNormalX() * nthRay.getDirX()) + (sceneObject1.getNormalY() * nthRay.getDirY()) + (sceneObject1.getNormalZ() * nthRay.getDirZ());
                }
            }
        }
    }


}