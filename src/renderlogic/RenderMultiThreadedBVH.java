package renderlogic;

import bvh.*;
import sceneobjects.*;


import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Arrays;

public class RenderMultiThreadedBVH {

    public long startTime, endTime, elapsedTime;
    public List<BVHNode> BVHNodes = new ArrayList<>();
    private int loadingProgress, currentProgress = 0;
    private String loadingString = "";
    public static double primaryRayStep = Main.primaryRayStep;
    public static double secondaryRayStep = Main.secondaryRayStep;
    public int hascounted = 0;
    public int checks = 0;
    int numThreads = Runtime.getRuntime().availableProcessors();

    public RenderMultiThreadedBVH() {
    }

    public void constructBVH(List<SceneObjects> sceneObjectsList) {

        // create list leaf nodes
        for (SceneObjects sceneObject : sceneObjectsList) {
            BoundingBox boundingBox = new BoundingBox(
                    sceneObject.getBounds()[0], sceneObject.getBounds()[1],
                    sceneObject.getBounds()[2], sceneObject.getBounds()[3],
                    sceneObject.getBounds()[4], sceneObject.getBounds()[5]);
            BVHNodes.add(new BVHNode(boundingBox, sceneObject));
        }

        while (BVHNodes.size() > 1) {
            double cost = 0, bestCost = Double.POSITIVE_INFINITY;
            BVHNode bestLeft = null;
            BVHNode bestRight = null;

            for (int i = 0; i < BVHNodes.size(); i++) {
                for (int j = i + 1; j < BVHNodes.size(); j++) {
                    BoundingBox combinedBox = new BoundingBox(BVHNodes.get(i), BVHNodes.get(j));
                    cost = (combinedBox.getArea() / (BVHNodes.get(i).getArea() + BVHNodes.get(j).getArea())) * (BVHNodes.get(i).getNumChildren() + BVHNodes.get(i).getNumChildren());
                    if (cost < bestCost) {
                        bestCost = cost;
                        bestLeft = BVHNodes.get(i);
                        bestRight = BVHNodes.get(j);
                    }
                }
            }
            // create a new bvh.bvh.BVHNode that has the smallest combined area
            BoundingBox parentBox = new BoundingBox(bestLeft, bestRight);
            BVHNode parentNode = new BVHNode(parentBox, bestLeft, bestRight);
            BVHNodes.remove(bestLeft);
            BVHNodes.remove(bestRight);
            BVHNodes.add(parentNode);
        }

        System.out.println("BVHNodes size: " + BVHNodes.size());
        System.out.println("RootNode numChildren: " + BVHNodes.get(0).getNumChildren());
    }

    public void computePixels(List<SceneObjects> sceneObjectsList, Camera cam, int numRays, int numBounces) {
        System.out.println("Avalialbe Threads: " + numThreads);
        Ray[][] primaryRay = new Ray[cam.getResX()][cam.getResY()];
        Ray[][] nthRay = new Ray[cam.getResX()][cam.getResY()];
        DrawScreen drawScreen = new DrawScreen(cam.getResX(), cam.getResY(), cam);

        ScheduledExecutorService drawScreenExecutor = Executors.newScheduledThreadPool(1);
        //Define the screen update task
        Runnable screenUpdateTask = () -> {
            drawScreen.drawFrameRGB(primaryRay, cam);
        };
        constructBVH(sceneObjectsList);
        BVHNode BVHRootNode = BVHNodes.getFirst();

        startTime = System.nanoTime();
        for (int j = 0; j < cam.getResY(); j++) {
            for (int i = 0; i < cam.getResX(); i++) {
                computePrimaryRay(cam, primaryRay, i, j, BVHRootNode);
            }
        }
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        System.out.println("Primary Ray time: " + elapsedTime / 1_000_000 + "ms");
        drawScreen.drawFrameRGB(primaryRay, cam);
        loadingBar();

        drawScreenExecutor.scheduleAtFixedRate(screenUpdateTask, 10, Main.frameTime, TimeUnit.MILLISECONDS);
        startTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // reuse arrays
        double[][] luminanceRed = new double[numBounces + 1][4];
        double[][] luminanceGreen = new double[numBounces + 1][4];
        double[][] luminanceBlue = new double[numBounces + 1][4];

        int[][] boundArrayX = new int[numThreads][2]; // x bounds
        threadRenderSegmentation(cam.getResX(), numThreads, boundArrayX);
        int[][] boundArrayY = new int[numThreads][2]; // y bounds
        threadRenderSegmentation(cam.getResY(), numThreads, boundArrayY);

        for (int segmenty = 0; segmenty < numThreads; segmenty++) {
            for (int segmentx = 0; segmentx < numThreads; segmentx++) {
                int finalSegmentx = segmentx;
                int finalSegmenty = segmenty;
                BVHNode threadBVHRootNode = deepCopyBVHNode(BVHRootNode);
                executor.execute(() -> marchIntersectionLogic(primaryRay, nthRay, threadBVHRootNode, numRays, numBounces, cam, luminanceRed, luminanceGreen, luminanceBlue, boundArrayX[finalSegmentx][0], boundArrayX[finalSegmentx][1], boundArrayY[finalSegmenty][0], boundArrayY[finalSegmenty][1]));
            }
            System.out.print("--");
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                System.err.println("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted while waiting for termination.");
            Thread.currentThread().interrupt();  // Restore the interrupted status
        }

        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        System.out.println(numRays + " Secondary Ray Time: " + elapsedTime / 1_000_000 + "ms");

        drawScreenExecutor.shutdown();

        // final print
        drawScreen.drawFrameRGB(primaryRay, cam);
        System.out.print("-|");
    }

