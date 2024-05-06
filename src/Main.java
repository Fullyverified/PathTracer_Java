import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {

        // create new sceneObjects ArrayList. initialise and store each object in it.
        List<SceneObjects> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(6, 0, 0, 1));
        sceneObjects.add(new Sphere(12, 0, -5, 1.25));
        sceneObjects.add(new PointLight(6, 0.5, 5, 1, 10));

        // create camera object and initialise it
        Camera cam = new Camera(0, 0, 0, 1, 0, 0, 0, 1, 0, 70, 4, 3, 80);
        // each cam. method calculates the various properties of the camera
        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();


        // create a 2D array of rays with the size of resolution of the camera
        Ray[][] primaryRay = new Ray[(int) cam.getResX()][(int) cam.getResY()];
        // 2d array of rays for secondary bounces
        Ray[][] secondRay = new Ray[(int) cam.getResX()][(int) cam.getResY()];

        // iterate through primary each ray, left to right, top to bottom, for each scene object.
        for (int j = 0; j < cam.getResY(); j++) {
            //System.out.println("i: " + i);
            for (int i = 0; i < cam.getResX(); i++) {
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
                    for (Object sceneObject : sceneObjects) {
                        // check if that object is a sphere
                        if (sceneObject instanceof Sphere) {
                            // check the discriminant of the ray for the sphere
                            if (((Sphere) sceneObject).intersectionDiscard(primaryRay[i][j])) {
                                // check if the ray intersects the sphere
                                if (((Sphere) sceneObject).intersectionCheck(primaryRay[i][j])) {
                                    // get the position of the intersection
                                    // set ray hit to 1
                                    primaryRay[i][j].setHitPointX(primaryRay[i][j].getRayPointX());
                                    primaryRay[i][j].setHitPointY(primaryRay[i][j].getRayPointY());
                                    primaryRay[i][j].setHitPointZ(primaryRay[i][j].getRayPointZ());
                                    primaryRay[i][j].setHit(1);
                                    // get the ID of the collided sphere
                                    primaryRay[i][j].setCollidedObject(((Sphere) sceneObject).getObjectID());
                                }
                                // if hit = 0, march the ray continue the loop
                                else {
                                    primaryRay[i][j].setHit(0);
                                }
                            }
                        }
                    }
                    r = r + 0.01;
                }
            }
        }

        // calculate second bounces
        // for each sceneObject
        for (int j = 0; j < cam.getResY(); j++) {
            for (int i = 0; i < cam.getResX(); i++) {
                // initialise second bounce ray
                if (primaryRay[i][j].getHit() == 1) {
                    secondRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
                    // t is number of second rays to be cast per pixel
                    for (int t = 0; t < 20000; t++) {
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
                                            secondRay[i][j].addBrightness(0.1);
                                        } else if ((sceneObject2) instanceof Sphere) {
                                            secondRay[i][j].addBrightness(0);
                                        }
                                    }
                                    // if hit = 0, march the ray continue the loop
                                    else {
                                        secondRay[i][j].setHit(0);
                                        secondRay[i][j].addBrightness(0);
                                    }
                                }
                                r = r + 0.01;
                            }
                        }
                    }
                }
            }
        }

        // iterate through each rays hit value and print the output
        for (int i = 0; i < cam.getResX(); i++) {System.out.print("---");}
        for (int j = 0; j < cam.getResY(); j++) {
            System.out.print("|");
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit() == 1) {
                    if (secondRay[i][j].getBrightness() > 0.5) {
                        System.out.print("###");
                        }
                    else if (secondRay[i][j].getBrightness() <= 0.5) {
                        System.out.print(":::");
                    }

                } else if (primaryRay[i][j].getHit() == 0) {
                    System.out.print("   ");
                }
            }
            System.out.println("|");
        }
        for (int i = 0; i < cam.getResX(); i++) {System.out.print("---");}
    }
}







