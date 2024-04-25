public class Main {

    public static double tscalar = 0;

    public static void main(String[] args)
    {
        Camera cam = new Camera(1,0,0,1,0,0);
        Ray ray1 = new Ray(cam.getPosX(), cam.getPosY(), cam.getPosZ(), cam.getDirX(), cam.getDirY(), cam.getDirZ());
        Sphere sphere1 = new Sphere(3,0,0,1);

        if (sphere1.intersectionDiscard(ray1))
        {
            for (double i = 0; i <= 100; i++)
            {
                sphere1.intersectionCheck(ray1);
                ray1.rayMarch(i);
            }
        }


    }

}