    public void computePrimaryRay(Camera cam, Ray[][] primaryRay, int i, int j, BVHNode rootNode) {
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
        primaryRay[i][j].updateNormalisation(); // update vector normalisation
        primaryRay[i][j].march(0);

        BVHNode leafNode = rootNode.searchBVHTree(primaryRay[i][j]);
        double BVHDistanceClose = leafNode != null ? leafNode.getIntersectionDistance(primaryRay[i][j])[0] : -1;
        double BVHDistanceFar = leafNode != null ? leafNode.getIntersectionDistance(primaryRay[i][j])[1] : -1;
        SceneObjects BVHSceneObject = leafNode != null ? leafNode.getSceneObject() : null;
        if (BVHDistanceClose != -1 && BVHDistanceFar != -1 && BVHSceneObject != null) {
            // march the ray to the start of the leaf node bounds
            double distance = BVHDistanceClose;
            while (distance <= BVHDistanceFar && !primaryRay[i][j].getHit()) {
                primaryRay[i][j].march(distance - 0.05); // march ray
                if (BVHSceneObject.intersectionCheck(primaryRay[i][j])) {
                    primaryRay[i][j].setHitPoint(primaryRay[i][j].getPosX(), primaryRay[i][j].getPosY(), primaryRay[i][j].getPosZ()); // get the position of the intersection
                    primaryRay[i][j].setHit(true);
                    primaryRay[i][j].setHitObject(BVHSceneObject);
                }
                // hit is already false otherwise
                distance += primaryRayStep;
            }
        }
    }

