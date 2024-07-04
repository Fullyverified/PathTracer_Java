import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new Sphere(7, 1.25, 0, 1,0.85));
        sceneObjectsList.add(new AABCubeCenter(7, -1.25, 0, 1,1,1, 0.85));

        sceneObjectsList.add(new SphereLight(3, 0,2, 1,40,1));

        sceneObjectsList.add(new AABCubeBounds(13, 14, -12, 12,-12,12,0.25));

        int resolutionX = 350;
        int aspectX = 4;
        int aspectY = 3;
        int fov = 60;
        Camera cam = new Camera(1, resolutionX, fov, aspectX, aspectY, 0,0,-3.5, 0.75, 0, 0.25, 0, 1, 0);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        RenderSingleThreaded renderSingleThreaded = new RenderSingleThreaded();
        renderSingleThreaded.computePixels(sceneObjectsList, cam, 5000, 4,250);

        //RenderMultiThreaded renderMultiThreaded = new RenderMultiThreaded();
        //renderMultiThreaded.computePixels(sceneObjectsList, cam, 6000, 4);
    }
}