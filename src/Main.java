import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new Sphere(8, 6.5, 0, 1));

        sceneObjectsList.add(new Sphere(8, 4, -2.5, 1));
        sceneObjectsList.add(new Sphere(8, 4, 2.5, 1));

        sceneObjectsList.add(new Sphere(8, 6.5, -2.5, 1));
        sceneObjectsList.add(new Sphere(8, 6.5, 2.5, 1));

        sceneObjectsList.add(new SphereLight(8, 4,0, 1,500));

        Camera cam = new Camera(80, 0, 5, 0, 1, 0, 0, 0, 1, 0, 55, 16, 9);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjectsList, cam,1000,2);
    }
}