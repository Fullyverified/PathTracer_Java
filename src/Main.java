import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {

        // create new sceneObjects ArrayList. initialise and store each object in it.
        List<Object> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(5, 0, 0, 1));

        Camera cam = new Camera(0, 0, 0, 1, 0, 0, 0, 1, 0,90, 16,9);
        cam.directionVector();
        cam.upVector();
        cam.rightVector();



        Ray ray1 = new Ray(cam.getPosX(), cam.getPosY(), cam.getPosZ(), cam.getDirX(), cam.getDirY(), cam.getDirZ());



        // iterate through each sceneObject
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
                        if (((Sphere) sceneObject).intersectionCheck(ray1))
                        {
                            ray1.setHitPointX(ray1.getRayPointX());
                            ray1.setHitPointY(ray1.getRayPointY());
                            ray1.setHitPointZ(ray1.getRayPointZ());
                        }
                        i = i + 0.1;
                    }
                }
            }
        }
    }


}






