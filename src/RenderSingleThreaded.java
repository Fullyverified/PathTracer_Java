import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderSingleThreaded {

    public long startTime, endTime, elapsedTime;
    public List<SceneObjects> visibleObjects = new ArrayList<>();
    private int loadingProgress, currentProgress = 0;
    private String loadingString = "";
    public static double primaryRayStep;
    public static double secondaryRayStep;

    public RenderSingleThreaded() {
    }

    public void computePixels(List<SceneObjects> sceneObjectsList, Camera cam, int numRays, int numBounces, long frameTime, boolean ASCII, double primaryRayStep, double secondaryRayStep) {
        RenderSingleThreaded.primaryRayStep = primaryRayStep;
        RenderSingleThreaded.secondaryRayStep = secondaryRayStep;

        Ray[][] primaryRay = new Ray[cam.getResX()][cam.getResY()];
        Ray[][] nthRay = new Ray[cam.getResX()][cam.getResY()];

        ScheduledExecutorService drawScreenExecutor = Executors.newScheduledThreadPool(1);
        AtomicBoolean updateScreen = new AtomicBoolean(false);
        //Define the screen update task
        Runnable screenUpdateTask = () -> {
            updateScreen.set(true);
        };
        DrawScreen drawScreen = new DrawScreen(cam.getResX(), cam.getResY(), ASCII);

        startTime = System.nanoTime();
        for (int j = 0; j < cam.getResY(); j++) {
            for (int i = 0; i < cam.getResX(); i++) {
                computePrimaryRay(cam, primaryRay, sceneObjectsList, i, j);
            }
        }
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        System.out.println("Primary Ray time: " + elapsedTime / 1_000_000 + "ms");
        drawScreen.drawFrameRGB(primaryRay, cam);

        System.out.println("Finished Primary Rays");
        System.out.print("|-");
        for (int l = 1; l < 100; l++) {
            System.out.print("-");
        }
        System.out.println("-|");
        System.out.print("|-");

        drawScreenExecutor.scheduleAtFixedRate(screenUpdateTask, 10, frameTime, TimeUnit.MILLISECONDS);
        startTime = System.nanoTime();
        for (int currentRay = 1; currentRay < numRays; currentRay++) {
            marchIntersectionLogic(primaryRay, nthRay, sceneObjectsList, numRays, currentRay, numBounces, cam);
            if (updateScreen.get()) {
                drawScreen.drawFrameRGB(primaryRay, cam);
                updateScreen.set(false);
            }
        }
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        System.out.println(numRays + " Secondary Ray Time: " + elapsedTime / 1_000_000 + "ms");

        drawScreenExecutor.shutdown();

        // final print
        drawScreen.drawFrameRGB(primaryRay, cam);
        System.out.print("-|");
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
                    }
                    // hit is already false otherwise
                }
                distance += primaryRayStep;
            }
        }
    }

    public void marchIntersectionLogic(Ray[][] primaryRay, Ray[][] nthRay, List<SceneObjects> sceneObjectsList, int numRays, int currentRay, int numBounces, Camera cam) {
        for (int j = 0; j < cam.getResY(); j++) {
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit()) {
                    nthRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
                    double[][] luminanceRed = new double[numBounces + 1][4];
                    double[][] luminanceGreen = new double[numBounces + 1][4];
                    double[][] luminanceBlue = new double[numBounces + 1][4];
                    // BOUNCES PER RAY
                    // initialize ray starting conditions
                    nthRay[i][j].initializeRay(primaryRay[i][j]);
                    storeHitDataRGB(luminanceRed, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getRBrightness(), nthRay[i][j].getHitObject().getReflecR());
                    storeHitDataRGB(luminanceGreen, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getGBrightness(), nthRay[i][j].getHitObject().getReflecG());
                    storeHitDataRGB(luminanceBlue, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getBBrightness(), nthRay[i][j].getHitObject().getReflecB());
                    for (int currentBounce = 0; currentBounce < numBounces && nthRay[i][j].getHit(); currentBounce++) {
                        // sample a new direction with importance sampling
                        cosineWeightedHemisphereImportanceSampling(nthRay[i][j], nthRay[i][j].getHitObject());
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
                        if (!visibleObjects.isEmpty()) {
                            while (distance <= 25 && !nthRay[i][j].getHit()) {
                                // march the ray
                                nthRay[i][j].marchRay(distance);
                                // CHECK INTERSECTIONS for non-culled objects
                                for (SceneObjects sceneObject1 : visibleObjects) {
                                    if (sceneObject1.intersectionCheck(nthRay[i][j])) {
                                        primaryRay[i][j].addNumHits(); // debug
                                        nthRay[i][j].updateHitProperties(sceneObject1);
                                        // data structure for storing object luminance, dot product and bounce depth, and boolean hit
                                        storeHitDataRGB(luminanceRed, nthRay[i][j], currentBounce, sceneObject1, sceneObject1.getRBrightness(), sceneObject1.getReflecR());
                                        storeHitDataRGB(luminanceGreen, nthRay[i][j], currentBounce, sceneObject1, sceneObject1.getGBrightness(), sceneObject1.getReflecG());
                                        storeHitDataRGB(luminanceBlue, nthRay[i][j], currentBounce, sceneObject1, sceneObject1.getBBrightness(), sceneObject1.getReflecB());
                                    }
                                }
                                distance += secondaryRayStep;
                            }
                        }
                    }

                    double redAmplitude = 0;
                    double blueAmplitude = 0;
                    double greenAmplitude = 0;
                    if (primaryRay[i][j].getHit()) {
                        for (int index = luminanceRed.length - 1; index >= 0; index--) {
                            if (luminanceRed[index][3] == 1) {
                                redAmplitude = ((luminanceRed[index][0] + redAmplitude) * luminanceRed[index][1]) * luminanceRed[index][2];
                            }
                            if (luminanceGreen[index][3] == 1) {
                                greenAmplitude = ((luminanceGreen[index][0] + greenAmplitude) * luminanceGreen[index][1]) * luminanceGreen[index][2];
                            }
                            if (luminanceBlue[index][3] == 1) {
                                blueAmplitude = ((luminanceBlue[index][0] + blueAmplitude) * luminanceBlue[index][1]) * luminanceBlue[index][2];
                            }
                        }
                    }
                    // keep track of absolute brightness
                    primaryRay[i][j].addLightRGBAbsolute(redAmplitude, greenAmplitude, blueAmplitude);
                    // avg brightness = absolute / current Ray
                    primaryRay[i][j].setAvgRed(primaryRay[i][j].getAbsoluteR() / currentRay);
                    primaryRay[i][j].setAvgGreen(primaryRay[i][j].getAbsoluteG() / currentRay);
                    primaryRay[i][j].setAvgBlue(primaryRay[i][j].getAbsoluteB() / currentRay);
                }
            }
        }
        loadingProgress = (int) (((float) currentRay / numRays) * 100); // loading bar
        if (currentProgress < loadingProgress) {
            currentProgress = loadingProgress;
            System.out.print("|");
            System.out.println("current Ray: " + currentRay);
        }
    }

    public void storeHitDataRGB(double[][] luminanceArray, Ray nthRay, int currentBounce, SceneObjects sceneObject, double objectBrightness, double objectReflectivity) {
        int pos = currentBounce + 1;
        luminanceArray[pos][0] = objectBrightness;
        luminanceArray[pos][1] = lambertCosineLaw(nthRay, sceneObject); // dot product
        luminanceArray[pos][2] = objectReflectivity; // reflectivity
        luminanceArray[pos][3] = 1; // boolean hit
    }

    public double lambertCosineLaw(Ray currentRay, SceneObjects sceneObject) {
        sceneObject.calculateNormal(currentRay);
        currentRay.updateNormalisation();
        // dot product of sphere normal and ray direction
        double costheta = Math.abs(sceneObject.getNormalX() * currentRay.getDirX() + sceneObject.getNormalY() * currentRay.getDirY() + sceneObject.getNormalZ() * currentRay.getDirZ());
        return costheta;
    }

    public void cosineWeightedHemisphereImportanceSampling(Ray nthRay, SceneObjects sceneObject) {

        // calculate the reflection direction relative to the normal
        sceneObject.calculateNormal(nthRay);
        double dotproduct = sceneObject.getNormalX() * nthRay.getDirX() + sceneObject.getNormalY() * nthRay.getDirY() + sceneObject.getNormalZ() * nthRay.getDirZ();
        double reflectionX = nthRay.getDirX() - 2 * (dotproduct) * sceneObject.getNormalX();
        double reflectionY = nthRay.getDirY() - 2 * (dotproduct) * sceneObject.getNormalY();
        double reflectionZ = nthRay.getDirZ() - 2 * (dotproduct) * sceneObject.getNormalZ();

        // generate random direction
        // two randoms between 0 and 1
        Random random = new Random();
        double alpha = random.nextDouble();
        double gamma = random.nextDouble();
        // convert to sphereical coodinates
        alpha = Math.acos(Math.sqrt(alpha)); // polar angle
        gamma = 2 * Math.PI * gamma; // azimuthal angle

        // create a sample vector S in tangent space
        double randomX = Math.sin(alpha) * Math.cos(gamma);
        double randomY = Math.sin(alpha) * Math.sin(gamma);
        double randomZ = Math.cos(alpha);
        // normalize random direction
        double randomMagnitude = Math.sqrt(randomX * randomX + randomY * randomY + randomZ * randomZ);
        randomX /= randomMagnitude;
        randomY /= randomMagnitude;
        randomZ /= randomMagnitude;

        // calculate Tangent and Bitangnet vectors using arbitrary vector a
        double aX, aY, aZ;
        // if the normals are exactly 0 there are problems... if statement to catch that
        if (Math.abs(sceneObject.getNormalX()) > 0.0001 || Math.abs(sceneObject.getNormalZ()) > 0.0001) {
            aX = 0;
            aY = 1;
            aZ = 0;
        } else {
            aX = 1;
            aY = 0;
            aZ = 0;
        }
        // tangent equals cross product of normal N and arbitrary vector a
        double tangentX = sceneObject.getNormalY() * aZ - sceneObject.getNormalZ() * aY;
        double tangentY = sceneObject.getNormalZ() * aX - sceneObject.getNormalX() * aZ;
        double tangentZ = sceneObject.getNormalX() * aY - sceneObject.getNormalY() * aX;
        // normalize
        double tangentMagnitude = Math.sqrt(tangentX * tangentX + tangentY * tangentY + tangentZ * tangentZ);
        tangentX /= tangentMagnitude;
        tangentY /= tangentMagnitude;
        tangentZ /= tangentMagnitude;

        // bitangnet equals cross product of tangent and normal
        double bitangentX = sceneObject.getNormalY() * tangentZ - sceneObject.getNormalZ() * tangentY;
        double bitangentY = sceneObject.getNormalZ() * tangentX - sceneObject.getNormalX() * tangentZ;
        double bitangentZ = sceneObject.getNormalX() * tangentY - sceneObject.getNormalY() * tangentX;
        // normalise bitangent
        double bitangentMagnitude = Math.sqrt(bitangentX * bitangentX + bitangentY * bitangentY + bitangentZ * bitangentZ);
        bitangentX /= bitangentMagnitude;
        bitangentY /= bitangentMagnitude;
        bitangentZ /= bitangentMagnitude;

        // set final sampled direction
        // x = randomX * tangentX + randomY * bitangentX + randomZ * normalX
        double directionX = randomX * tangentX + randomY * bitangentX + randomZ * sceneObject.getNormalX();
        double directionY = randomX * tangentY + randomY * bitangentY + randomZ * sceneObject.getNormalY();
        double directionZ = randomX * tangentZ + randomY * bitangentZ + randomZ * sceneObject.getNormalZ();

        // bias direction with roughness calculation:
        // bias the reflection direction with the random direction
        // biasedDirection = (1 - roughness) * reflectionDirection + roughness * randomDirection
        double roughness = sceneObject.getRoughness();
        directionX = ((1 - roughness) * reflectionX) + roughness * directionX;
        directionY = ((1 - roughness) * reflectionY) + roughness * directionY;
        directionZ = ((1 - roughness) * reflectionZ) + roughness * directionZ;

        nthRay.setDirection(directionX, directionY, directionZ);
        nthRay.updateNormalisation();
        // check new dot product - invert if necessary
        dotproduct = sceneObject.getNormalX() * nthRay.getDirX() + sceneObject.getNormalY() * nthRay.getDirY() + sceneObject.getNormalZ() * nthRay.getDirZ();
        if (dotproduct < 0) {
            directionX = -directionX;
            directionY = -directionY;
            directionZ = -directionZ;

            nthRay.setDirection(directionX, directionY, directionZ);
            nthRay.updateNormalisation();
        }
        nthRay.updateOrigin(0.1); // march the ray a tiny amount to move it off the sphere
    }
}