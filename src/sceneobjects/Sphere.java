package sceneobjects;

import bvh.*;
import sceneobjects.*;
import renderlogic.*;

public class Sphere implements SceneObjects {

    private double centerx, centery, centerz;
    private double xradius, yradius, zradius;
    private static int numSpheres = 0;
    private int sphereID = 0;
    private double normalx, normaly, normalz;
    private double reflectivity, reflecR, reflecB, reflecG, roughness = 1;
    private double luminance, R, G, B = 0;

    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public Sphere(double centerx, double centery, double centerz, double xRad, double yRad, double zRad, double reflectivity, double roughness) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.xradius = xRad;
        this.yradius = yRad;
        this.zradius = zRad;

        this.reflectivity = reflectivity;
        this.sphereID = numSpheres;
        numSpheres++;

        this.reflecR = reflectivity;
        this.reflecG = reflectivity;
        this.reflecB = reflectivity;
        this.roughness = roughness;
    }

    public Sphere(double centerx, double centery, double centerz, double xRad, double yRad, double zRad, double colourR, double colourG, double colourB, double roughness) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.xradius = xRad;
        this.yradius = yRad;
        this.zradius = zRad;

        this.reflectivity = reflectivity;
        this.sphereID = numSpheres;
        numSpheres++;

        this.reflecR = colourR;
        this.reflecG = colourG;
        this.reflecB = colourB;
        this.roughness = roughness;

    }

    public boolean objectCulling(Ray ray) {
        // calculate the vector from the spheres center to the origin of the ray
        // oc = o - c
        double centerOriginX = ray.getPosX() - this.centerx;
        double centerOriginY = ray.getPosY() - this.centery;
        double centerOriginZ = ray.getPosZ() - this.centerz;

        // scale factor along each axis
        double invXR = 1.0 / (xradius * xradius);
        double invYR = 1.0 / (yradius * yradius);
        double invZR = 1.0 / (zradius * zradius);

        //System.out.println("invXR: " + invXR + ", invYR: " + invYR + ", invZR: " + invZR);

        // calculate values of a, b, c for the quadratic equation
        // a = the dot product of normx, normy, normz - should always equal 1
        double a = (ray.getDirX() * ray.getDirX() * invXR) + (ray.getDirY() * ray.getDirY() * invYR) + (ray.getDirZ() * ray.getDirZ() * invZR);
        // b = 2 * (the dot product of the centerorigin vector by the direction vector)
        double b = 2 * ((centerOriginX * ray.getDirX() * invXR) + (centerOriginY * ray.getDirY() * invYR) + (centerOriginZ * ray.getDirZ() * invZR));
        // c = the dot product of centerorigin by itself, - the radius^2 of the sphere
        double c = (centerOriginX * centerOriginX * invXR) + (centerOriginY * centerOriginY * invYR) + (centerOriginZ * centerOriginZ * invZR) - 1;

        // calculate the discriminant | b^2 - 2ac
        double discriminant = (b * b) - (4 * (a * c));

        //System.out.println("a: " + a);
        //System.out.println("b: " + b);
        //System.out.println("c: " + c);
        //System.out.println("discriminant:  " + discriminant);

        if (discriminant < 0) {
            return false;
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double sqrt1 = (-b - sqrtDiscriminant) / (2 * a);
        double sqrt2 = (-b + sqrtDiscriminant) / (2 * a);
        //System.out.println("sqrtdisc:  " + sqrtDiscriminant);
        //System.out.println("sqrt1:  " + sqrt1);
        //System.out.println("sqrt2:  " + sqrt2);

        if (sqrt1 >= 0 || sqrt2 >= 0) {
            return true;
        } else {
            return false;
        }

    }

    // this is a weird hack idk why it works. the real ellipsoid equation refuses to work
    public boolean intersectionCheck(Ray ray) {
        // distance of the ray to the center of the sphere
        double sradius = 1;
        double scaledX = (ray.getPosX() - this.centerx) / xradius;
        double scaledY = (ray.getPosY() - this.centery) / yradius;
        double scaledZ = (ray.getPosZ() - this.centerz) / zradius;

        double distanceToC = Math.sqrt(scaledX * scaledX + scaledY * scaledY + scaledZ * scaledZ);
        // check if we have hit the sphere yet
        return distanceToC <= sradius;
    }

    // calculate the normal of the sphere and a point
    public void calculateNormal(Ray ray) {
        normalx = 2 * (ray.getPosX() - centerx) / (xradius * xradius);
        normaly = 2 * (ray.getPosY() - centery) / (yradius * yradius);
        normalz = 2 * (ray.getPosZ() - centerz) / (zradius * zradius);
        double magnitude = Math.sqrt((normalx * normalx) + (normaly * normaly) + (normalz * normalz));
        if (magnitude != 0) {
            this.normalx = normalx / magnitude;
            this.normaly = normaly / magnitude;
            this.normalz = normalz / magnitude;
        }
        if (centerx == 0 && centery == 0 && centerz == 0) {
            normalx = -ray.getPosX() / (xradius * xradius);
            ;
            normaly = -ray.getPosY() / (yradius * yradius);
            ;
            normalz = -ray.getPosZ() / (zradius * zradius);
            ;
        }
    }

    public double[] getBounds() {

        double epsilon = 0;
        double[] bounds = new double[6];

        bounds[0] = this.centerx - xradius - epsilon;
        bounds[1] = this.centerx + xradius + epsilon;

        bounds[2] = this.centery - yradius - epsilon;
        bounds[3] = this.centery + yradius + epsilon;

        bounds[4] = this.centerz - zradius - epsilon;
        bounds[5] = this.centerz + zradius + epsilon;

        return bounds;
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
        return false;
    }

    public double getRefractiveIndex() {
        return 1;
    }

    public double[] distanceToEntryExit(Ray ray) {
        double[] distance = new double[2];
        return distance;
    }

    public void printType() {
        System.out.println("Type: Sphere");
    }
}