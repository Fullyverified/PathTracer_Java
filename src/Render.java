import java.util.Random;

public class Render {



    public void secondBounce(Camera cam, Ray[][] primaryRay, Ray[][] secondRay) {

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
    }

}
