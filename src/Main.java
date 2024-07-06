import java.util.ArrayList;
import java.util.List;

public class Main {

    public static int RenderResolutionX = 600;
    public static int aspectX = 4;
    public static int aspectY = 3;
    public static int fov = 60;
    public static int frameTime = 60; // Milliseconds
    public static int raysPerPixel = 5000;
    public static int bouncesPerRay = 4;
    public static boolean ASCIIMode = false;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new Sphere(7, 2.5, 2.5, 1, 1,1,1,1)); // top left
        sceneObjectsList.add(new Sphere(7, 2.5, 0, 1, 1,1,1,0.5)); // top
        sceneObjectsList.add(new Sphere(7, 2.5, -2.5, 1, 1,1,1,0)); // top right
        sceneObjectsList.add(new Sphere(7, 0, -2.5, 1, 1,1,1,0.75)); // right
        sceneObjectsList.add(new Sphere(7, -2.5, -2.5, 1, 1,1,1,0.75)); // bottom right
        sceneObjectsList.add(new Sphere(7, -2.5, 0, 1, 1,1,1,0.75)); // bottom
        sceneObjectsList.add(new Sphere(7, -2.5, 2.5, 1, 1,1,1,0.75)); // bottom left
        sceneObjectsList.add(new Sphere(7, 0, 2.5, 1, 1,1,1,0.75)); // left

        sceneObjectsList.add(new SphereLight(7, 0,0, 1,40,40,40,1,1,1,0.5));

        Camera cam = new Camera(0.1, RenderResolutionX, fov, aspectX, aspectY, 0,0,0, 1, 0, 0, 0, 1, 0);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        RenderSingleThreaded renderSingleThreaded = new RenderSingleThreaded();
        renderSingleThreaded.computePixels(sceneObjectsList, cam, raysPerPixel, bouncesPerRay, frameTime, ASCIIMode);
    }
}