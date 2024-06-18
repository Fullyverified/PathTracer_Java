import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new Sphere(7, 1.25, 0, 1));
        sceneObjectsList.add(new AABCubeCenter(7, -1.25, 0, 1,1,1));

        sceneObjectsList.add(new SphereLight(3, 0,2, 1,40));

        sceneObjectsList.add(new AABCubeBounds(13, 14, -12, 12,-12,12));

        Camera cam = new Camera(60, 0, 0, -1.75, 0.80, 0, 0.2, 0, 1, 0, 50, 4, 4);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjectsList, cam, 10000, 2);
    }
}