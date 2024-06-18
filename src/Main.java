import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<SceneObjects> sceneObjectsList = new ArrayList<>();

        sceneObjectsList.add(new AABCubeCenter(0,-1,0,15,2,10)); // floor
        sceneObjectsList.add(new AABCubeCenter(10,5,0,2,10,20)); // front wall
        sceneObjectsList.add(new SphereLight(-3,3,0,2,20)); // sphere behind camera

        Camera cam = new Camera(70, 0, 0.5, 0, 1, 0, 0, 0, 1, 0, 90, 4, 3);

        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();

        Render render = new Render();
        render.computePixels(sceneObjectsList, cam, 10000, 3);
    }
}