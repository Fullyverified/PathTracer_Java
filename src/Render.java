import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class Render {

    public Render() {
    }

    // ... ,,, ::: ;;; !!! ||| \\\ *** %%% $$$ ### @@@
    public void drawScreen(Camera cam, Ray[][] primaryRay, Ray[][] secondRay) {
        // iterate through each rays hit value and print the output
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("---");
        }
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit() == 1) {
                    if (primaryRay[i][j].getIntensity() >= 20) {
                        System.out.print("###");
                    } else if (primaryRay[i][j].getIntensity() >= 10 && primaryRay[i][j].getIntensity() < 20) {
                        System.out.print("XXX");
                    } else if (primaryRay[i][j].getIntensity() >= 5 && primaryRay[i][j].getIntensity() < 10) {
                        System.out.print("***");
                    } else if (primaryRay[i][j].getIntensity() >= 0.5 && primaryRay[i][j].getIntensity() < 5) {
                        System.out.print(";;;");
                    } else if (primaryRay[i][j].getIntensity() < 0.5 && primaryRay[i][j].getIntensity() > 0) {
                        System.out.print("...");
                    } else {
                        System.out.print("   ");
                    }

                } else if (primaryRay[i][j].getHit() == 0) {
                    System.out.print("   ");
                }
            }
            System.out.println("|");
        }
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("---");
        }
    }

    /*// prints the brightness value of each pixel
    public void debugDrawScreen(Camera cam, Ray[][] primaryRay, Ray[][] secondRay) {
        // iterate through each rays hit value and print the output
        for (int i = 0; i < cam.getResX(); i++) {
            System.out.print("------");
        }
        System.out.println(" ");
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit() == 1) {
                    DecimalFormat df = new DecimalFormat("#.00");

                    if (secondRay[i][j].getLuminance() >= 10) {
                        System.out.print(df.format(secondRay[i][j].getLuminance()) + "|");
                    } else if (secondRay[i][j].getLuminance() >= 1.0 && secondRay[i][j].getLuminance() < 10) {
                        System.out.print("0" + df.format(secondRay[i][j].getLuminance()) + "|");
                    } else if (secondRay[i][j].getLuminance() < 1) {
                        System.out.print("00" + df.format(secondRay[i][j].getLuminance()) + "|");
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
    }*/

    // computeLuminance
    // Ldirect = (intensity * BRDF * max(0, normal * direction)) / distance^2
    public void computeDirectIllumination(Ray[][] ray, int i, int j) {
        double BRDF = 1; // constant for now
        double[][] luminanceArray = ray[i][j].getLuminance();
        double intensity;

        for (int l = 0; l < luminanceArray.length; l++)
        {
            double dotProduct = ray[i][j].getDirX() * luminanceArray[l][2] + ray[i][j].getDirY() * luminanceArray[l][3] + ray[i][j].getDirZ() * luminanceArray[l][4];
            if (dotProduct > 0) {
                ray[i][j].addIntensity((luminanceArray[l][0] * BRDF * dotProduct) / luminanceArray[l][1] * luminanceArray[l][1]);
            }
            else
            {
                ray[i][j].addIntensity(0);
            }
        }

    }

    public void computeFinalItensity(Ray[][] firstRay, Ray[][] secondRay, int resX, int resY) {

        for (int j = 0; j < resY; j++)
        {
            for (int i = 0; i < resX; i++)
            {
                firstRay[i][j].addIntensity(secondRay[i][j].getIntensity());
            }
        }
    }

    // Lindrect = (intensity * BRDF * max(0, normal * randomDirection)) / probability density function
    public void computeIndirectIllumination() {


    }

    public void computePrimaryRays(Camera cam, Ray[][] primaryRay, List<SceneObjects> sceneObjects, int i, int j) {
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

        // while the ray is not intersecting the sphere and the ray has not marched 100 units
        // create local variable r (the rays step)
        double r = 0;
        while (r <= 100 && primaryRay[i][j].getHit() == 0) {
            // march the ray
            primaryRay[i][j].rayMarch(r);

            // for each object that is in the sceneObjects collection
            for (SceneObjects sceneObject1 : sceneObjects) {
                // check the discriminant of the ray for the sphere
                if (sceneObject1.intersectionDiscard(primaryRay[i][j])) {
                    // check if the ray intersects the sphere
                    if (sceneObject1.intersectionCheck(primaryRay[i][j])) {
                        // get the position of the intersection
                        primaryRay[i][j].setHitPointX(primaryRay[i][j].getRayPointX());
                        primaryRay[i][j].setHitPointY(primaryRay[i][j].getRayPointY());
                        primaryRay[i][j].setHitPointZ(primaryRay[i][j].getRayPointZ());
                        // set ray hit to 1
                        primaryRay[i][j].setHit(1);
                        // get the ID of the collided sphere
                        primaryRay[i][j].setCollidedObject(sceneObject1.getObjectID());
                        // add the light intensity value to the corresponding rays array
                        primaryRay[i][i].setLuminance(sceneObject1.getLuminance(), r, sceneObject1.getNormalX(), sceneObject1.getNormalZ(), sceneObject1.getNormalY());
                    }
                    // if hit = 0, march the ray continue the loop
                    else {
                        primaryRay[i][j].setHit(0);
                    }
                }
            }
            r = r + 0.01;
        }
    }

    // optimization technique
    // shadow ray creates the second ray object
    // must be run in order to use computeNextBounce
    public void computeShadowRay(Ray[][] primaryRay, Ray[][] secondRay, List<SceneObjects> sceneObjects, int i, int j) {

        // calculate second bounces
        // initialise second bounce ray
        if (primaryRay[i][j].getHit() == 1) {
            secondRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());

            for (SceneObjects sceneObject : sceneObjects) {

                if (sceneObject instanceof PointLight) {
                    // calculate the vector from the ray position to the spherical light
                    // ray to light = point light - ray pos
                    secondRay[i][j].setDirX(sceneObject.getPosX() - secondRay[i][j].getPosX());
                    secondRay[i][j].setDirY(sceneObject.getPosY() - secondRay[i][j].getPosY());
                    secondRay[i][j].setDirZ(sceneObject.getPosZ() - secondRay[i][j].getPosZ());

                    secondRay[i][j].updateNormalisation();

                    double r = 0;
                    secondRay[i][j].setHit(0);
                    while (r <= 25 && secondRay[i][j].getHit() == 0) {
                        // march the ray
                        secondRay[i][j].rayMarch(r);
                        for (SceneObjects sceneObject2 : sceneObjects) {
                            // check the discriminant of the ray for the sphere
                            if (sceneObject2.intersectionDiscard(secondRay[i][j])) {
                                // check if the ray intersects with an object
                                if (sceneObject2.intersectionCheck(secondRay[i][j])) {
                                    // get the position of the intersection
                                    // set ray hit to 1
                                    secondRay[i][j].setHitPointX(secondRay[i][j].getRayPointX());
                                    secondRay[i][j].setHitPointY(secondRay[i][j].getRayPointY());
                                    secondRay[i][j].setHitPointZ(secondRay[i][j].getRayPointZ());
                                    secondRay[i][j].setHit(1);
                                    // get the ID of the collided pointlight
                                    secondRay[i][j].setCollidedObject(sceneObject2.getObjectID());
                                    if ((sceneObject2) instanceof PointLight) {
                                        // store the luminance and distance to the object in an array belonging to the ray
                                        sceneObject2.surfaceToNormal(secondRay[i][j].getHitPointX(), secondRay[i][j].getHitPointY(), secondRay[i][j].getHitPointZ());
                                        secondRay[i][j].setLuminance(sceneObject2.getLuminance(), r, sceneObject2.getNormalX(), sceneObject2.getNormalZ(), sceneObject2.getNormalY());

                                    } else if ((sceneObject2) instanceof Sphere) {
                                        secondRay[i][j].setLuminance(sceneObject2.getLuminance(), r, sceneObject2.getNormalX(), sceneObject2.getNormalZ(), sceneObject2.getNormalY());
                                    }
                                }
                                // if hit = 0, march the ray continue the loop
                                else {
                                    secondRay[i][j].setHit(0);
                                }
                            }
                            r = r + 0.01;
                        }
                    }

                }

            }

        }

    }

    public void computeNextBounce(int numRaysPerPixel, Ray[][] primaryRay, Ray[][] secondRay, List<SceneObjects> sceneObjects, int i, int j) {

        // calculate second bounces
        // initialise second bounce ray
        if (primaryRay[i][j].getHit() == 1) {
            //secondRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
            // t is number of second rays to be cast per pixel
            for (int t = 0; t < numRaysPerPixel; t++) {
                // give the second ray a random normalised direction
                Random random = new Random();
                double randomDir = random.nextDouble(2.0) - 1.0;
                secondRay[i][j].setDirX(randomDir);
                randomDir = random.nextDouble(2.0) - 1.0;
                secondRay[i][j].setDirY(randomDir);
                randomDir = random.nextDouble(2.0) - 1.0;
                secondRay[i][j].setDirZ(randomDir);

                // normalise the random direction
                secondRay[i][j].updateNormalisation();

                for (SceneObjects sceneObject1 : sceneObjects) {
                    // check if object ID is identical to the one we intersected with
                    if (sceneObject1.getObjectID() == primaryRay[i][j].getCollidedObject()) {
                        // calculate the normal from the surface of the sphere at the point of intersection
                        ((Sphere) sceneObject1).surfaceToNormal(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());

                        // dot product of normal and randomly generated direction
                        double dotproduct = (((Sphere) sceneObject1).getNormalX() * secondRay[i][j].getDirX() + ((Sphere) sceneObject1).getNormalY() * secondRay[i][j].getDirY() + ((Sphere) sceneObject1).getNormalZ() * secondRay[i][j].getDirZ());

                        while (dotproduct < 0) {
                            randomDir = random.nextDouble(2.0) - 1.0;
                            secondRay[i][j].setDirX(randomDir);
                            randomDir = random.nextDouble(2.0) - 1.0;
                            secondRay[i][j].setDirY(randomDir);
                            randomDir = random.nextDouble(2.0) - 1.0;
                            secondRay[i][j].setDirZ(randomDir);
                            // normalise the random direction
                            secondRay[i][j].updateNormalisation();
                            dotproduct = (((Sphere) sceneObject1).getNormalX() * secondRay[i][j].getDirX() + ((Sphere) sceneObject1).getNormalY() * secondRay[i][j].getDirY() + ((Sphere) sceneObject1).getNormalZ() * secondRay[i][j].getDirZ());
                        }
                    }
                }
                // create local variable r (the rays step)
                double r = 0;
                secondRay[i][j].setHit(0);
                while (r <= 25 && secondRay[i][j].getHit() == 0) {
                    // march the ray
                    secondRay[i][j].rayMarch(r);
                    for (SceneObjects sceneObject2 : sceneObjects) {
                        // check the discriminant of the ray for the sphere
                        if (sceneObject2.intersectionDiscard(secondRay[i][j])) {
                            // check if the ray intersects with an object
                            if (sceneObject2.intersectionCheck(secondRay[i][j])) {
                                // get the position of the intersection
                                // set ray hit to 1
                                secondRay[i][j].setHitPointX(secondRay[i][j].getRayPointX());
                                secondRay[i][j].setHitPointY(secondRay[i][j].getRayPointY());
                                secondRay[i][j].setHitPointZ(secondRay[i][j].getRayPointZ());
                                secondRay[i][j].setHit(1);
                                // get the ID of the collided pointlight
                                secondRay[i][j].setCollidedObject(sceneObject2.getObjectID());
                                if ((sceneObject2) instanceof PointLight) {
                                    secondRay[i][j].setLuminance(sceneObject2.getLuminance(), r, sceneObject2.getNormalX(), sceneObject2.getNormalZ(), sceneObject2.getNormalY());
                                } else if ((sceneObject2) instanceof Sphere) {
                                    secondRay[i][j].setLuminance(sceneObject2.getLuminance(), r, sceneObject2.getNormalX(), sceneObject2.getNormalZ(), sceneObject2.getNormalY());
                                }
                            }
                            // if hit = 0, march the ray continue the loop
                            else {
                                secondRay[i][j].setHit(0);
                                secondRay[i][j].setLuminance(sceneObject2.getLuminance(), r, sceneObject2.getNormalX(), sceneObject2.getNormalZ(), sceneObject2.getNormalY());
                            }
                        }
                        r = r + 0.01;
                    }
                }
            }
        }
    }

}
