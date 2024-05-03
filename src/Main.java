import java.util.ArrayList;
import java.util.List;

public class Main {

    public static double tscalar = 0;

    public static void main(String[] args) {
        // create new sceneObjects ArrayList. initialise and store each object in it.
        List<Object> sceneObjects = new ArrayList<>();
        sceneObjects.add(new Sphere(6, 0, 0, 1));
        sceneObjects.add(new Sphere(12, 2, -3, 1.5));

        // create camera object and initialise it
        Camera cam = new Camera(0, 0, 0, 1, 0, 0, 0, 1, 0,70, 4,3,80);
        // each cam. method calculates the various properties of the camera
        cam.directionVector();
        cam.upVector();
        cam.rightVector();
        cam.imagePlane();
        // create a 2D array of rays with the size of resolution of the camera
        Ray[][] primaryRay = new Ray[(int) cam.getResX()][(int) cam.getResY()];
        // 2d array of rays for secondary bounces
        Ray[][] secondRay = new Ray[(int) cam.getResX()][(int) cam.getResY()];

        // iterate through primary each ray, left to right, top to bottom, for each scene object.
        for (int j = 0; j < cam.getResY(); j++)
        {
            //System.out.println("i: " + i);
            for (int i = 0; i < cam.getResX(); i++)
            {
                //System.out.println("i: " + i);
                //System.out.println("j: " + j);
                primaryRay[i][j] = new Ray(cam.getPosX(), cam.getPosY(), cam.getPosZ());

                // update the rays index to the current pixel
                primaryRay[i][j].setPixelX(i);
                primaryRay[i][j].setPixelY(j);

                // calculate pixel position on the plane
                primaryRay[i][j].setPixelIndexX((((i + 0.5) / cam.getResX()) * 2) - 1);
                primaryRay[i][j].setPixelIndexY(1 - (((j + 0.5) / cam.getResY()) * 2));

                // calculate pixel position in the scene
                primaryRay[i][j].setPixelPosX(primaryRay[i][j].getPixelIndexX() * cam.getCamWidth() / 2);
                primaryRay[i][j].setPixelPosY(primaryRay[i][j].getPixelIndexY() * cam.getCamHeight() / 2);

                // set the primary ray direction
                // D = normCamD + rightvector * ScenePosX + upvector * ScenePosY
                primaryRay[i][j].setDirX(cam.getNormDirX() + cam.getNormRightX() * primaryRay[i][j].getPixelPosX() + cam.getNormUpX() * primaryRay[i][j].getPixelPosY());
                primaryRay[i][j].setDirY(cam.getNormDirY() + cam.getNormRightY() * primaryRay[i][j].getPixelPosX() + cam.getNormUpY() * primaryRay[i][j].getPixelPosY());
                primaryRay[i][j].setDirZ(cam.getNormDirZ() + cam.getNormRightZ() * primaryRay[i][j].getPixelPosX() + cam.getNormUpZ() * primaryRay[i][j].getPixelPosY());
                // update vector normalisation
                primaryRay[i][j].updateNormalisation();

                // for each object that is in the sceneObjects collection
                for (Object sceneObject : sceneObjects)
                {
                    // check if that object is a sphere
                    if (sceneObject instanceof Sphere)
                    {
                        // check the discriminant of the ray for the sphere
                        if (((Sphere) sceneObject).intersectionDiscard(primaryRay[i][j]))
                        {
                            // create local variable r (the rays step)
                            double r = 0;
                            // while the ray is not intersecting the sphere and the ray has not marched 100 units
                            while (!((Sphere) sceneObject).intersectionCheck(primaryRay[i][j]) && r <= 100)
                            {
                                // march the ray
                                primaryRay[i][j].rayMarch(r);
                                // check if the ray intersects the sphere
                                if (((Sphere) sceneObject).intersectionCheck(primaryRay[i][j]))
                                {
                                    // get the position of the intersection
                                    //  set ray hit to 1
                                    primaryRay[i][j].setHitPointX(primaryRay[i][j].getRayPointX());
                                    primaryRay[i][j].setHitPointY(primaryRay[i][j].getRayPointY());
                                    primaryRay[i][j].setHitPointZ(primaryRay[i][j].getRayPointZ());
                                    primaryRay[i][j].setHit(1);
                                }
                                // secondary bounces
                                else if (primaryRay[i][j].getHit() == 1)
                                {
                                    // secondary bounces
                                }
                                // if hit = 0, march the ray continue the loop
                                else {
                                    primaryRay[i][j].setHit(0);}
                                r = r + 0.01;
                            }
                        }
                    }
                }
            }
        }

        // iterate through each rays hit value and print the output
        for (int j = 0; j < cam.getResY(); j++)
        {
            for (int i = 0; i < cam.getResX(); i++)
            {
                if (primaryRay[i][j].getHit() == 1)
                {
                    System.out.print("###");
                }
                else if (primaryRay[i][j].getHit() == 0)
                {
                    System.out.print("   ");
                }
            }
            System.out.println("|");
        }


    }


}






