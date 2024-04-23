public class Sphere {

    private double centerx, centerOriginX;
    private double centery, centerOriginY;
    private double centerz, centerOriginZ;
    private double sradius;
    private double a, b, c, discriminant;


    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public Sphere(double centerx, double centery, double centerz, double sradius) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.sradius = sradius;
    }

    // p = o + td
    // p new ray position
    // o ray origin
    // t tscalar (amount to march the ray by)
    // d direction vector

    public void intersectionCheck(Ray ray) {
        // calculate the vector from the spheres center to the origin of the ray
        // oc = o - c
        centerOriginX = ray.getRayPointX() - this.centerx;
        centerOriginY = ray.getRayPointY() - this.centery;
        centerOriginZ = ray.getRayPointZ() - this.centerz;
        System.out.println("oc = " + centerOriginX + ", " + centerOriginY + ", " + centerOriginZ);
        // oc = (vectorx, vectory, vectorz)

        // calculate values of a, b, c for the quadratic equation
        // a = the dot product of normx, normy, normz - should always equal 1
        this.a = (ray.getNormDirX() * ray.getNormDirX()) + (ray.getNormDirY() * ray.getNormDirY() + (ray.getNormDirZ() * ray.getNormDirZ()));
        // b = 2 * (the dot product of the centerorigin vector by the direction vector)
        this.b = 2 * ((centerOriginX * ray.getNormDirX()) + (centerOriginY * ray.getNormDirY()) + (centerOriginZ * ray.getNormDirZ()));
        // c = the dot product of centerorigin by itself, - the radius^2 of the sphere
        this.c = ((centerOriginX * centerOriginX) + (centerOriginY * centerOriginY) + (centerOriginZ * centerOriginZ) - (this.sradius * this.sradius));
        System.out.println("a = " + a + ". b = " + b + ". c = " + c);

        // calculate the discriminant | b^2 - 2ac
        this.discriminant = (b * b) - (4 * (a * c));

        if (this.discriminant < 0)
        {
            System.out.println("No intersection");
        }
        else if(this.discriminant == 0)
        {
            System.out.println("Exactly one intersection");
        }
        else if (this.discriminant > 0) {
            System.out.println("The ray intersects at two points");
            System.out.println("----------------------------------------");
        }

    }
}
