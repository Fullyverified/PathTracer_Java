import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;
    public int width, height;


    public static void main(String[] args) {
        // create new sceneObjects ArrayList. initialise and store each object in it.
        List<Object> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(10, 0, 0, 1));

        // create camera object and initialise it
        Camera cam = new Camera(1, 0, 0, 1, 0, 0, 0, 1, 0,90, 4,3,60);
        // each cam. method calculates the various properties of the camera
        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();
        // create a 2D array of rays with the size of resolution of the camera
        Ray[][] rayIndex = new Ray[(int) cam.getResX()][(int) cam.getResY()];

        // iterate through each ray, left to right, top to bottom, for each scene object.
        for (int j = 0; j < cam.getResY(); j++)
        {
            //System.out.println("i: " + i);
            for (int i = 0; i < cam.getResX(); i++)
            {
                //System.out.println("i: " + i);
                //System.out.println("j: " + j);
                rayIndex[i][j] = new Ray(cam.getPosX(), cam.getPosY(), cam.getPosZ());

                // update the rays index to the current pixel
                rayIndex[i][j].setPixelX(i);
                rayIndex[i][j].setPixelY(j);

                // calculate pixel position on the plane
                rayIndex[i][j].setPixelIndexX((((i + 0.5) / cam.getResX()) * 2) - 1);
                rayIndex[i][j].setPixelIndexY(1 - (((j + 0.5) / cam.getResY()) * 2));

                // calculate pixel position in the scene
                rayIndex[i][j].setPixelPosX(rayIndex[i][j].getPixelIndexX() * cam.getCamWidth() / 2);
                rayIndex[i][j].setPixelPosY(rayIndex[i][j].getPixelIndexY() * cam.getCamHeight() / 2);

                // set the ray direction
                // D = normCamD + rightvector * ScenePosX + upvector * ScenePosY
                rayIndex[i][j].setDirX(cam.getNormDirX() + cam.getNormRightX() * rayIndex[i][j].getPixelPosX() + cam.getNormUpX() * rayIndex[i][j].getPixelPosY());
                rayIndex[i][j].setDirY(cam.getNormDirY() + cam.getNormRightY() * rayIndex[i][j].getPixelPosX() + cam.getNormUpY() * rayIndex[i][j].getPixelPosY());
                rayIndex[i][j].setDirZ(cam.getNormDirZ() + cam.getNormRightZ() * rayIndex[i][j].getPixelPosX() + cam.getNormUpZ() * rayIndex[i][j].getPixelPosY());
                // update vector normalisation
                rayIndex[i][j].updateNormalisation();

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
                                else {
                                    rayIndex[i][j].setHit(0);}
                                r = r + 0.1;
                            }
                        }
                    }
                }
            }
        }


        for (int j = 0; j < cam.getResY(); j++)
        {
            for (int i = 0; i < cam.getResX(); i++)
            {
                if (rayIndex[i][j].getHit() == 1)
                {
                    System.out.print("@");
                }
                else if (rayIndex[i][j].getHit() == 0)
                {
                    System.out.print(" ");
                }
            }
            System.out.println("|");
        }


    }


}






