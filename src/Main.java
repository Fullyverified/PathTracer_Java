import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjects = new ArrayList<>();
        //sceneObjects.add(new Sphere(7, 0, 0, 1));
        //sceneObjects.add(new Sphere(7, 0,-1.5, 1));
        sceneObjects.add(new SphereLight(6, -2,1.5, 1.25,20));


        sceneObjects.add(new Sphere(6, 0, -1.5, 1.25));

        Camera cam = new Camera(130, -1, 0, 0, 1, 0, 0, 0, 1, 0, 50, 21, 9);
        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjects, cam,300,1);
    }
}







