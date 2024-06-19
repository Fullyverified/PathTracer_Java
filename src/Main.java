import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new Sphere(7, 1.25, -1, 1,0.85));
        sceneObjectsList.add(new AABCubeCenter(7, -1.25, -1, 1,1,1, 0.85));

        sceneObjectsList.add(new SphereLight(3, 0,3, 1,40,1));

        sceneObjectsList.add(new AABCubeBounds(13, 14, -12, 12,-12,12,0.25));

        Camera cam = new Camera(0.5, 70, 55, 4, 3, 0,0,0, 1, 0, 0, 0, 1, 0);


        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjectsList, cam, 5000, 5);
    }
}