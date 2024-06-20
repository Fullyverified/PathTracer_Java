import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new Sphere(7, 1.25, -2, 1,0.85));
        sceneObjectsList.add(new AABCubeCenter(7, -1.25, -1,1,1,1, 0.85));

        sceneObjectsList.add(new SphereLight(3, 0,2, 1,40,1)); // light

        sceneObjectsList.add(new AABCubeBounds(13, 14, -12, 12,-12,12,0.25)); // wall

        Camera cam = new Camera(1, 120, 55, 4, 3, 0,0,-3.5, 0.85, 0, 0.15, 0, 1, 0);


        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        RenderSinglethreaded render = new RenderSinglethreaded();
        render.computePixels(sceneObjectsList, cam, 10000, 5);
    }
}