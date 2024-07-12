package renderlogic;
import sceneobjects.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static int RenderResolutionX = 800;
    public static int aspectX = 4;
    public static int aspectY = 3;
    public static int fov = 30;
    public static int frameTime = 100; // Milliseconds
    public static int raysPerPixel = 1000;
    public static int bouncesPerRay = 5;
    public static boolean ASCIIMode = false;
    public static double primaryRayStep = 0.01;
    public static double secondaryRayStep = 0.1;
    public static boolean denoise = false;
    public static double denoiseWeight = 0.75;
    public static boolean reinhardToneMapping = false;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new AABCubeBounds(0, 32, -1, 0, -16, 16, 1, 1, 1, 1)); // floor
        sceneObjectsList.add(new AABCubeBounds(32, 33, 0, 15, -16, 16, 1, 1, 1, 1)); // back wall

        sceneObjectsList.add(new AABCubeBounds(0, 30, 0, 15, -15, -14, 1, 1, 1, 1)); // right wall
        sceneObjectsList.add(new AABCubeBounds(0, 30, 0, 15, 14, 15, 1, 1, 1, 1)); // left wall

        sceneObjectsList.add(new SphereLight(10,15,11,1,1,1,40,1,0.9)); // light

        // back row
        sceneObjectsList.add(new Sphere(14, 2, 6, 2.5, 2, 2, 1, 1, 1, 0.9)); // right
        sceneObjectsList.add(new Sphere(14, 2, 0, 2.5, 2, 2, 1, 1, 1, 0.3)); // left
        sceneObjectsList.add(new Sphere(14, 2, -6, 2.5, 2, 2, 1, 1, 1, 0.1)); // middle

        // Colored spheres
        sceneObjectsList.add(new Sphere(9, 1, 6, 1, 1, 1, 1, 0, 0, 0.75)); // Red sphere
        sceneObjectsList.add(new Sphere(9, 1, 0, 1, 1, 1, 0, 0, 1, 0.75)); // Blue sphere
        sceneObjectsList.add(new Sphere(9, 1, -6, 1, 1, 1, 0, 1, 0, 0.75)); // Green sphere

        // floor rectangles
        sceneObjectsList.add(new AABCubeCenter(9,0.125,3,3,0.25,3,1,1,1,1));
        sceneObjectsList.add(new AABCubeCenter(9,0.125,-3,3,0.25,3,1,1,1,1));

        // spheres on rectangles
        sceneObjectsList.add(new Sphere(9, 1.25, 3, 1, 1, 1, 1, 1, 1, 1)); // Red sphere
        sceneObjectsList.add(new GlassSphere(9, 1.25, -3, 1, 1, 1, 1,1,1.53)); // Blue sphere

        // elevated spheres
        //sceneObjectsList.add(new GlassSphere(15, 7.5, 0, 1, 1, 1, 1,1,1.53)); // Blue sphere
        sceneObjectsList.add(new Sphere(15, 7.5,8.5,1,1,1,1, 1,1,0)); // left
        sceneObjectsList.add(new Sphere(15, 7.5,0,1,1,1,1, 1,1,0)); // middle
        sceneObjectsList.add(new Sphere(15, 7.5,-8.5,1,1,1,1, 1,1,0)); // right

        Camera cam = new Camera(40, RenderResolutionX, fov, aspectX, aspectY, -20, 8, 0, 1, -0.15, 0, 0, 1, 0);

        RenderSingleThreadedBVH renderSingleThreadedBVH = new RenderSingleThreadedBVH();
        renderSingleThreadedBVH.computePixels(sceneObjectsList, cam, raysPerPixel, bouncesPerRay);
    }
}

// Air: 1.0003, Water: 1.33, Glass 1.5 - 1.9 (normal window 1.52), Diamond 2.42, Acrylic 1.49, Vegetable Oil 1.47, Ethanol 1.36