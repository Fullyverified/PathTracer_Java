import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;
    public int width, height;


    public static void main(String[] args) {

        // create new sceneObjects ArrayList. initialise and store each object in it.
        List<Object> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(5, 0, 0, 1));



        Camera cam = new Camera(0, 0, 0, 1, 0, 0, 0, 1, 0,90, 16,9,30);
        cam.directionVector();
        cam.upVector();
        cam.rightVector();

        Ray[][] rayIndex = new Ray[(int) cam.getResX()][(int) cam.getResY()];



        // Ray ray1 = new Ray(cam.getPosX(), cam.getPosY(), cam.getPosZ(), cam.getDirX(), cam.getDirY(), cam.getDirZ());



        // iterate through each sceneObject

        for (int i = 0; i < cam.getResX(); i++)
        {
            for (int j = 0; j < cam.getResY() - 1; j++)
            {
                rayIndex[i][j] = new Ray();
                rayIndex[i][j].setPixelX(i);
                rayIndex[i][j].setPixelX(j);
                rayIndex[i][j].setPixelIndeX(i / (int) cam.getResX() * 2 - 1);
                rayIndex[i][j].setPixelIndeY(1 - (i / (int) cam.getResY()) * 2);

                rayIndex[i][j].setPixelPosX(rayIndex[i][j].getPixelIndexX() * cam.getCamHeight() / 2);
                rayIndex[i][j].setPixelPosY(rayIndex[i][j].getPixelIndexY() * cam.getCamHeight() / 2);

                rayIndex[i][j].setDirX(cam.getNormDirX() + cam.getNormRightX() * rayIndex[i][j].getPixelPosX() + cam.getNormUpX() * rayIndex[i][j].getPixelPosY());
                rayIndex[i][j].setDirY(cam.getNormDirY() + cam.getNormRightY() * rayIndex[i][j].getPixelPosX() + cam.getNormUpY() * rayIndex[i][j].getPixelPosY());
                rayIndex[i][j].setDirZ(cam.getNormDirZ() + cam.getNormRightZ() * rayIndex[i][j].getPixelPosX() + cam.getNormUpZ() * rayIndex[i][j].getPixelPosY());

                for (Object sceneObject : sceneObjects)
                {
                    if (sceneObject instanceof Sphere)
                    {
                        if (((Sphere) sceneObject).intersectionDiscard(rayIndex[i][j]))
                        {
                            double r = 0;
                            while (!((Sphere) sceneObject).intersectionCheck(rayIndex[i][j]) && r <= 100)
                            {
                                ((Sphere) sceneObject).intersectionCheck(rayIndex[i][j]);
                                rayIndex[i][j].rayMarch(r);
                                if (((Sphere) sceneObject).intersectionCheck(rayIndex[i][j]))
                                {

                                    rayIndex[i][j].setHitPointX(rayIndex[i][j].getRayPointX());
                                    rayIndex[i][j].setHitPointY(rayIndex[i][j].getRayPointY());
                                    rayIndex[i][j].setHitPointZ(rayIndex[i][j].getRayPointZ());
                                    rayIndex[i][j].setHit(1);
                                }
                                r = r + 0.1;
                            }
                        }
                    }
                }



            }
        }

    }


}






