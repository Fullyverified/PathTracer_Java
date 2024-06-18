import java.util.Random;

public class AABCubeBounds implements SceneObjects {

    private double minX, maxX;
    private double minY, maxY;
    private double minZ, maxZ;
    private double centreX, centreY, centreZ;
    private double xlength, ylength, zlength;
    private double tminX, tminY, tminZ;
    private double tmaxX, tmaxY, tmaxZ;
    private double tNear = 0, tFar = 0;
    private static int numCubes = 200;
    private int cubeID = 0;
    private double normalx, normaly, normalz;
    private double luminance = 0;

    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public AABCubeBounds(double centreX, double centreY, double centreZ, double xLength, double yLength, double zLength) {
        this.centreX = centreX;
        this.centreY = centreY;
        this.centreZ = centreZ;

        this.xlength = xLength;
        this.ylength = yLength;
        this.zlength = zLength;

        this.minX = centreX - xLength / 2;
        this.maxX = centreX + xLength / 2;
        System.out.println("minx: " + minX + " maxx: " + maxX);
        this.minY = centreY - yLength / 2;
        this.maxY = centreY + yLength / 2;
        System.out.println("miny: " + minY + " maxy: " + maxY);
        this.minZ = centreZ - zLength / 2;
        this.maxZ = centreZ + zLength / 2;
        System.out.println("minz: " + minZ + " maxZ: " + maxZ);

        this.cubeID = numCubes;
        numCubes++;
    }

    /*private double safeDivide(double numerator, double denominator) {
        if (Math.abs(denominator) < 1e-8) { // use a small epsilon to avoid instability
            return denominator > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        return numerator / denominator;
    }*/

    public void computeMinMax(Ray ray) {

        // (cubecorner - ray origin) / ray direction
        // if the direction is negative they may need to be flipped

        double tmp;
        if (ray.getDirX() == 0) {
            if (ray.getPosX() < minX || ray.getPosX() > maxX) {
                tminX = Double.POSITIVE_INFINITY; // No intersection possible on this axis
                tmaxX = Double.NEGATIVE_INFINITY;
            } else {
                tminX = Double.NEGATIVE_INFINITY; // Always intersecting on this axis
                tmaxX = Double.POSITIVE_INFINITY;
            }
        } else {
            tminX = (minX - ray.getPosX()) / ray.getDirX();
            tmaxX = (maxX - ray.getPosX()) / ray.getDirX();
            if (tminX > tmaxX) {
                tmp = tmaxX;
                tmaxX = tminX;
                tminX = tmp;
            }
        }

        if (ray.getDirY() == 0) {
            if (ray.getPosY() < minY || ray.getPosY() > maxY) {
                tminY = Double.POSITIVE_INFINITY; // No intersection possible on this axis
                tmaxY = Double.NEGATIVE_INFINITY;
            } else {
                tminY = Double.NEGATIVE_INFINITY; // Always intersecting on this axis
                tmaxY = Double.POSITIVE_INFINITY;
            }
        } else {
            tminY = (minY - ray.getPosY()) / ray.getDirY();
            tmaxY = (maxY - ray.getPosY()) / ray.getDirY();
            if (tminY > tmaxY) {
                tmp = tmaxY;
                tmaxY = tminY;
                tminY = tmp;
            }
        }

        if (ray.getDirZ() == 0) {
            if (ray.getPosZ() < minZ || ray.getPosZ() > maxZ) {
                tminZ = Double.POSITIVE_INFINITY; // No intersection possible on this axis
                tmaxZ = Double.NEGATIVE_INFINITY;
            } else {
                tminZ = Double.NEGATIVE_INFINITY; // Always intersecting on this axis
                tmaxZ = Double.POSITIVE_INFINITY;
            }
        } else {
            tminZ = (minZ - ray.getPosZ()) / ray.getDirZ();
            tmaxZ = (maxZ - ray.getPosZ()) / ray.getDirZ();
            if (tminZ > tmaxZ) {
                tmp = tmaxZ;
                tmaxZ = tminZ;
                tminZ = tmp;
            }
        }
    }

