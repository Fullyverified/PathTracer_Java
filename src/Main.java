import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {
        List<Object> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(5, 0, 0, 1));

        Camera cam = new Camera(1, 0, 0, 1, 0, 0);
        Ray ray1 = new Ray(cam.getPosX(), cam.getPosY(), cam.getPosZ(), cam.getDirX(), cam.getDirY(), cam.getDirZ());

        for (Object sceneObject : sceneObjects)
        {
            if (sceneObject instanceof Sphere)
            {
                if (((Sphere) sceneObject).intersectionDiscard(ray1))
                {
                    double i = 0;
                    while (!((Sphere) sceneObject).intersectionCheck(ray1) && i <= 100)
                    {
                        ((Sphere) sceneObject).intersectionCheck(ray1);
                        ray1.rayMarch(i);
                        i = i + 0.1;
                    }
                }
            }
        }
    }


}






