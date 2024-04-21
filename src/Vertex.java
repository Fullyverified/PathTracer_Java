public class Vertex {

    private double x;
    private double y;
    private double z;

    //Constructor
    public Vertex (double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //Getter
    public double getX() {return this.x;}
    public double getY() {return this.y;}
    public double getZ() {return this.z;}


    //Setter
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public void setZ(double z) {this.z = y;}
}
