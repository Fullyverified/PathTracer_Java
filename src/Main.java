import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();



        sceneObjectsList.add(new Sphere(9, 2.25, 0, 1));
        sceneObjectsList.add(new Sphere(9, -2.25, 0, 1));

        sceneObjectsList.add(new Sphere(9, 0, -2.25, 1));
        sceneObjectsList.add(new Sphere(9, 0, 2.25, 1));

        sceneObjectsList.add(new Sphere(9, 2.25, -2.25, 1));
        sceneObjectsList.add(new Sphere(9, 2.25, 2.25, 1));

        sceneObjectsList.add(new Sphere(9, -2.25, -2.25, 1));
        sceneObjectsList.add(new Sphere(9, -2.25, 2.25, 1));

        sceneObjectsList.add(new SphereLight(9, 0,0, 1,500));

        Camera cam = new Camera(100, -1, 0, 0, 1, 0, 0, 0, 1, 0, 50, 4, 3);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjectsList, cam,2000,1);
    }
}