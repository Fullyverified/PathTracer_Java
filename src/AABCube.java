
public class AABCube implements SceneObjects {

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
    public AABCube(double centreX, double centreY, double centreZ, double xLength, double yLength, double zLength) {
        this.centreX = centreX;
        this.centreY = centreY;
        this.centreZ = centreZ;

        this.xlength = xLength;
        this.ylength = yLength;
        this.zlength = zLength;

        this.minX = centreX - xLength / 2;
        this.maxX = centreX + xLength / 2;
        this.minY = centreY - yLength / 2;
        this.maxY = centreY + yLength / 2;
        this.minZ = centreZ - zLength / 2;
        this.maxZ = centreZ + zLength / 2;

        this.cubeID = numCubes;
        numCubes++;
    }

    private double safeDivide(double numerator, double denominator) {
        if (Math.abs(denominator) < 1e-8) { // use a small epsilon to avoid instability
            return denominator > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        return numerator / denominator;
    }

    public void computeMinMax(Ray ray) {

        // (cubecorner - ray origin) / ray direction
        // if the direction is negative they may need to be flipped

        /*this.tminX = Math.min(safeDivide(minX - ray.getPosX(), ray.getDirX()), safeDivide(maxX - ray.getPosX(), ray.getDirX()));
        this.tmaxX = Math.max(safeDivide(minX - ray.getPosX(), ray.getDirX()), safeDivide(maxX - ray.getPosX(), ray.getDirX()));

        this.tminY = Math.min(safeDivide(minY - ray.getPosY(), ray.getDirY()), safeDivide(maxY - ray.getPosY(), ray.getDirY()));
        this.tmaxY = Math.max(safeDivide(minY - ray.getPosY(), ray.getDirY()), safeDivide(maxY - ray.getPosY(), ray.getDirY()));

        this.tminZ = Math.min(safeDivide(minZ - ray.getPosZ(), ray.getDirZ()), safeDivide(maxZ - ray.getPosZ(), ray.getDirZ()));
        this.tmaxZ = Math.max(safeDivide(minZ - ray.getPosZ(), ray.getDirZ()), safeDivide(maxZ - ray.getPosZ(), ray.getDirZ()));*/

        double tmp;
        this.tminX = (minX - ray.getPosX()) / ray.getDirX();
        this.tmaxX = (maxX - ray.getPosX()) / ray.getDirX();
        if (tminX > tmaxX) {
            tmp = this.tmaxX;
            this.tmaxX = tminX;
            this.tminX = tmp;
        }

        this.tminY = (minY - ray.getPosY()) / ray.getDirY();
        this.tmaxY = (maxY - ray.getPosY()) / ray.getDirY();
        if (tminY > tmaxY) {
            tmp = this.tmaxY;
            this.tmaxY = tminY;
            this.tminY = tmp;
        }

        this.tminZ = (minZ - ray.getPosZ()) / ray.getDirZ();
        this.tmaxZ = (maxZ - ray.getPosZ()) / ray.getDirZ();
        if (tminZ > tmaxZ) {
            tmp = tmaxZ;
            this.tmaxZ = tminZ;
            this.tminZ = tmp;
        }
    }

    // initial check to see if the ray will or will not hit the cube (for performance)
    public boolean objectCulling(Ray ray) {
        computeMinMax(ray);

        this.tNear = Double.NEGATIVE_INFINITY;
        this.tFar = Double.POSITIVE_INFINITY;

        // find the biggest Min
        double[] minArray = {tminX, tminY, tminZ};
        for (int i = 0; i < minArray.length; i++) {
            if (minArray[i] > tNear) {
                this.tNear = minArray[i];
            }
        }

        // find the smallest Max
        double[] maxArray = {tmaxX, tmaxY, tmaxZ};
        for (int i = 0; i < maxArray.length; i++) {
            if (maxArray[i] < tFar) {
                this.tFar = maxArray[i];
            }
        }

        if (this.tNear <= this.tFar) {
            // the ray is on a path to intersect
            //System.out.println("Object culling: true");
            return true;
        } else {
            // the ray will not intersect
            //System.out.println("Object culling: false");
            return false;
        }
    }

    // check if the ray is intersecting the cube
    public boolean intersectionCheck(Ray ray) {
        computeMinMax(ray);

        tNear = Double.NEGATIVE_INFINITY;
        tFar = Double.POSITIVE_INFINITY;

        // find the biggest Min
        double[] minArray = {tminX, tminY, tminZ};
        for (int i = 0; i < minArray.length; i++) {
            if (minArray[i] > tNear) {
                this.tNear = minArray[i];
            }
        }
        //System.out.println("tnear: " + this.tNear);

        // this fixes many problems...
        tNear = tNear * -1;

        // find the smallest Max
        double[] maxArray = {tmaxX, tmaxY, tmaxZ};
        for (int i = 0; i < maxArray.length; i++) {
            if (maxArray[i] < tFar) {
                this.tFar = maxArray[i];
            }
        }

        //System.out.println("tfar: " + this.tFar);

        if (tNear <= tFar && tNear > 0 &&tFar > 0) {
            // the ray is intersecting
            //System.out.println("Intersection check: true");
            return true;
        } else {
            // the ray will not intersect
            //System.out.println("Intersection check: false");
            return false;
        }
    }

    // calculate the normal of the sphere and a point
    public void calculateNormal(double posX, double posY, double posZ) {
        double epsilon = 0.1;
        // x
        if ((Math.abs(posX - minX)) < epsilon) {
            setNormal(1, 0, 0);
        }
        else if ((Math.abs(posX - maxX)) < epsilon) {
            setNormal(-1, 0, 0);
        }
        // y
        else if ((Math.abs(posY - minY)) < epsilon) {
            setNormal(0, 1, 0);
        }
        else if ((Math.abs(posY - maxY)) < epsilon) {
            setNormal(0, -1, 0);
        }
        // z
        else if ((Math.abs(posZ - minZ)) < epsilon) {
            setNormal(0, 0, 1);
        }
        else if ((Math.abs(posZ - maxZ)) < epsilon) {
            setNormal(0, 0, -1);
        }
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
