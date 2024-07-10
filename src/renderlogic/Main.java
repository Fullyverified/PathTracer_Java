package renderlogic;

import sceneobjects.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static int RenderResolutionX = 300;
    public static int aspectX = 1;
    public static int aspectY = 1;
    public static int fov = 53;
    public static int frameTime = 45; // Milliseconds
    public static int raysPerPixel = 400;
    public static int bouncesPerRay = 8;
    public static boolean ASCIIMode = false;
    public static double primaryRayStep = 0.01;
    public static double secondaryRayStep = 0.01;
    public static boolean denoise = false;
    public static double denoiseWeight = 0.75;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new AABCubeCenter(10,-3, 0,14,1,7,1,1,1, 0.75)); // floor
        sceneObjectsList.add(new AABCubeCenter(10,3,0,14,1,7,1,1,1, 0.75)); // roof

        sceneObjectsList.add(new AABCubeCenter(8,0,0,1,6,7,1,1,1,0.75)); // back wall

        sceneObjectsList.add(new AABCubeCenter(10,3,3,14,12,1,1,0,0,0.75)); // left wall
        sceneObjectsList.add(new AABCubeCenter(10,3,-3,14,12,1,0,1,0,0.75)); // right wall

        sceneObjectsList.add(new SphereLight(5,2.5,0,1,0.1,1,40,1,0.75)); // oval at ceiling

        sceneObjectsList.add(new GlassSphere(3,0,1.5,0.8,1,1,1,0.75,1.52));
        //sceneObjectsList.add(new Sphere(5,-1.7,1,0.8,0.8, 0.8,1,1,1,0.75)); // sphere
        sceneObjectsList.add(new Sphere(5,-1.7,-1,0.8,0.8, 0.8,1,1,1,0)); // mirror ball

        Camera cam = new Camera(0.05, RenderResolutionX, fov, aspectX, aspectY, -2,0,0, 1, 0, 0, 0, 1, 0);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        RenderSingleThreadedBVH renderSingleThreadedBVH = new RenderSingleThreadedBVH();
        renderSingleThreadedBVH.computePixels(sceneObjectsList, cam, raysPerPixel, bouncesPerRay);

    }
}

// Air: 1.0003, Water: 1.33, Glass 1.5 - 1.9 (normal window 1.52), Diamond 2.42, Acrylic 1.49, Vegetable Oil 1.47, Ethanol 1.36