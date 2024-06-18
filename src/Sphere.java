import java.util.Random;

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
    private double luminance = 0;

    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public Sphere(double centerx, double centery, double centerz, double sradius) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.sradius = sradius;
        this.sphereID = numSpheres;
        numSpheres++;
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
        double dotproduct = normalx * nthRay.getDirX() + normaly * nthRay.getDirY() + normalz * nthRay.getDirZ();
        calculateNormal(nthRay);
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


    public double getLuminance() {
        return this.luminance;
    }
}