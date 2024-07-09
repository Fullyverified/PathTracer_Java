import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderSingleThreadedBVH {


    public List<BVHNode> BVHNodes = new ArrayList<>();
    private int loadingProgress, currentProgress = 0;
    private String loadingString = "";
    public static double primaryRayStep;
    public static double secondaryRayStep;

    public RenderSingleThreadedBVH() {
    }

    public void constructBVH(List<SceneObjects> sceneObjectsList) {

        // create list leaf nodes
        double epsilon = 0;
        for (SceneObjects sceneObject : sceneObjectsList) {
            BoundingBox boundingBox = new BoundingBox(
                    sceneObject.getBounds()[0] - epsilon, sceneObject.getBounds()[1] + epsilon,
                    sceneObject.getBounds()[2] - epsilon, sceneObject.getBounds()[3] + epsilon,
                    sceneObject.getBounds()[4] - epsilon, sceneObject.getBounds()[5] + epsilon);
            BVHNodes.add(new BVHNode(boundingBox, sceneObject));
        }

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        // determine bounds of all sceneObjects
        for (SceneObjects sceneObject : sceneObjectsList) {
            if (minX > sceneObject.getBounds()[0]) {
                minX = sceneObject.getBounds()[0];
            }
            if (maxX < sceneObject.getBounds()[1]) {
                maxX = sceneObject.getBounds()[1];
            }

            if (minY > sceneObject.getBounds()[2]) {
                minY = sceneObject.getBounds()[2];
            }
            if (maxY < sceneObject.getBounds()[3]) {
                maxY = sceneObject.getBounds()[3];
            }

            if (minZ > sceneObject.getBounds()[4]) {
                minZ = sceneObject.getBounds()[4];
            }
            if (maxZ < sceneObject.getBounds()[5]) {
                maxZ = sceneObject.getBounds()[5];
            }
        }
        double extentX = maxX - minX;
        double extentY = maxY - minY;
        double extentZ = maxZ - minZ;

        while (BVHNodes.size() > 1) {
            double cost = 0, bestCost = Double.POSITIVE_INFINITY;
            BVHNode bestLeft = null;
            BVHNode bestRight = null;

            for (int i = 0; i < BVHNodes.size(); i++) {
                for (int j = i + 1; j < BVHNodes.size(); j++) {
                    BoundingBox combinedBox = new BoundingBox(BVHNodes.get(i), BVHNodes.get(j));
                    cost = combinedBox.getArea() * (BVHNodes.get(i).getNumChildren() + BVHNodes.get(i).getNumChildren());

                    if (cost < bestCost) {
                        bestCost = cost;
                        bestLeft = BVHNodes.get(i);
                        bestRight = BVHNodes.get(j);
                    }
                }
            }
            // create a new BVHNode that has the smallest combined area
            BoundingBox parentBox = new BoundingBox(bestLeft, bestRight);
            BVHNode parentNode = new BVHNode(parentBox, bestLeft, bestRight);
            BVHNodes.remove(bestLeft);
            BVHNodes.remove(bestRight);
            BVHNodes.add(parentNode);
        }

        System.out.println("BVHNodes size: " + BVHNodes.size());
        System.out.println("RootNode numChildren: " + BVHNodes.get(0).getNumChildren());
    }

    public void computePixels(List<SceneObjects> sceneObjectsList, Camera cam, int numRays, int numBounces, long frameTime, boolean ASCII, double primaryRayStep, double secondaryRayStep) {

        RenderSingleThreadedBVH.primaryRayStep = primaryRayStep;
        RenderSingleThreadedBVH.secondaryRayStep = secondaryRayStep;

        constructBVH(sceneObjectsList);
        BVHNode BVHRootNode = BVHNodes.getFirst();
        Ray[][] primaryRay = new Ray[cam.getResX()][cam.getResY()];
        Ray[][] nthRay = new Ray[cam.getResX()][cam.getResY()];
        DrawScreen drawScreen = new DrawScreen(cam.getResX(), cam.getResY(), ASCII);

        ScheduledExecutorService drawScreenExecutor = Executors.newScheduledThreadPool(1);
        AtomicBoolean updateScreen = new AtomicBoolean(false);
        //Define the screen update task
        Runnable screenUpdateTask = () -> {
            updateScreen.set(true);
        };

        for (int j = 0; j < cam.getResY(); j++) {
            for (int i = 0; i < cam.getResX(); i++) {
                computePrimaryRay(cam, primaryRay, i, j, BVHRootNode);
            }
        }
        drawScreen.drawFrameRGB(primaryRay, cam);
        loadingBar();

        drawScreenExecutor.scheduleAtFixedRate(screenUpdateTask, 10, frameTime, TimeUnit.MILLISECONDS);
        for (int currentRay = 1; currentRay < numRays; currentRay++) {
            marchIntersectionLogic(primaryRay, nthRay, BVHRootNode, numRays, currentRay, numBounces, cam);
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
        primaryRay[i][j].marchRay(0);

        BVHNode leafNode = rootNode.searchBVHTree(primaryRay[i][j]) == null ? null : rootNode.searchBVHTree(primaryRay[i][j]);
        double BVHDistanceClose = leafNode != null ? leafNode.getIntersectionDistance(primaryRay[i][j])[0] : -1;
        double BVHDistanceFar = leafNode != null ? leafNode.getIntersectionDistance(primaryRay[i][j])[1] : -1;
        SceneObjects BVHSceneObject = leafNode != null ? leafNode.getSceneObject() : null;

        if (BVHDistanceClose != -1 && BVHDistanceFar != -1 && BVHSceneObject != null) {
            // march the ray to the start of the leaf node bounds
            double distance = BVHDistanceClose;
            while (distance <= BVHDistanceFar && !primaryRay[i][j].getHit()) {
                primaryRay[i][j].marchRay(distance - 0.5); // march ray
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

    public void marchIntersectionLogic(Ray[][] primaryRay, Ray[][] nthRay, BVHNode rootNode, int numRays, int currentRay, int numBounces, Camera cam) {
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
                        cosineWeightedHemisphereImportanceSampling(nthRay[i][j], nthRay[i][j].getHitObject()); // sample a new direction with cosine weighted hemisphere importance sampling
                        BVHNode leafNode = rootNode.searchBVHTree(nthRay[i][j]) == null ? null : rootNode.searchBVHTree(nthRay[i][j]);
                        double BVHDistanceClose = leafNode != null ? leafNode.getIntersectionDistance(nthRay[i][j])[0] : -1;
                        double BVHDistanceFar = leafNode != null ? leafNode.getIntersectionDistance(nthRay[i][j])[1] : -1;
                        SceneObjects BVHSceneObject = leafNode != null ? leafNode.getSceneObject() : null;
                        nthRay[i][j].setHit(false);
                        double distance = BVHDistanceClose;
                        // march ray and check intersections
                        if (BVHDistanceClose != -1 && BVHDistanceFar != -1 && BVHSceneObject != null) {
                            while (distance <= BVHDistanceFar && !nthRay[i][j].getHit()) {
                                nthRay[i][j].marchRay(distance - 0.5); // march the ray to the start of the leaf node bounds
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

    public void loadingBar() {
        System.out.println("Finished Primary Rays");
        System.out.print("|-");
        for (int l = 1; l < 100; l++) {
            System.out.print("-");
        }
        System.out.println("-|");
        System.out.print("|-");
    }
}