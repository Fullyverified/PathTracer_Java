public class BoundingBox {

    private double minX = 0, maxX = 0;
    private double minY = 0, maxY = 0;
    private double minZ = 0, maxZ = 0;
    private double tminX, tminY, tminZ;
    private double tmaxX, tmaxY, tmaxZ;
    private double tNear = 0, tFar = 0;
    private double normalx, normaly, normalz;

    // constructor
    public BoundingBox(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    // constructor for combining two bounding boxes
    public BoundingBox(BVHNode left, BVHNode right) {
        minX = Math.min(left.getBoundingBox().getBounds()[0], right.getBoundingBox().getBounds()[0]);
        maxX = Math.max(left.getBoundingBox().getBounds()[1], right.getBoundingBox().getBounds()[1]);
        minY = Math.min(left.getBoundingBox().getBounds()[2], right.getBoundingBox().getBounds()[2]);
        maxY = Math.max(left.getBoundingBox().getBounds()[3], right.getBoundingBox().getBounds()[3]);
        minZ = Math.min(left.getBoundingBox().getBounds()[4], right.getBoundingBox().getBounds()[4]);
        maxZ = Math.max(left.getBoundingBox().getBounds()[5], right.getBoundingBox().getBounds()[5]);
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

    public double getArea() {
        double extentX = maxX - minX;
        double extentY = maxY - minY;
        double extentZ = maxZ - minZ;
        return extentX * extentY * extentZ;
    }

    public double[] getIntersectionDistance(Ray ray) {
        double close, far;
        computeMinMax(ray);
        tNear = Math.max(Math.max(tminX, tminY), tminZ);
        tFar = Math.min(Math.min(tmaxX, tmaxY), tmaxZ);
        if (tNear > tFar || tFar < 0) {
           return new double[]{-1, -1}; // no intersection
        }
        // if tNear < 0, return tFar, else return tNear
        return new double[]{tNear, tFar};
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

    public void setBounds(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
        this.minX = xMin;
        this.maxX = xMax;
        this.minY = yMin;
        this.maxY = yMax;
        this.minZ = zMin;
        this.maxZ = zMax;
    }
}