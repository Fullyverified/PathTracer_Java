public class Camera {

    private double posX, dirX, normDirX;
    private double posY, dirY, normDirY;
    private double posZ, dirZ, normDirZ;
    private double posMagnitude, dirMagnitude;

    public static void main(String[] args) {}

    //Constructor
    public Camera(double posX, double posY, double posZ, double dirX, double dirY, double dirZ)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;

        this.dirX = dirX;
        this.dirY = dirY;
        this.dirZ = dirZ;

        this.dirMagnitude = Math.sqrt(dirX*dirX + dirY*dirY + dirZ*dirZ);
        this.normDirX = (this.dirX / this.dirMagnitude);
        this.normDirY = (this.dirY / this.dirMagnitude);
        this.normDirZ = (this.dirZ / this.dirMagnitude);

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
    public double getDirMagnitude() {return this.dirMagnitude;}

    //Setter
    public void setPosX(double posX) {this.posX = posX;}
    public void setPosY(double posY) {this.posY = posY;}
    public void setPosZ(double posZ) {this.posZ = posZ;}
    public void setDirX(double dirX) {this.posX = dirX;}
    public void setDirY(double dirY) {this.posY = dirY;}
    public void setDirZ(double dirZ) {this.posZ = dirZ;}
}
