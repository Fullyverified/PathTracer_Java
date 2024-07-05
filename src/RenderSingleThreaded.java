import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderSingleThreaded {

    public List<SceneObjects> visibleObjects = new ArrayList<>();
    private int loadingProgress, currentProgress = 0;
    private String loadingString = "";

    public RenderSingleThreaded() {
    }

    public void computePixels(List<SceneObjects> sceneObjectsList, Camera cam, int numRays, int numBounces, long frameTime, boolean ASCII) {
        Ray[][] primaryRay = new Ray[cam.getResX()][cam.getResY()];
        Ray[][] nthRay = new Ray[cam.getResX()][cam.getResY()];

        ScheduledExecutorService drawScreenExecutor = Executors.newScheduledThreadPool(1);
        AtomicBoolean updateScreen = new AtomicBoolean(false);
        // Define the screen update task
        Runnable screenUpdateTask = () -> {
            updateScreen.set(true);
        };

        DrawScreen drawScreen = new DrawScreen(cam.getResX(), cam.getResY(), ASCII);

        for (int j = 0; j < cam.getResY(); j++) {
            for (int i = 0; i < cam.getResX(); i++) {
                computePrimaryRay(cam, primaryRay, sceneObjectsList, i, j);
            }
        }

        System.out.println("Finished Primary Rays");
        System.out.print("|-");
        for (int l = 1; l < 100; l++) {
            System.out.print("-");
        }
        System.out.println("-|");
        System.out.print("|-");


        drawScreenExecutor.scheduleAtFixedRate(screenUpdateTask, 50, frameTime, TimeUnit.MILLISECONDS);

        // sample a ray for every pixel, then move to the next ray
        for (int currentRay = 1; currentRay < numRays; currentRay++) {
            marchIntersectionLogic(primaryRay, nthRay, sceneObjectsList, numRays, currentRay, numBounces, cam);
            if (updateScreen.get()) {
                drawScreen.drawFrameRGB(primaryRay, cam);
                updateScreen.set(false);
            }
        }


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
                        // add light amplitude
                        if (sceneObject1.getLuminance() != 0) {
                            primaryRay[i][j].addRed(lambertCosineLaw(primaryRay[i][j], sceneObject1) * sceneObject1.getRBrightness() * sceneObject1.getReflecR());
                            primaryRay[i][j].addGreen(lambertCosineLaw(primaryRay[i][j], sceneObject1) * sceneObject1.getGBrightness() * sceneObject1.getReflecG());
                            primaryRay[i][j].addBlue(lambertCosineLaw(primaryRay[i][j], sceneObject1) * sceneObject1.getBBrightness() * sceneObject1.getReflecB());
                        }
                    }
                    // hit is already false otherwise
                }
                distance += 0.01;
            }
        }
    }

    public void marchIntersectionLogic(Ray[][] primaryRay, Ray[][] nthRay, List<SceneObjects> sceneObjectsList, int numRays, int currentRay, int numBounces, Camera cam) {
        for (int j = 0; j < cam.getResY(); j++) {
            for (int i = 0; i < cam.getResX(); i++) {
                if (primaryRay[i][j].getHit()) {
                    nthRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
                    double[][] luminanceRed= new double[numBounces + 1][5];
                    double[][] luminanceGreen = new double[numBounces + 1][5];
                    double[][] luminanceBlue = new double[numBounces + 1][5];
                    // BOUNCES PER RAY
                    // initialize ray starting conditions
                    nthRay[i][j].initializeRay(primaryRay[i][j]);
                    storeHitDataRGB(luminanceRed, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getRBrightness(), nthRay[i][j].getHitObject().getReflecR());
                    storeHitDataRGB(luminanceGreen, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getGBrightness(), nthRay[i][j].getHitObject().getReflecG());
                    storeHitDataRGB(luminanceBlue, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getBBrightness(), nthRay[i][j].getHitObject().getReflecB());
                    for (int currentBounce = 0; currentBounce < numBounces && nthRay[i][j].getHit(); currentBounce++) {
                        if (currentBounce == 0) {
                            // first bounce uses random direction
                            randomDirection(nthRay[i][j], nthRay[i][j].getHitObject());
                            //reflectionBounce(nthRay[i][j], nthRay[i][j].getHitObject());
                        } else {
                            randomDirection(nthRay[i][j], nthRay[i][j].getHitObject());
                            //reflectionBounce(nthRay[i][j], nthRay[i][j].getHitObject());
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
                                    // data structure for storing object luminance, dot product and bounce depth, and boolean hit
                                    storeHitDataRGB(luminanceRed, nthRay[i][j], currentBounce, sceneObject1, sceneObject1.getRBrightness(), sceneObject1.getReflecR());
                                    storeHitDataRGB(luminanceGreen, nthRay[i][j], currentBounce, sceneObject1, sceneObject1.getGBrightness(), sceneObject1.getReflecG());
                                    storeHitDataRGB(luminanceBlue, nthRay[i][j], currentBounce, sceneObject1, sceneObject1.getBBrightness(), sceneObject1.getReflecB());
                                }
                            }
                            distance += 0.1;
                        }
                    }

                    double redAmplitude = 0;
                    double blueAmplitude = 0;
                    double greenAmplitude = 0;
                    if (primaryRay[i][j].getHit()) {
                        for (int index = luminanceRed.length - 1; index >= 0; index--) {
                            if (luminanceRed[index][3] == 1) {
                                redAmplitude = ((luminanceRed[index][0] + redAmplitude) * luminanceRed[index][1]) * luminanceRed[index][4];
                            }
                            if (luminanceGreen[index][3] == 1) {
                                blueAmplitude = ((luminanceGreen[index][0] + blueAmplitude) * luminanceGreen[index][1]) * luminanceGreen[index][4];
                            }
                            if (luminanceBlue[index][3] == 1) {
                                greenAmplitude = ((luminanceBlue[index][0] + greenAmplitude) * luminanceBlue[index][1]) * luminanceBlue[index][4];
                            }
                        }
                    }
                    primaryRay[i][j].addLightRGB(redAmplitude, greenAmplitude, blueAmplitude);
                }
            }
        }
        loadingProgress = (int) (((float) currentRay / numRays) * 100); // loading bar
        if (currentProgress < loadingProgress) {
            currentProgress = loadingProgress;
            System.out.print("|");
        }
    }

    public void storeHitDataRGB(double[][] luminanceArray, Ray nthRay, int currentBounce, SceneObjects sceneObject, double objectBrightness, double objectReflectivity) {
        int pos = currentBounce + 1;
        luminanceArray[pos][0] = objectBrightness;
        luminanceArray[pos][1] = lambertCosineLaw(nthRay, sceneObject); // dot product
        luminanceArray[pos][2] = currentBounce + 1; // which bounce
        luminanceArray[pos][3] = 1; // boolean hit
        luminanceArray[pos][4] = objectReflectivity; // reflectivity

        if (currentBounce == -1) {
            luminanceArray[pos][1] = 1;
        }
    }

    public void randomDirection(Ray nthRay, SceneObjects sceneObject) {
        double dotproduct = -1;
        Random random = new Random();

        nthRay.marchRay(0);
        sceneObject.calculateNormal(nthRay);

        while (dotproduct <= 0) {
            // Generate a random direction uniformly on a sphere
            double theta = Math.acos(2 * random.nextDouble() - 1); // polar angle
            double phi = 2 * Math.PI * random.nextDouble(); // azimuthal angle

            nthRay.setDirX(Math.sin(theta) * Math.cos(phi));
            nthRay.setDirY(Math.sin(theta) * Math.sin(phi));
            nthRay.setDirZ(Math.cos(theta));

            // Normalize the random direction
            nthRay.updateNormalisation();

            // Calculate the dot product
            dotproduct = sceneObject.getNormalX() * nthRay.getDirX() + sceneObject.getNormalY() * nthRay.getDirY() + sceneObject.getNormalZ() * nthRay.getDirZ();
        }
        nthRay.updateOrigin(0.15); // march the ray a tiny amount to move it off the sphere
    }

    // R = I - 2 * (I dot N) * N
    public void reflectionBounce(Ray nthRay, SceneObjects sceneObject) {
        double dotproduct = sceneObject.getNormalX() * nthRay.getDirX() + sceneObject.getNormalY() * nthRay.getDirY() + sceneObject.getNormalZ() * nthRay.getDirZ();
        sceneObject.calculateNormal(nthRay);
        double reflectionX = nthRay.getDirX() - 2 * (dotproduct) * sceneObject.getNormalX();
        double reflectionY = nthRay.getDirY() - 2 * (dotproduct) * sceneObject.getNormalY();
        double reflectionZ = nthRay.getDirZ() - 2 * (dotproduct) * sceneObject.getNormalZ();

        nthRay.setDirection(reflectionX, reflectionY, reflectionZ);
        nthRay.updateNormalisation();
        nthRay.updateOrigin(0.15); // march the ray a tiny amount to move it off the sphere
    }

    public double lambertCosineLaw(Ray currentRay, SceneObjects sceneObject) {
        sceneObject.calculateNormal(currentRay);
        currentRay.updateNormalisation();

        // dot product of sphere normal and ray direction
        double costheta = Math.abs(sceneObject.getNormalX() * currentRay.getDirX() + sceneObject.getNormalY() * currentRay.getDirY() + sceneObject.getNormalZ() * currentRay.getDirZ());
        return costheta;
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