package renderlogic;

public class Vector3 {

    private double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    // add
    public void add(Vector3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    // add new
    public Vector3 addNew(Vector3 other) {
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    // subtract
    public void subtract(Vector3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
    }

    // subtract new
    public Vector3 subtractNew(Vector3 other) {
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    // dot
    public double dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    // cross
    public Vector3 cross( Vector3 other) {
        return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    // normalise
    public void normalise() {
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude != 0) {
            x /= magnitude;
            y /= magnitude;
            z /= magnitude;
        }
    }

    // set
    public void set(double newX, double newY, double newZ) {
        x = newX;
        y = newY;
        z = newZ;
    }

    // setV
    public void setV(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3 getVector3() {
        return new Vector3(x, y, z);
    }

    public double getX() {
       return this.x;
    }
    public double getY() {
        return this.y;
    }
    public double getZ() {
        return this.z;
    }

    // set x
    public void setX(double x) {
        this.x = x;
    }
    // set y
    public void setY(double y) {
        this.y = x;
    }
    // set z
    public void setZ(double z) {
        this.z = x;
    }

}
