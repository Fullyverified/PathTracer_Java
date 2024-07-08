import java.util.ArrayList;
import java.util.List;

public class Main {

    public static int RenderResolutionX = 70;
    public static int aspectX = 4;
    public static int aspectY = 4;
    public static int fov = 60;
    public static int frameTime = 100; // Milliseconds
    public static int raysPerPixel = 3000;
    public static int bouncesPerRay = 8;
    public static boolean ASCIIMode = false;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new AABCubeCenter(10,-3, 0,14,1,7,1,1,1, 0.75)); // floor
        sceneObjectsList.add(new AABCubeCenter(10,3,0,14,1,7,1,1,1, 0.75)); // roof

        sceneObjectsList.add(new AABCubeCenter(8,0,0,1,6,7,1,1,1,0.75)); // back wall

        sceneObjectsList.add(new AABCubeCenter(10,3,3,14,12,1,1,0,0,0.75)); // left wall
        sceneObjectsList.add(new AABCubeCenter(10,3,-3,14,12,1,0,1,0,0.75)); // right wall

        sceneObjectsList.add(new SphereLight(5,2.5,0,1,0.1,1,40,1,0.75)); // oval at ceiling

        sceneObjectsList.add(new Sphere(5,-2,0,0.8,0.8, 0.8,1,1,1,0));

        Camera cam = new Camera(0.075, RenderResolutionX, fov, aspectX, aspectY, -2,0,0, 1, 0, 0, 0, 1, 0);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        RenderSingleThreaded renderSingleThreaded = new RenderSingleThreaded();
        renderSingleThreaded.computePixels(sceneObjectsList, cam, raysPerPixel, bouncesPerRay, frameTime, ASCIIMode);
    }
}