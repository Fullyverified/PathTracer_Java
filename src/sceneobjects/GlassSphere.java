package sceneobjects;

import bvh.*;
import sceneobjects.*;
import renderlogic.*;

public class GlassSphere implements SceneObjects {

    private double centerx, centery, centerz;
    private double sradius = 1;
    private static int numSpheres = 0;
    private int sphereID = 0;
    private double normalx, normaly, normalz;
    private double reflectivity, reflecR, reflecB, reflecG, roughness = 1, refractiveIndex = 1;
    private double luminance, R, G, B;

    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public GlassSphere(double centerx, double centery, double centerz, double radius, double reflectivity, double roughness, double refractiveIndex) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.sradius = radius;

        this.reflectivity = reflectivity;
        this.sphereID = numSpheres;
        numSpheres++;

        this.reflecR = reflectivity;
        this.reflecG = reflectivity;
        this.reflecB = reflectivity;
        this.roughness = roughness;
        this.refractiveIndex = refractiveIndex;
    }

    public GlassSphere(double centerx, double centery, double centerz, double radius, double colourR, double colourG, double colourB, double roughness, double refractiveIndex) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.sradius = radius;

        this.reflectivity = reflectivity;
        this.sphereID = numSpheres;
        numSpheres++;

        this.reflecR = colourR;
        this.reflecG = colourG;
        this.reflecB = colourB;
        this.roughness = roughness;
        this.refractiveIndex = refractiveIndex;
    }

    public boolean objectCulling(Ray ray) {
        // calculate the vector from the spheres center to the origin of the ray
        // oc = o - c
        double centerOriginX = ray.getPosX() - this.centerx;
        double centerOriginY = ray.getPosY() - this.centery;
        double centerOriginZ = ray.getPosZ() - this.centerz;

        // scale factor along each axis
        double scaleFactor = 1 / (sradius * sradius);

        // calculate values of a, b, c for the quadratic equation
        // a = the dot product of normx, normy, normz - should always equal 1
        double a = (ray.getDirX() * ray.getDirX() * scaleFactor) + (ray.getDirY() * ray.getDirY() * scaleFactor) + (ray.getDirZ() * ray.getDirZ() * scaleFactor);
        // b = 2 * (the dot product of the centerorigin vector by the direction vector)
        double b = 2 * ((centerOriginX * ray.getDirX() * scaleFactor) + (centerOriginY * ray.getDirY() * scaleFactor) + (centerOriginZ * ray.getDirZ() * scaleFactor));
        // c = the dot product of centerorigin by itself, - the radius^2 of the sphere
        double c = ((centerOriginX * centerOriginX * scaleFactor) + (centerOriginY * centerOriginY * scaleFactor) + (centerOriginZ * centerOriginZ * scaleFactor) - 1);

        // calculate the discriminant | b^2 - 2ac
        double discriminant = (b * b) - (4 * (a * c));

        if (discriminant < 0) {
            return false;
        } else {
            double sqrtDiscriminant = Math.sqrt(discriminant);
            double sqrt1 = (-b - sqrtDiscriminant) / (2 * a);
            double sqrt2 = (-b + sqrtDiscriminant) / (2 * a);

            if (sqrt1 >= 0 || sqrt2 >= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    // this is a weird hack idk why it works. the real ellipsoid equation refuses to work
    public boolean intersectionCheck(Ray ray) {
        double scaleFactor = 1 / (sradius);
        // distance of the ray to the center of the sphere
        double sradius = 1;
        double scaledX = (ray.getPosX() - this.centerx) * scaleFactor;
        double scaledY = (ray.getPosY() - this.centery) * scaleFactor;
        double scaledZ = (ray.getPosZ() - this.centerz) * scaleFactor;

        double distanceToC = Math.sqrt(scaledX * scaledX + scaledY * scaledY + scaledZ * scaledZ);
        // check if we have hit the sphere yet
        return distanceToC <= sradius;
    }

    // calculate the normal of the sphere and a point
    public void calculateNormal(Ray ray) {
        double scaleFactor = 1 / (sradius * sradius);
        normalx = 2 * (ray.getPosX() - centerx) * scaleFactor;
        normaly = 2 * (ray.getPosY() - centery) * scaleFactor;
        normalz = 2 * (ray.getPosZ() - centerz) * scaleFactor;
        double magnitude = Math.sqrt((normalx * normalx) + (normaly * normaly) + (normalz * normalz));
        if (magnitude != 0) {
            this.normalx = normalx / magnitude;
            this.normaly = normaly / magnitude;
            this.normalz = normalz / magnitude;
        }
        if (centerx == 0 && centery == 0 && centerz == 0) {
            normalx = -ray.getPosX() * scaleFactor;
            normaly = -ray.getPosY() * scaleFactor;
            normalz = -ray.getPosZ() * scaleFactor;
        }
    }

    public double[] getBounds() {

        double epsilon = 0;
        double[] bounds = new double[6];

        bounds[0] = this.centerx - sradius - epsilon;
        bounds[1] = this.centerx + sradius + epsilon;

        bounds[2] = this.centery - sradius - epsilon;
        bounds[3] = this.centery + sradius + epsilon;

        bounds[4] = this.centerz - sradius - epsilon;
        bounds[5] = this.centerz + sradius + epsilon;

        return bounds;
    }

    public double[] distanceToEntryExit(Ray ray) {
        double[] distance = new double[2];

        // calculate the vector from the spheres center to the origin of the ray
        // oc = o - c
        double centerOriginX = ray.getPosX() - this.centerx;
        double centerOriginY = ray.getPosY() - this.centery;
        double centerOriginZ = ray.getPosZ() - this.centerz;

        // calculate values of a, b, c for the quadratic equation
        // a = the dot product of normx, normy, normz - should always equal 1
        double a = ray.getDirX() * ray.getDirX() + ray.getDirY() * ray.getDirY() + ray.getDirZ() * ray.getDirZ();
        // b = 2 * (the dot product of the centerorigin vector by the direction vector)
        double b = 2 * (centerOriginX * ray.getDirX() + centerOriginY * ray.getDirY() + centerOriginZ * ray.getDirZ());
        // c = the dot product of centerorigin by itself, - the radius^2 of the sphere
        double c = (centerOriginX * centerOriginX + centerOriginY * centerOriginY + centerOriginZ * centerOriginZ) - (this.sradius * this.sradius);

        // Discriminant of the quadratic equation
        double discriminant = (b * b) - (4 * a * c);

        if (discriminant > 0) {
            double sqrtDiscriminant = Math.sqrt(discriminant);
            double t1 = (-b - sqrtDiscriminant) / (2 * a);
            double t2 = (-b + sqrtDiscriminant) / (2 * a);

            if (t1 > t2) {
                double temp = t1;
                t1 = t2;
                t2 = temp;
            }

            if (t1 > 0) {
                distance[0] = t1;  // Entry distance
                distance[1] = t2;  // Exit distance
            }
            else if (t2 > 0) {
                distance[0] = 0; //
                distance[1] = t2;
            }
        }
        return distance;
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

    public void setPos(double x, double y, double z) {
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

    public double getRBrightness() {
        return this.R;
    }

    public double getGBrightness() {
        return this.G;
    }

    public double getBBrightness() {
        return this.B;
    }

    public double getReflecR() {
        return this.reflecR;
    }

    public double getReflecG() {
        return this.reflecG;
    }

    public double getReflecB() {
        return this.reflecB;
    }

    public double getRoughness() {
        return this.roughness;
    }

    public boolean getTransparent() {
        return true;
    }

    public double getRefractiveIndex() {
        return this.refractiveIndex;
    }
    public void printType() {
        System.out.println("Type: GlassSphere");
    }
}