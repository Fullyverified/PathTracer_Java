public class AABCubeBounds implements SceneObjects {

    private double minX, maxX;
    private double minY, maxY;
    private double minZ, maxZ;
    private double tminX, tminY, tminZ;
    private double tmaxX, tmaxY, tmaxZ;
    private double tNear = 0, tFar = 0;
    private static int numCubes = 200;
    private int cubeID = 0;
    private double normalx, normaly, normalz;
    private double luminance, R, G, B = 0;
    double reflectivity, reflecR, reflecB, reflecG, roughness = 1;


    // constructor
    public AABCubeBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ, double reflectivity, double roughness) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.reflectivity = reflectivity;

        this.cubeID = numCubes;
        numCubes++;

        this.reflecR = reflectivity;
        this.reflecG = reflectivity;
        this.reflecB = reflectivity;
        this.roughness = roughness;
    }

    public AABCubeBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ, double colourR, double colourG, double colourB, double roughness) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;

        this.cubeID = numCubes;
        numCubes++;

        this.reflecR = colourR;
        this.reflecG = colourG;
        this.reflecB = colourB;
        this.roughness = roughness;
    }

    public void computeMinMax(Ray ray) {

        // (cubecorner - ray origin) / ray direction
        // if the direction is negative they may need to be flipped

        double tmp;
        if (ray.getDirX() == 0) {
            if (ray.getPosX() < minX || ray.getPosX() > maxX) {
                tminX = Double.POSITIVE_INFINITY; // No intersection possible on this axis
                tmaxX = Double.NEGATIVE_INFINITY;
            } else {
                tminX = Double.NEGATIVE_INFINITY; // Always intersecting on this axis
                tmaxX = Double.POSITIVE_INFINITY;
            }
        } else {
            tminX = (minX - ray.getPosX()) / ray.getDirX();
            tmaxX = (maxX - ray.getPosX()) / ray.getDirX();
            if (tminX > tmaxX) {
                tmp = tmaxX;
                tmaxX = tminX;
                tminX = tmp;
            }
        }

        if (ray.getDirY() == 0) {
            if (ray.getPosY() < minY || ray.getPosY() > maxY) {
                tminY = Double.POSITIVE_INFINITY; // No intersection possible on this axis
                tmaxY = Double.NEGATIVE_INFINITY;
            } else {
                tminY = Double.NEGATIVE_INFINITY; // Always intersecting on this axis
                tmaxY = Double.POSITIVE_INFINITY;
            }
        } else {
            tminY = (minY - ray.getPosY()) / ray.getDirY();
            tmaxY = (maxY - ray.getPosY()) / ray.getDirY();
            if (tminY > tmaxY) {
                tmp = tmaxY;
                tmaxY = tminY;
                tminY = tmp;
            }
        }

        if (ray.getDirZ() == 0) {
            if (ray.getPosZ() < minZ || ray.getPosZ() > maxZ) {
                tminZ = Double.POSITIVE_INFINITY; // No intersection possible on this axis
                tmaxZ = Double.NEGATIVE_INFINITY;
            } else {
                tminZ = Double.NEGATIVE_INFINITY; // Always intersecting on this axis
                tmaxZ = Double.POSITIVE_INFINITY;
            }
        } else {
            tminZ = (minZ - ray.getPosZ()) / ray.getDirZ();
            tmaxZ = (maxZ - ray.getPosZ()) / ray.getDirZ();
            if (tminZ > tmaxZ) {
                tmp = tmaxZ;
                tmaxZ = tminZ;
                tminZ = tmp;
            }
        }
    }

    // initial check to see if the ray will or will not hit the cube (for performance)
    public boolean objectCulling(Ray ray) {
        computeMinMax(ray);
        tNear = Math.max(Math.max(tminX, tminY), tminZ);
        tFar = Math.min(Math.min(tmaxX, tmaxY), tmaxZ);
        if (tNear > tFar || tFar < 0) {
            return false; // no intersection
        }
        return true; // intersection
    }

    // check if the ray is intersecting the cube
    public boolean intersectionCheck(Ray ray) {

        if (minX <= ray.getPosX() && maxX >= ray.getPosX() && minY <= ray.getPosY() && maxY >= ray.getPosY() && minZ <= ray.getPosZ() && maxZ >= ray.getPosZ()) {
            return true;
        } else {
            return false;
        }

    }

    // calculate the normal of the sphere and a point
    public void calculateNormal(Ray nthRay) {
        double epsilon = 0.05;
        // x
        if ((Math.abs(nthRay.getPosX() - minX)) < epsilon) {
            setNormal(-1, 0, 0);
        } else if ((Math.abs(nthRay.getPosX() - maxX)) < epsilon) {
            setNormal(1, 0, 0);
        }
        // y
        else if ((Math.abs(nthRay.getPosY() - minY)) < epsilon) {
            setNormal(0, -1, 0);
        } else if ((Math.abs(nthRay.getPosY() - maxY)) < epsilon) {
            setNormal(0, 1, 0);
        }
        // z
        else if ((Math.abs(nthRay.getPosZ() - minZ)) < epsilon) {
            setNormal(0, 0, -1);
        } else if ((Math.abs(nthRay.getPosZ() - maxZ)) < epsilon) {
            setNormal(0, 0, 1);
        }
    }

    public double[] getBounds() {
        double[] bounds = new double[6];

        bounds[0] = minX;
        bounds[1] = maxX;

        bounds[2] = minY;
        bounds[3] = maxY;

        bounds[4] = minZ;
        bounds[5] = maxZ;

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

    public void setNormal(double x, double y, double z) {
        this.normalx = x;
        this.normaly = y;
        this.normalz = z;
    }

    public void setPos(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax){
        this.minX = xMin;
        this.maxX = xMax;
        this.minY = yMin;
        this.maxY = yMax;
        this.minZ = zMin;
        this.maxZ = zMax;
    }

    // get sphere ID
    public int getObjectID() {
        return this.cubeID;
    }

    public double getPosX() {
        return this.minX;
    }

    public double getPosY() {
        return this.minY;
    }

    public double getPosZ() {
        return this.minZ;
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