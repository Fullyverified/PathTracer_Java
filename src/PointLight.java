
public class PointLight implements SceneObjects {

    private double centerx, centerOriginX;
    private double centery, centerOriginY;
    private double centerz, centerOriginZ;
    private double sradius, luminance;
    private double a, b, c, discriminant;
    private double distanceToC, distanceToR;
    private static int numPointLights = 100;
    private int pointLightID = 0;


    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public PointLight(double centerx, double centery, double centerz, double sradius, double luminance) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.sradius = sradius;
        this.luminance = luminance;
        this.pointLightID = numPointLights;
        numPointLights++;
    }

    // p = o + td
    // p new ray position
    // o ray origin
    // t tscalar (amount to march the ray by)
    // d direction vector

    // initial check to see if the ray will or will not hit an object (for performance)
    public boolean intersectionDiscard(Ray ray) {
        // calculate the vector from the spheres center to the origin of the ray
        // oc = o - c
        centerOriginX = ray.getPosX() - this.centerx;
        centerOriginY = ray.getPosY() - this.centery;
        centerOriginZ = ray.getPosZ() - this.centerz;
        // oc = (vectorx, vectory, vectorz)

        // calculate values of a, b, c for the quadratic equation
        // a = the dot product of normx, normy, normz - should always equal 1
        this.a = (ray.getNormDirX() * ray.getNormDirX()) + (ray.getNormDirY() * ray.getNormDirY() + (ray.getNormDirZ() * ray.getNormDirZ()));
        // b = 2 * (the dot product of the centerorigin vector by the direction vector)
        this.b = 2 * ((centerOriginX * ray.getNormDirX()) + (centerOriginY * ray.getNormDirY()) + (centerOriginZ * ray.getNormDirZ()));
        // c = the dot product of centerorigin by itself, - the radius^2 of the sphere
        this.c = ((centerOriginX * centerOriginX) + (centerOriginY * centerOriginY) + (centerOriginZ * centerOriginZ) - (this.sradius * this.sradius));

        // calculate the discriminant | b^2 - 2ac
        this.discriminant = (b * b) - (4 * (a * c));
        //System.out.println("Discriminant: " + this.discriminant);

        if (this.discriminant < 0)
        {
            //System.out.println("No intersection. x: ");
            //System.out.println("----------------------------------------");
            return false;
        }
        else if(this.discriminant == 0)
        {
            //System.out.println("Exactly one intersection");
            //System.out.println("----------------------------------------");
            return true;
        }
        else if (this.discriminant > 0) {
            //System.out.println("The ray intersects at two points");
            //System.out.println("----------------------------------------");
            return true;
        }
        return false;
    }

    // check the distance between the current ray and the sphere
    // distance = sqrt(rayposxyz^2 - spherecenterxyz^2))
    public boolean intersectionCheck(Ray ray)
    {
        // distance of the ray to the center of the sphere
        this.distanceToC = Math.sqrt(Math.pow((ray.getRayPointX() - this.centerx),2) + Math.pow((ray.getRayPointY() - this.centery),2) + Math.pow((ray.getRayPointZ() - this.centerz),2));
        this.distanceToR = this.distanceToC - this.sradius;

        // check if we have hit the sphere yet
        if (distanceToC > sradius)
        {
            //System.out.println("Not intersected yet. x: " + ray.getRayPointX() + " y: " + ray.getRayPointY() + " z: " + ray.getRayPointZ());
            return false;
        }
        else if (distanceToC == sradius)
        {
            //System.out.println("Perfect intersection. x: " + ray.getRayPointX() + " y: " + ray.getRayPointY() + " z: " + ray.getRayPointZ());
            return true;
        }
        else if (distanceToC < sradius)
        {
            //System.out.println("Ray inside sphere. x: " + ray.getRayPointX() + " y: " + ray.getRayPointY() + " z: " + ray.getRayPointZ());
            return true;
        }
        else {System.out.println("Something is wrong");}
        return false;
    }


    // get sphere ID
    public int getObjectID()
    {
        return this.pointLightID;
    }

    public double getLuminance() {return this.luminance;}
    public void setLuminance(double luminance) {this.luminance = luminance;}

    public double getPosX()
    {return this.centerx;}
    public double getPosY()
    {return this.centery;}
    public double getPosZ()
    {return this.centerz;}

}
