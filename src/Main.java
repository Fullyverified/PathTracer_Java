import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(6, 0, 0, 1));
        sceneObjects.add(new Sphere(12, -1,-5, 1.25));
        sceneObjects.add(new PointLight(6, 0.5, 5, 1, 300));

        Camera cam = new Camera(0, 0, 0, 1, 0, 0, 0, 1, 0, 70, 4, 3, 60);
        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjects, cam,20000,1);
    }
}







