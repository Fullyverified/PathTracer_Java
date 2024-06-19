
public class Plane implements SceneObjects{

    private double normalx, normaly, normalz;
    double normalMagnitude;
    private double pointx, pointy, pointz;
    private static int numPLanes = 0;
    private int planeID = 300;
    private double luminance = 1000;
    double reflectivity = 1;

    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public Plane(double normalx, double normaly, double normalz, double pointx, double pointy, double pointz, double reflectivity) {
        this.normalx = normalx;
        this.normaly = normaly;
        this.normalz = normalz;
        this.normalMagnitude = Math.sqrt(this.normalx*this.normalx + this.normaly*this.normaly + this.normalz*this.normalz);
        this.normalx = (this.normalx / this.normalMagnitude);
        this.normaly = (this.normaly / this.normalMagnitude);
        this.normalz = (this.normalz / this.normalMagnitude);
        this.pointx = pointx;
        this.pointy = pointy;
        this.pointz = pointz;
        this.reflectivity = reflectivity;
    }

    // initial check to see if the ray will or will not hit an object (for performance)
    public boolean objectCulling(Ray ray) {

        return true;
    }

    // check the distance between the current ray and the sphere
    // distance = sqrt(rayposxyz^2 - spherecenterxyz^2))
    public boolean intersectionCheck(Ray nthRay) {
        double dotproductDenomiator = this.normalx * nthRay.getDirX() + this.normaly * nthRay.getDirY() + this.normalz * nthRay.getDirZ();

        double valuex = pointx - nthRay.getPosX();
        double valuey = pointy - nthRay.getPosY();
        double valuez = pointz - nthRay.getPosZ();

        double dotproductTop = valuex * normalx + valuey * normaly + valuez * normaly;

        if (dotproductDenomiator > 0)
        {
            double intersect = dotproductTop / dotproductDenomiator;
            if (intersect > 0) {
            return true;
            }
        }

        return false;
    }

    // calculate the normal of the plane and a point
    public void calculateNormal(double posX, double posY, double posZ) {
        /*normalx = posX - this.centerx;
        normaly = posY - this.centerx;
        normalz = posZ - this.centerz;
        double magnitude = Math.sqrt((normalx * normalx) + (normaly * normaly) + (normalz * normalz));
        this.normalx = normalx / magnitude;
        this.normaly = normaly / magnitude;
        this.normalz = normalz / magnitude;*/
    }

    public void randomDirection(Ray nthRay) {

    }

    public void reflectionBounce(Ray nthRay) {

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


    // get plane ID
    public int getObjectID() {
        return this.planeID;
    }

    public double getPosX() {
        return pointx;
    }

    public double getPosY() {
        return pointy;
    }

    public double getPosZ() {
        return pointz;
    }

    public double getLuminance() {
        return this.luminance;
    }

    public void calculateNormal(Ray nthRay) {

    }
    public double getReflectivity() {
        return this.reflectivity;
    }

}