    // initial check to see if the ray will or will not hit the cube (for performance)
    public boolean objectCulling(Ray ray) {
        computeMinMax(ray);
        tNear = Math.max(Math.max(tminX, tminY), tminZ);
        tFar = Math.min(Math.min(tmaxX, tmaxY), tmaxZ);
        if (tNear > tFar || tFar < 0) {
            return false; // no intersection
        }
        return true; // intersection
    }

    // check if the ray is intersecting the cube
    public boolean intersectionCheck(Ray ray) {

        if (minX <= ray.getPosX() && maxX >= ray.getPosX() && minY <= ray.getPosY() && maxY >= ray.getPosY() && minZ <= ray.getPosZ() && maxZ >= ray.getPosZ()) {
            return true;
        } else {
            return false;
        }

    }

    // calculate the normal of the sphere and a point
    public void calculateNormal(Ray nthRay) {
        double epsilon = 0.1;
        // x
        if ((Math.abs(nthRay.getPosX() - minX)) < epsilon) {
            setNormal(-1, 0, 0);
        } else if ((Math.abs(nthRay.getPosX() - maxX)) < epsilon) {
            setNormal(1, 0, 0);
        }
        // y
        else if ((Math.abs(nthRay.getPosY() - minY)) < epsilon) {
            setNormal(0, -1, 0);
        } else if ((Math.abs(nthRay.getPosY() - maxY)) < epsilon) {
            setNormal(0, 1, 0);
        }
        // z
        else if ((Math.abs(nthRay.getPosZ() - minZ)) < epsilon) {
            setNormal(0, 0, -1);
        } else if ((Math.abs(nthRay.getPosZ() - maxZ)) < epsilon) {
            setNormal(0, 0, 1);
        }
    }

    public void randomDirection(Ray nthRay) {
        double dotproduct = -1;
        Random random = new Random();

        nthRay.marchRay(0);
        calculateNormal(nthRay);

        while (dotproduct <= 0){
            // Generate a random direction uniformly on a sphere
            double theta = Math.acos(2 * random.nextDouble() - 1); // polar angle
            double phi = 2 * Math.PI * random.nextDouble(); // azimuthal angle

            nthRay.setDirX(Math.sin(theta) * Math.cos(phi));
            nthRay.setDirY(Math.sin(theta) * Math.sin(phi));
            nthRay.setDirZ(Math.cos(theta));

            // Normalize the random direction
            nthRay.updateNormalisation();

            // Calculate the dot product
            dotproduct = this.normalx * nthRay.getDirX() + this.normaly * nthRay.getDirY() + this.normalz * nthRay.getDirZ();
        }
        nthRay.updateOrigin(0.15); // march the ray a tiny amount to move it off the sphere
    }

    // R = I - 2 * (I dot N) * N
    public void reflectionBounce(Ray nthRay) {
        calculateNormal(nthRay);
        double dotproduct = normalx * nthRay.getDirX() + normaly * nthRay.getDirY() + normalz * nthRay.getDirZ();
        double reflectionX = nthRay.getDirX() - 2 * (dotproduct) * normalx;
        double reflectionY = nthRay.getDirY() - 2 * (dotproduct) * normaly;
        double reflectionZ = nthRay.getDirZ() - 2 * (dotproduct) * normalz;

        nthRay.setDirection(reflectionX, reflectionY, reflectionZ);
        nthRay.updateNormalisation();
        nthRay.updateOrigin(0.15); // march the ray a tiny amount to move it off the sphere
    }

    // get each the normalised normal
    public double getNormalX() {
        return this.normalx;
    }

    public double getNormalY() {
        return this.normaly;
    }

    public double getNormalZ() {
        return this.normalz;
    }

    public void setNormal(double x, double y, double z) {
        this.normalx = x;
        this.normaly = y;
        this.normalz = z;
    }


    // get sphere ID
    public int getObjectID() {
        return this.cubeID;
    }

    public double getPosX() {
        return this.minX;
    }

    public double getPosY() {
        return this.minY;
    }

    public double getPosZ() {
        return this.minZ;
    }


    public double getLuminance() {
        return this.luminance;
    }

}
