import java.util.ArrayList;
import java.util.List;

public class Main {

    public static int RenderResolutionX = 300;
    public static int aspectX = 4;
    public static int aspectY = 3;
    public static int fov = 90;
    public static int frameTime = 50; // Milliseconds
    public static int raysPerPixel = 200;
    public static int bouncesPerRay = 20;
    public static boolean ASCIIMode = false;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new AABCubeCenter(10,-3, 0,14,1,7,1,1,1, 0.5)); // floor
        sceneObjectsList.add(new AABCubeCenter(10,3,0,14,1,7,1,1,1, 0.5)); // roof

        sceneObjectsList.add(new AABCubeCenter(8,0,0,1,6,7,1,1,1,0.5)); // back wall

        //sceneObjectsList.add(new AABCubeCenter(9,3,3,14,12,1,1,1,1,0.5)); // left wall
        //sceneObjectsList.add(new AABCubeCenter(9,3,-3,14,12,1,1,1,1,0.5)); // right wall

        sceneObjectsList.add(new SphereLight(-2,0,0,1,40,1,0.5)); // sphere behind camera

        Camera cam = new Camera(1, RenderResolutionX, fov, aspectX, aspectY, 0,0,0, 1, 0, 0, 0, 1, 0);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        RenderSingleThreaded renderSingleThreaded = new RenderSingleThreaded();
        renderSingleThreaded.computePixels(sceneObjectsList, cam, raysPerPixel, bouncesPerRay, frameTime, ASCIIMode);
    }
}