import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {

        List<SceneObjects> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(5, 1,-1, 1));
        sceneObjects.add(new Sphere(10, 0,-5, 1.25));
        //sceneObjects.add(new AABCube(8,-1,1,2,2,2));

        sceneObjects.add(new SphereLight(3, 0.5, 2, 0.5,300));

        Camera cam = new Camera(50, 0, 0, 0, 1, 0, 0, 0, 1, 0, 70, 1, 1);
        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjects, cam,10000,1);}
}







