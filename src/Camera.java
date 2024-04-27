public class Camera {

    private double posX, dirX, normDirX, upX, normUpX, rightX, normRightX;
    private double posY, dirY, normDirY, upY, normUpY, rightY, normRightY;
    private double posZ, dirZ, normDirZ, upZ, normUpZ, rightZ, normRightZ;
    private double posMagnitude, dirMagnitude;

    public static void main(String[] args) {}

    // constructor
    public Camera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ, double upX, double upY, double upZ, double rightX, double rightY, double rightZ)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;

        this.dirX = dirX;
        this.dirY = dirY;
        this.dirZ = dirZ;

        // calculate magnitude and normalised direction vectors
        this.dirMagnitude = Math.sqrt(dirX*dirX + dirY*dirY + dirZ*dirZ);
        this.normDirX = (this.dirX / this.dirMagnitude);
        this.normDirY = (this.dirY / this.dirMagnitude);
        this.normDirZ = (this.dirZ / this.dirMagnitude);

        this.normUpX = (this.upX / this.dirMagnitude);
        this.normUpY = (this.upY / this.dirMagnitude);
        this.normUpZ = (this.upZ / this.dirMagnitude);;

        // calculate the right vector of the camera
        // rvector = cross product: normalised direction * up vector
        // a * b = (a2b3 - a3b2, a3b1 - a1b3, a1b2 - a2b1)



        this.rightX = this.upX * this.normDirX;
        this.rightY = this.upY * this.normDirX;
        this.rightZ = this.upZ * this.normDirX;

        this.normRightX = this.rightX / Math.abs(this.rightX);

    }

    //Getter
    public double getPosX() {return this.posX;}
    public double getPosY() {return this.posY;}
    public double getPosZ() {return this.posZ;}

    public double getDirX() {return this.dirX;}
    public double getDirY() {return this.dirY;}
    public double getDirZ() {return this.dirZ;}

    public double getNormDirX() {return this.normDirX;}
    public double getNormDirY() {return this.normDirY;}
    public double getNormDirZ() {return this.normDirZ;}

    public double getNormUpX() {return this.normUpX;}
    public double getNormUpY() {return this.normUpY;}
    public double getNormUpZ() {return this.normUpZ;}

    public double getNormRightX() {return this.normRightX;}
    public double getNormRightY() {return this.normRightY;}
    public double getNormRightZ() {return this.normRightZ;}

    public double getDirMagnitude() {return this.dirMagnitude;}

    //Setter
    public void setPosX(double posX) {this.posX = posX;}
    public void setPosY(double posY) {this.posY = posY;}
    public void setPosZ(double posZ) {this.posZ = posZ;}

    public void setDirX(double dirX) {this.posX = dirX;}
    public void setDirY(double dirY) {this.posY = dirY;}
    public void setDirZ(double dirZ) {this.posZ = dirZ;}


}
