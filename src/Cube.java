
public class Cube implements SceneObjects {

    private double minX, maxX;
    private double minY, maxY;
    private double minZ, maxZ;
    private double tminX, tminY, tminZ;
    private double tmaxX, tmaxY, tmaxZ;
    private double tNear = 0, tFar = 0;
    private static int numCubes = 0;
    private int cubeID = 0;
    private double normalx, normaly, normalz;
    private double luminance = 0;

    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public Cube(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.cubeID = numCubes;
        numCubes++;
    }

    public void computeMinMax(Ray ray)
    {

        // (cubecorner - ray origin) / ray direction
        // if the direction is negative they may need to be flipped

        double tmp;
        this.tminX = (minX - ray.getPosX()) / ray.getDirX();
        this.tmaxX = (maxX - ray.getPosX()) / ray.getDirX();
        if (tminX > tmaxX)
        {
            tmp = this.tmaxX;
            this.tmaxX = tminX;
            this.tminX = tmp;
        }

        this.tminY = (minY - ray.getPosY()) / ray.getDirY();
        this.tmaxY = (maxY - ray.getPosY()) / ray.getDirY();
        if (tminY > tmaxY)
        {
            tmp = this.tmaxY;
            this.tmaxY = tminY;
            this.tminY = tmp;
        }

        this.tminZ = (minZ - ray.getPosZ()) / ray.getDirZ();
        this.tmaxZ = (maxZ - ray.getPosZ()) / ray.getDirZ();
        if (tminZ > tmaxZ)
        {
            tmp = tmaxZ;
            this.tmaxZ = tminZ;
            this.tminZ = tmp;
        }
    }

    // initial check to see if the ray will or will not hit the cube (for performance)
    public boolean objectCulling(Ray ray)
    {
        computeMinMax(ray);

        this.tNear = Double.NEGATIVE_INFINITY;
        this.tFar = Double.POSITIVE_INFINITY;

        // find the biggest Min
        double[] minArray = {tminX, tminY, tminZ};
        for (double i : minArray) {
            if (i > tNear) {
                this.tNear = i;
            }
        }

        // find the smallest Max
        double[] maxArray = {tmaxX, tmaxY, tmaxZ};
        for (double i : maxArray) {
            if (i < tFar) {
                tFar = i;
            }
        }

        if (this.tNear <= this.tFar)
        {
            // the ray is on a path to intersect
            //System.out.println("Object culling: true");
            return true;
        }
        else {
            // the ray will not intersect
            //System.out.println("Object culling: false");
            return true;
        }
    }

    // check if the ray is intersecting the cube
    public boolean intersectionCheck(Ray ray)
    {
        computeMinMax(ray);

        tNear = Double.NEGATIVE_INFINITY;
        tFar = Double.POSITIVE_INFINITY;

        // find the biggest Min
        double[] minArray = {tminX, tminY, tminZ};
        for (double v : minArray) {
            if (v > tNear) {
                tNear = v;
            }
        }

        // find the smallest Max
        double[] maxArray = {tmaxX, tmaxY, tmaxZ};
        for (int i = 0; i < maxArray.length; i++)
        {
            if (maxArray[i] < tFar)
            {tFar = minArray[i];}
        }

        if (tNear <= tFar && tFar > 0)
        {
            // the ray is intersecting
            //System.out.println("Intersection check: true");
            return true;
        }
        else {
            // the ray will not intersect
            //System.out.println("Intersection check: false");
            return false;
        }
    }

    // calculate the normal of the sphere and a point
    public void calculateNormal (double posX, double posY, double posZ)
    {
        normalx = posX - this.minX;
        normaly = posY - this.minY;
        normalz = posZ - this.minZ;
        double magnitude = Math.sqrt((normalx*normalx) + (normaly*normaly) + (normalz * normalz));
        this.normalx = normalx / magnitude;
        this.normaly = normaly / magnitude;
        this.normalz = normalz / magnitude;
    }

    // get each the normalised normal
    public double getNormalX() {return this.normalx;}
    public double getNormalY() {return this.normaly;}
    public double getNormalZ() {return this.normalz;}


    // get sphere ID
    public int getObjectID()
    {
        return this.cubeID;
    }

    public double getPosX()
    {return this.minX;}
    public double getPosY()
    {return this.minY;}
    public double getPosZ()
    {return this.minZ;}


    public double getLuminance()
    {return this.luminance;}
}
