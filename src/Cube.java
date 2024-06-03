
public class Cube implements SceneObjects {

    private double minX, maxX;
    private double minY, maxY;
    private double minZ, maxZ;
    private double tminX, tminY, tminZ;
    private double tmaxX, tmaxY, tmaxZ;
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
        double tmp;
        tminX = (minX - ray.getPosX()) / ray.getDirX();
        tmaxX = (maxX - ray.getPosX()) / ray.getDirX();
        if (tminX > tmaxX)
        {
            tmp = tmaxX;
            tmaxX = tminX;
            tminX = tmp;
        }

        tminY = (minY - ray.getPosY()) / ray.getDirY();
        tmaxY = (maxY - ray.getPosX()) / ray.getDirY();
        if (tminY > tmaxY)
        {
            tmp = tmaxY;
            tmaxY = tminY;
            tminY = tmp;
        }

        tminZ = (minZ - ray.getPosZ()) / ray.getDirZ();
        tmaxZ = (maxZ - ray.getPosX()) / ray.getDirZ();
        if (tminX > tmaxX)
        {
            tmp = tmaxX;
            tmaxX = tminX;
            tminX = tmp;
        }
    }

    // initial check to see if the ray will or will not hit the cube (for performance)
    public boolean intersectionDiscard(Ray ray)
    {
        computeMinMax(ray);



        return false;
    }

    // check if the ray is intersecting the cube
    public boolean intersectionCheck(Ray ray)
    {

        return false;
    }

    // calculate the normal of the sphere and a point
    public void surfaceToNormal (double posX, double posY, double posZ)
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
