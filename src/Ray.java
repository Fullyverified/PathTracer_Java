public class Ray {
    private double oX, dirX, normDirX;
    private double oY, dirY, normDirY;
    private double oZ, dirZ, normDirZ;
    private double dirMagnitude;
    private double rayPointX, rayPointY, rayPointZ, tscalar;

    //Constructor
    public Ray(double oX, double oY, double oZ, double dirX, double dirY, double dirZ)
    {
        this.oX = oX;
        this.oY = oY;
        this.oZ = oZ;
        this.dirX = dirX;
        this.dirY = dirY;
        this.dirZ = dirZ;

        //Calculate magnitude and normalised direction vectors
        this.dirMagnitude = Math.sqrt(dirX*dirX + dirY*dirY + dirZ*dirZ);
        this.normDirX = (this.dirX / this.dirMagnitude);
        this.normDirY = (this.dirY / this.dirMagnitude);
        this.normDirZ = (this.dirZ / this.dirMagnitude);
    }

    //Update magnitude and normalised directions vectors
    public void updateMagnitude()
    {
        this.dirMagnitude = Math.sqrt(this.dirX*this.dirX + this.dirY*this.dirY + this.dirZ*this.dirZ);
        this.normDirX = (this.dirX / this.dirMagnitude);
        this.normDirY = (this.dirY / this.dirMagnitude);
        this.normDirZ = (this.dirZ / this.dirMagnitude);
    }

    // p = o + td
    // p new ray position
    // o ray origin
    // t tscalar (amount to march the ray by)
    // d direction vector

    //March the ray to the next step (tscalar)
    public void rayMarch(double tscalar)
    {
        this.tscalar = tscalar;
        this.rayPointX = this.oX + (tscalar * normDirX);
        this.rayPointY = this.oY + (tscalar * normDirY);
        this.rayPointZ = this.oZ + (tscalar * normDirZ);
        System.out.println("ray pointx = " + this.rayPointX + ". raypointy = " + rayPointY + ". raypointz = " + rayPointZ);
    }


    //Getter
    public double getPosX() {return this.oX;}
    public double getPosY() {return this.oY;}
    public double getPosZ() {return this.oZ;}
    public double getDirX() {return this.dirX;}
    public double getDirY() {return this.dirY;}
    public double getDirZ() {return this.dirZ;}
    public double getNormDirX() {return this.normDirX;}
    public double getNormDirY() {return this.normDirY;}
    public double getNormDirZ() {return this.normDirZ;}
    public double getDirMagnitude() {return this.dirMagnitude;}
    public double getRayPointX() {return this.rayPointX;}
    public double getRayPointY() {return this.rayPointY;}
    public double getRayPointZ() {return this.rayPointZ;}


    //Setter
    public void setPosX(double posX) {this.oX = posX;}
    public void setPosY(double posY) {this.oY = posY;}
    public void setPosZ(double posZ) {this.oZ = posZ;}
    public void setDirX(double dirX) {this.oX = dirX;}
    public void setDirY(double dirY) {this.oY = dirY;}
    public void setDirZ(double dirZ) {this.oZ = dirZ;}

}
