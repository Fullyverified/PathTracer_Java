import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(5, 1.5,-1.5, 1));
        //sceneObjects.add(new Sphere(5, -1.5,-1.5, 1));

        //sceneObjects.add(new AABCube(8,-1,1,2,2,2));

        sceneObjects.add(new SphereLight(5, 0, 1.5, 1,20));

        Camera cam = new Camera(30, -1, 0, 0, 1, 0, 0, 0, 1, 0, 50, 1, 1);
        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjects, cam,2000,1);}
}