    public void marchIntersectionLogic(Ray[][] primaryRay, Ray[][] nthRay, BVHNode rootNode, int numRays, int numBounces, Camera cam, double[][] luminanceRed, double[][] luminanceGreen, double[][] luminanceBlue, int lowerX, int upperX, int lowerY, int upperY) {
        for (int currentRay = 1; currentRay <= numRays; currentRay++) {
            for (int j = 0; j < cam.getResY(); j++) {
                for (int i = 0; i < cam.getResX(); i++) {
                    if (primaryRay[i][j].getHit()) {
                        nthRay[i][j] = new Ray(primaryRay[i][j].getHitPointX(), primaryRay[i][j].getHitPointY(), primaryRay[i][j].getHitPointZ());
                        clearArray(luminanceRed);
                        clearArray(luminanceGreen);
                        clearArray(luminanceBlue);
                        // BOUNCES PER RAY
                        // initialize ray starting conditions
                        nthRay[i][j].initializeRay(primaryRay[i][j]);
                        storeHitDataRGB(luminanceRed, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getRBrightness(), nthRay[i][j].getHitObject().getReflecR());
                        storeHitDataRGB(luminanceGreen, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getGBrightness(), nthRay[i][j].getHitObject().getReflecG());
                        storeHitDataRGB(luminanceBlue, nthRay[i][j], -1, nthRay[i][j].getHitObject(), nthRay[i][j].getHitObject().getBBrightness(), nthRay[i][j].getHitObject().getReflecB());
                        for (int currentBounce = 0; currentBounce < numBounces && nthRay[i][j].getHit(); currentBounce++) {
                            // sample a new direction with importance sampling
                            if (nthRay[i][j].getHitObject().getTransparent()) {
                                refractionDirection(nthRay[i][j], nthRay[i][j].getHitObject());
                            } else {
                                cosineWeightedHemisphereImportanceSampling(nthRay[i][j], nthRay[i][j].getHitObject(), false);
                            }
                            BVHNode leafNode = rootNode.searchBVHTree(nthRay[i][j]);
                            double BVHDistanceClose = leafNode != null ? leafNode.getIntersectionDistance(nthRay[i][j])[0] : -1;
                            double BVHDistanceFar = leafNode != null ? leafNode.getIntersectionDistance(nthRay[i][j])[1] : -1;
                            SceneObjects BVHSceneObject = leafNode != null ? leafNode.getSceneObject() : null;
                            nthRay[i][j].setHit(false);
                            double distance = BVHDistanceClose;
                            // march ray and check intersections
                            if (BVHDistanceClose != -1 && BVHDistanceFar != -1 && BVHSceneObject != null) {
                                while (distance <= BVHDistanceFar && !nthRay[i][j].getHit()) {
                                    nthRay[i][j].march(distance - 0.05); // march the ray to the start of the leaf node bounds
                                    if (BVHSceneObject.intersectionCheck(nthRay[i][j])) {
                                        nthRay[i][j].updateHitProperties(BVHSceneObject);
                                        // data structure for storing object luminance, dot product and bounce depth, and boolean hit
                                        storeHitDataRGB(luminanceRed, nthRay[i][j], currentBounce, BVHSceneObject, BVHSceneObject.getRBrightness(), BVHSceneObject.getReflecR());
                                        storeHitDataRGB(luminanceGreen, nthRay[i][j], currentBounce, BVHSceneObject, BVHSceneObject.getGBrightness(), BVHSceneObject.getReflecG());
                                        storeHitDataRGB(luminanceBlue, nthRay[i][j], currentBounce, BVHSceneObject, BVHSceneObject.getBBrightness(), BVHSceneObject.getReflecB());
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
                        // avg brightness = absolute / current renderlogic.Ray
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

    public void cosineWeightedHemisphereImportanceSampling(Ray ray, SceneObjects sceneObject, boolean flipNormal) {
        // boolean flip normal - if inside the object the normal needs to be inverted
        double normalx, normaly, normalz;
        if (flipNormal) {
            normalx = -sceneObject.getNormalX();
            normaly = -sceneObject.getNormalY();
            normalz = -sceneObject.getNormalZ();
        } else {
            normalx = sceneObject.getNormalX();
            normaly = sceneObject.getNormalY();
            normalz = sceneObject.getNormalZ();
        }

        // calculate the reflection direction relative to the normal
        sceneObject.calculateNormal(ray);
        double dotproduct = normalx * ray.getDirX() + normaly * ray.getDirY() + normalz * ray.getDirZ();
        double reflectionX = ray.getDirX() - 2 * (dotproduct) * normalx;
        double reflectionY = ray.getDirY() - 2 * (dotproduct) * normaly;
        double reflectionZ = ray.getDirZ() - 2 * (dotproduct) * normalz;

        // generate random direction
        // two randoms between 0 and 1
        Random random = new Random();
        double alpha = random.nextDouble();
        double gamma = random.nextDouble();
        // convert to sphereical coodinates
        alpha = Math.acos(Math.sqrt(alpha)); // polar angle - sqrt more likely to be near the pole (z axis)
        gamma = 2 * Math.PI * gamma; // azimuthal angle

        // convert random direction in spherical coordinates to vector coordinates
        double randomX = Math.sin(alpha) * Math.cos(gamma);
        double randomY = Math.sin(alpha) * Math.sin(gamma);
        double randomZ = Math.cos(alpha);
        // normalize random direction
        double randomMagnitude = Math.sqrt(randomX * randomX + randomY * randomY + randomZ * randomZ);
        randomX /= randomMagnitude;
        randomY /= randomMagnitude;
        randomZ /= randomMagnitude;
        // convert to tangent space (a coordinate system defined by the normal of the surface)
        // calculate Tangent and Bitangnet vectors using arbitrary vector a
        double aX, aY, aZ; // arbitrary vector a
        // if the normals are exactly 0 there are problems... if statement to catch that
        if (Math.abs(normalx) > 0.0001 || Math.abs(normalz) > 0.0001) {
            aX = 0;
            aY = 1;
            aZ = 0;
        } else {
            aX = 1;
            aY = 0;
            aZ = 0;
        }
        // tangent vector T equals cross product of normal N and arbitrary vector a
        double tangentX = normaly * aZ - normalz * aY;
        double tangentY = normalz * aX - normalx * aZ;
        double tangentZ = normalx * aY - normaly * aX;
        // normalize
        double tangentMagnitude = Math.sqrt(tangentX * tangentX + tangentY * tangentY + tangentZ * tangentZ);
        tangentX /= tangentMagnitude;
        tangentY /= tangentMagnitude;
        tangentZ /= tangentMagnitude;

        // bitangnet vector B equals cross product of tangent and normal
        double bitangentX = normaly * tangentZ - normalz * tangentY;
        double bitangentY = normalz * tangentX - normalx * tangentZ;
        double bitangentZ = normalx * tangentY - normaly * tangentX;
        // normalise bitangent
        double bitangentMagnitude = Math.sqrt(bitangentX * bitangentX + bitangentY * bitangentY + bitangentZ * bitangentZ);
        bitangentX /= bitangentMagnitude;
        bitangentY /= bitangentMagnitude;
        bitangentZ /= bitangentMagnitude;

        // set final sampled direction
        // x = randomX * tangentX + randomY * bitangentX + randomZ * normalX
        double directionX = randomX * tangentX + randomY * bitangentX + randomZ * normalx;
        double directionY = randomX * tangentY + randomY * bitangentY + randomZ * normaly;
        double directionZ = randomX * tangentZ + randomY * bitangentZ + randomZ * normalz;

        // bias direction with roughness calculation:
        // bias the reflection direction with the random direction
        // biasedDirection = (1 - roughness) * reflectionDirection + roughness * randomDirection
        double roughness = sceneObject.getRoughness();
        directionX = ((1 - roughness) * reflectionX) + roughness * directionX;
        directionY = ((1 - roughness) * reflectionY) + roughness * directionY;
        directionZ = ((1 - roughness) * reflectionZ) + roughness * directionZ;

        ray.setDirection(directionX, directionY, directionZ);
        ray.updateNormalisation();
        // check new dot product - invert if necessary
        dotproduct = normalx * ray.getDirX() + normaly * ray.getDirY() + normalz * ray.getDirZ();
        if (dotproduct < 0) {
            directionX = -directionX;
            directionY = -directionY;
            directionZ = -directionZ;

            ray.setDirection(directionX, directionY, directionZ);
            ray.updateNormalisation();
        }
        ray.updateOrigin(0.1); // march the ray a tiny amount to move it off the sphere

    }

    public void refractionDirection(Ray ray, SceneObjects sceneObject) {

        // refraction for transparent objects
        double n1 = 1.0003; // Refractive index of air
        double n2 = sceneObject.getRefractiveIndex();
        // cosine of incident angle
        double cosTheta1 = -(sceneObject.getNormalX() * ray.getDirX() + sceneObject.getNormalY() * ray.getDirY() + sceneObject.getNormalZ() * ray.getDirZ()); // cos(theta_1) is the negative of the dot product because normal may be pointing inwards
        double sinTheta1 = Math.sqrt(1.0 - cosTheta1 * cosTheta1);
        double sinTheta2 = (n1 / n2) * sinTheta1;

        if (sinTheta2 >= 1) {
            // Total internal reflection - bounce off object
            cosineWeightedHemisphereImportanceSampling(ray, sceneObject, false);
        } else {
            // Valid refraction into next medium
            double cosTheta2 = Math.sqrt(1.0 - sinTheta2 * sinTheta2);
            double refractedX = ray.getDirX() * (n1 / n2) + ((n1 / n2) * cosTheta1 - cosTheta2) * sceneObject.getNormalX();
            double refractedY = ray.getDirY() * (n1 / n2) + ((n1 / n2) * cosTheta1 - cosTheta2) * sceneObject.getNormalY();
            double refractedZ = ray.getDirZ() * (n1 / n2) + ((n1 / n2) * cosTheta1 - cosTheta2) * sceneObject.getNormalZ();
            ray.setDirection(refractedX, refractedY, refractedZ);
            ray.updateNormalisation();
            ray.updateOrigin(sceneObject.distanceToEntryExit(ray)[1]); // march the ray to the other side of the object
            // compute new refraction for ray exit (from object to air)
            n1 = sceneObject.getRefractiveIndex(); // Refractive index of air
            n2 = 1.0003;
            // cosine of incident angle
            cosTheta1 = (sceneObject.getNormalX() * ray.getDirX() + sceneObject.getNormalY() * ray.getDirY() + sceneObject.getNormalZ() * ray.getDirZ()); // cos(theta_1) is the negative of the dot product because normal may be pointing inwards
            sinTheta1 = Math.sqrt(1.0 - cosTheta1 * cosTheta1);
            sinTheta2 = (n1 / n2) * sinTheta1;
            if (sinTheta2 >= 1) {
                while (sinTheta2 >= 1) {
                    cosineWeightedHemisphereImportanceSampling(ray, sceneObject, true);
                    ray.updateOrigin(-0.1); // undo the march from the previous method
                    ray.updateOrigin(sceneObject.distanceToEntryExit(ray)[1]); // march the ray to the other side of the object
                    // recalculate the sin of the angle to work out if the ray still has total internal reflection or not
                    cosTheta1 = (sceneObject.getNormalX() * ray.getDirX() + sceneObject.getNormalY() * ray.getDirY() + sceneObject.getNormalZ() * ray.getDirZ()); // cos(theta_1) is the negative of the dot product because normal may be pointing inwards
                    sinTheta1 = Math.sqrt(1.0 - cosTheta1 * cosTheta1);
                    sinTheta2 = (n1 / n2) * sinTheta1;
                }
            } else {
                // Valid refraction out of object
                cosTheta2 = Math.sqrt(1.0 - sinTheta2 * sinTheta2);
                refractedX = ray.getDirX() * (n1 / n2) + ((n1 / n2) * cosTheta1 - cosTheta2) * sceneObject.getNormalX();
                refractedY = ray.getDirY() * (n1 / n2) + ((n1 / n2) * cosTheta1 - cosTheta2) * sceneObject.getNormalY();
                refractedZ = ray.getDirZ() * (n1 / n2) + ((n1 / n2) * cosTheta1 - cosTheta2) * sceneObject.getNormalZ();
                ray.setDirection(refractedX, refractedY, refractedZ);
                ray.updateNormalisation();
                ray.updateOrigin(sceneObject.distanceToEntryExit(ray)[1] + 0.1); // march the ray to the other side of the object
            }
        }
    }

    public double[] convertToVector(double alpha, double gamma) {
        // convert to sphereical coodinates
        alpha = Math.acos(Math.sqrt(alpha)); // polar angle
        gamma = 2 * Math.PI * gamma; // azimuthal angle

        // create a sample vector S in tangent space
        double X = Math.sin(alpha) * Math.cos(gamma);
        double Y = Math.sin(alpha) * Math.sin(gamma);
        double Z = Math.cos(alpha);
        // normalize random direction
        double randomMagnitude = Math.sqrt(X * X + Y * Y + Z * Z);
        randomMagnitude = 1 / randomMagnitude;
        X *= randomMagnitude;
        Y *= randomMagnitude;
        Z *= randomMagnitude;
        return new double[]{X, Y, Z};
    }

    public double[] convertToRadians(double x, double y, double z) {
        double r = Math.sqrt(x * x + y * y + z * z);
        double theta = Math.acos(z / r);
        double phi = Math.atan2(y, x);
        return new double[]{theta, phi};
    }


    public void loadingBar() {
        System.out.println("Finished Primary Rays");
        System.out.print("|-");
        for (int l = 1; l < 100; l++) {
            System.out.print("-");
        }
        System.out.println("-|");
        System.out.print("|-");
    }

    public void clearArray(double[][] array) {
        for (double[] row : array) {
            Arrays.fill(row, 0.0);
        }
    }

    public static void threadRenderSegmentation(int res, int nThreads, int[][] boundArray) {
        int bound = res / nThreads;
        int lower = 0, upper = 0;
        for (int i = 0; i < nThreads; i++) {
            if (i != nThreads - 1) {
                lower = i * bound;
                boundArray[i][0] = lower;
                upper = ((i + 1) * bound) - 1;
                boundArray[i][1] = upper;
            }
            if (i == nThreads - 1) {
                lower = i * bound;
                boundArray[i][0] = lower - 1;
                upper = res;
                boundArray[i][1] = upper - 1;
            }
        }
    }

    public BVHNode deepCopyBVHNode(BVHNode original) {
        if (original == null) return null;

        // Assuming BVHNode has a copy constructor
        BVHNode copy = new BVHNode(original);

        return copy;
    }

}