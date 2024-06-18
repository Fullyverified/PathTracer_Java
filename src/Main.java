import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new Sphere(7, 2.5, 0, 1));
        sceneObjectsList.add(new Sphere(7, -2.5, 0, 1));

        sceneObjectsList.add(new Sphere(7, 0, -2.5, 1));
        sceneObjectsList.add(new Sphere(7, 0, 2.5, 1));

        sceneObjectsList.add(new Sphere(7, 2.5, -2.5, 1));
        sceneObjectsList.add(new Sphere(7, 2.5, 2.5, 1));

        sceneObjectsList.add(new Sphere(7, -2.5, -2.5, 1));
        sceneObjectsList.add(new Sphere(7, -2.5, 2.5, 1));

        sceneObjectsList.add(new SphereLight(7, 0,0, 1,20));

        Camera cam = new Camera(65, 0, 0, 0, 1, 0, 0, 0, 1, 0, 50, 4, 4);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjectsList, cam, 2500, 5);
    }
}