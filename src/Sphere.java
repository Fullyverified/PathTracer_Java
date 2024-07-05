public class Sphere implements SceneObjects {

    private double centerx, centerOriginX;
    private double centery, centerOriginY;
    private double centerz, centerOriginZ;
    private double sradius;
    private double a, b, c, discriminant, sqrtDiscriminant;
    private double distanceToC, distanceToR;
    private static int numSpheres = 0;
    private int sphereID = 0;
    private double normalx, normaly, normalz;
    double reflectivity, reflecR, reflecB, reflecG, roughness = 1;
    private double luminance, R, G, B = 0;

    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public Sphere(double centerx, double centery, double centerz, double sradius, double reflectivity, double roughness) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.sradius = sradius;
        this.reflectivity = reflectivity;
        this.sphereID = numSpheres;
        numSpheres++;

        this.reflecR = reflectivity;
        this.reflecG = reflectivity;
        this.reflecB = reflectivity;
        this.roughness = roughness;
    }

    public Sphere(double centerx, double centery, double centerz, double sradius, double colourR, double colourG, double colourB, double roughness) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.sradius = sradius;
        this.reflectivity = reflectivity;
        this.sphereID = numSpheres;
        numSpheres++;

        this.reflecR = colourR;
        this.reflecG = colourG;
        this.reflecB = colourB;
        this.roughness = roughness;

    }

    // p = o + td
    // p new ray position
    // o ray origin
    // t tscalar (amount to march the ray by)
    // d direction vector

    // initial check to see if the ray will or will not hit an object (for performance)
    public boolean objectCulling(Ray ray) {
        // calculate the vector from the spheres center to the origin of the ray
        // oc = o - c
        centerOriginX = ray.getPosX() - this.centerx;
        centerOriginY = ray.getPosY() - this.centery;
        centerOriginZ = ray.getPosZ() - this.centerz;

        // calculate values of a, b, c for the quadratic equation
        // a = the dot product of normx, normy, normz - should always equal 1
        this.a = (ray.getDirX() * ray.getDirX()) + (ray.getDirY() * ray.getDirY() + (ray.getDirZ() * ray.getDirZ()));
        //this.a = 1;

        // b = 2 * (the dot product of the centerorigin vector by the direction vector)
        this.b = 2 * ((centerOriginX * ray.getDirX()) + (centerOriginY * ray.getDirY()) + (centerOriginZ * ray.getDirZ()));
        // c = the dot product of centerorigin by itself, - the radius^2 of the sphere
        this.c = ((centerOriginX * centerOriginX) + (centerOriginY * centerOriginY) + (centerOriginZ * centerOriginZ) - (this.sradius * this.sradius));

        // calculate the discriminant | b^2 - 2ac
        this.discriminant = (b * b) - (4 * (a * c));
        //System.out.println("Discriminant: " + this.discriminant);

        if (this.discriminant < 0) {
            //System.out.println("No intersection. x: ");
            //System.out.println("----------------------------------------");
            return false;
        } else {

            sqrtDiscriminant = Math.sqrt(discriminant);
            double sqrt1 = (-b - sqrtDiscriminant) / (2 * a);
            double sqrt2 = (-b + sqrtDiscriminant) / (2 * a);

            if (sqrt1 >= 0 || sqrt2 >= 0) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    // check the distance between the current ray and the sphere
// distance = sqrt(rayposxyz^2 - spherecenterxyz^2))
    public boolean intersectionCheck(Ray ray) {

        // distance of the ray to the center of the sphere
        this.distanceToC = Math.sqrt(Math.pow((ray.getPosX() - this.centerx), 2) + Math.pow((ray.getPosY() - this.centery), 2) + Math.pow((ray.getPosZ() - this.centerz), 2));
        this.distanceToR = this.distanceToC - this.sradius;

        // check if we have hit the sphere yet
        if (distanceToC > sradius) {
            //System.out.println("Not intersected yet. x: " + ray.getPosX() + " y: " + ray.getPosY() + " z: " + ray.getPosZ());
            return false;
        } else if (distanceToC == sradius) {
            //System.out.println("Perfect intersection. x: " + ray.getPosX() + " y: " + ray.getPosY() + " z: " + ray.getPosZ());
            return true;
        } else if (distanceToC < sradius) {
            //System.out.println("Ray inside sphere. x: " + ray.getPosX() + " y: " + ray.getPosY() + " z: " + ray.getPosZ());
            return true;
        } else {
            System.out.println("Something is wrong");
        }
        return false;
    }

    // calculate the normal of the sphere and a point
    public void calculateNormal(Ray nthRay) {
        normalx = nthRay.getPosX() - this.centerx;
        normaly = nthRay.getPosY() - this.centery;
        normalz = nthRay.getPosZ() - this.centerz;
        double magnitude = Math.sqrt((normalx * normalx) + (normaly * normaly) + (normalz * normalz));
        if (magnitude != 0) {
            this.normalx = normalx / magnitude;
            this.normaly = normaly / magnitude;
            this.normalz = normalz / magnitude;
        }
        if (centerx == 0 && centery == 0 && centerz == 0) {
            normalx = nthRay.getPosX() * -1;
            normaly = nthRay.getPosY() * -1;
            normalz = nthRay.getPosZ() * -1;
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

    // get sphere ID
    public int getObjectID() {
        return this.sphereID;
    }

    public double getPosX() {
        return this.centerx;
    }

    public double getPosY() {
        return this.centery;
    }

    public double getPosZ() {
        return this.centerz;
    }

    public void setPos(double x, double y, double z){
        this.centerx = x;
        this.centery = y;
        this.centerz = z;
    }

    public double getLuminance() {
        return this.luminance;
    }

    public double getReflectivity() {
        return this.reflectivity;
    }

    public double getRBrightness() {return this.R;}
    public double getGBrightness() {return this.G;}
    public double getBBrightness() {return this.B;}

    public double getReflecR() {return this.reflecR;}
    public double getReflecG() {return this.reflecG;}
    public double getReflecB() {return this.reflecB;}

    public double getRoughness() {return this.roughness;}

}