package sceneobjects;

import bvh.*;
import sceneobjects.*;
import renderlogic.*;

public class SphereLight implements SceneObjects {

    private double centerx, centerOriginX;
    private double centery, centerOriginY;
    private double centerz, centerOriginZ;
    private double sradius = 1;
    double xradius, yradius, zradius;
    private double a, b, c, discriminant, sqrtDiscriminant;
    private static int numPointLights = 100;
    private int pointLightID = 0;
    private double normalx, normaly, normalz;
    double reflectivity, reflecR, reflecB, reflecG, roughness = 1;
    private double luminance, R, G, B = 0;


    //Equation of a sphere: (x - cx)^2 + (y - cy)^2 + (z - cz)^2 = r^2

    //Constructor
    public SphereLight(double centerx, double centery, double centerz, double xRad, double yRad, double zRad, double luminance, double reflectivity, double roughness) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.xradius = xRad;
        this.yradius = yRad;
        this.zradius = zRad;
        this.luminance = luminance;
        this.reflectivity = reflectivity;
        this.pointLightID = numPointLights;
        numPointLights++;

        this.reflecR = reflectivity;
        this.reflecG = reflectivity;
        this.reflecB = reflectivity;
        this.R = luminance;
        this.G = luminance;
        this.B = luminance;
        this.roughness = roughness;
    }

    public SphereLight(double centerx, double centery, double centerz, double xRad, double yRad, double zRad, double redBrightness, double greenBrightness, double blueBrightness, double redReflectivity, double greenReflectivity, double blueReflectivity, double roughness) {
        this.centerx = centerx;
        this.centery = centery;
        this.centerz = centerz;
        this.xradius = xRad;
        this.yradius = yRad;
        this.zradius = zRad;
        this.R = redBrightness;
        this.G = greenBrightness;
        this.B = blueBrightness;
        this.reflectivity = reflectivity;
        this.pointLightID = numPointLights;
        numPointLights++;

        this.reflecR = redReflectivity;
        this.reflecG = greenReflectivity;
        this.reflecB = blueReflectivity;
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

        // calculate values of a, b, c for the quadratic equation
        // a = the dot product of normx, normy, normz - should always equal 1
        double a = (ray.getDirX() * ray.getDirX() * invXR) + (ray.getDirY() * ray.getDirY() * invYR) + (ray.getDirZ() * ray.getDirZ() * invZR);
        // b = 2 * (the dot product of the centerorigin vector by the direction vector)
        double b = 2 * ((centerOriginX * ray.getDirX() * invXR) + (centerOriginY * ray.getDirY() * invYR) + (centerOriginZ * ray.getDirZ() * invZR));
        // c = the dot product of centerorigin by itself, - the radius^2 of the sphere
        double c = ((centerOriginX * centerOriginX * invXR) + (centerOriginY * centerOriginY * invYR) + (centerOriginZ * centerOriginZ * invZR) - 1);

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
            }
            else {
                return false;
            }
        }
    }

    // this is a weird hack idk why it works. the real ellipsoid equation refuses to work
    public boolean intersectionCheck(Ray ray)
    {
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
            normalx = -ray.getPosX() / (xradius * xradius);;
            normaly = -ray.getPosY() / (yradius * yradius);;
            normalz = -ray.getPosZ() / (zradius * zradius);;
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

    // get sphere ID
    public int getObjectID()
    {
        return this.pointLightID;
    }

    public double getReflectivity() {
        return this.reflectivity;
    }
    public double getLuminance() {return this.luminance;}

    public double getRBrightness() {return this.R;}
    public double getGBrightness() {return this.G;}
    public double getBBrightness() {return this.B;}

    public double getReflecR() {return this.reflecR;}
    public double getReflecG() {return this.reflecG;}
    public double getReflecB() {return this.reflecB;}

    public void setLuminance(double luminance) {this.luminance = luminance;}

    public double getPosX()
    {return this.centerx;}
    public double getPosY()
    {return this.centery;}
    public double getPosZ()
    {return this.centerz;}

    public void setPos(double x, double y, double z){
        this.centerx = x;
        this.centery = y;
        this.centerz = z;
    }

    // get each the normalised normal
    public double getNormalX() {return this.normalx;}
    public double getNormalY() {return this.normaly;}
    public double getNormalZ() {return this.normalz;}

    public double getRoughness() {return this.roughness;}
    public boolean getTransparent() {return false;}
    public double getRefractiveIndex() {return 1;}

    public double[] distanceToEntryExit(Ray ray) {
        double[] distance = new double[2];
        return distance;
    }
}