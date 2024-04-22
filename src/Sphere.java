public class Sphere {

    private double centerx, vectorx;
    private double centery, vectory;
    private double centerz, vectorz;
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

    //b^2 - 2ac

    // p = o + td
    // p new ray position
    // o ray origin
    // t tscalar (amount to march the ray by)
    // d direction vector

    public void intersectionCheck(Ray ray) {
        // caculate the vector from the spheres center to the origin of the ray
        // oc = o - c
        vectorx = ray.getPosX() - this.centerx;
        vectory = ray.getPosY() - this.centery;
        vectorz = ray.getPosZ() - this.centerz;

        // calculate values of a, b, c for the quadratic equation
        // the normalised direction should always be 1 (so it doesnt matter if you use x, y or z)
        this.a = (ray.getNormDirX() * ray.getNormDirX()) + (ray.getNormDirY() * ray.getNormDirY() + (ray.getNormDirZ() * ray.getNormDirZ()));
        this.b = 2 * ((vectorx * ray.getNormDirX()) + (vectory * ray.getNormDirY()) + (vectorz * ray.getNormDirZ()));
        this.c = ((vectorx * vectorx) + (vectory * vectory) + (vectorz * vectorz) - (this.sradius * this.sradius));
        System.out.println("a = " + a + ". b = " + b + ". c = " + c);

        // calculate the discriminant
        this.discriminant = (b * b) - 4 * (a * c);

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
        }

    }
}
