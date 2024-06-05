public class Ray {
    private double oX, dirX, normDirX;
    private double oY, dirY, normDirY;
    private double oZ, dirZ, normDirZ;
    private double dirMagnitude;
    private double rayPosX;
    private double rayPosY;
    private double rayPosZ;
    private double hitPointX, hitPointY, hitPointZ, tscalar;
    private boolean hit = false;
    private double px, py, pixelIndexX, pixelIndexY, pixelPosX, pixelPosY;
    private int collidedObject;
    private double lightAmplitude = 0;
    private double[][] luminanceArray;

    // constructor
    public Ray(double oX, double oY, double oZ)
    {
        this.oX = oX;
        this.oY = oY;
        this.oZ = oZ;
    }

    // update magnitude and normalised directions vectors
    public void updateNormalisation()
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


    // getter
    // origin
    public double getOriginX() {return this.oX;}
    public double getOriginY() {return this.oY;}
    public double getOriginZ() {return this.oZ;}
    // pos
    public double getPosX() {return this.rayPosX;}
    public double getPosY() {return this.rayPosY;}
    public double getPosZ() {return this.rayPosZ;}
    // direction
    public double getDirX() {return this.dirX;}
    public double getDirY() {return this.dirY;}
    public double getDirZ() {return this.dirZ;}
    // normalised direction
    public double getNormDirX() {return this.normDirX;}
    public double getNormDirY() {return this.normDirY;}
    public double getNormDirZ() {return this.normDirZ;}
    public double getDirMagnitude() {return this.dirMagnitude;}

    // final intersection point
    public double getHitPointX() {return this.hitPointX;}
    public double getHitPointY() {return this.hitPointY;}
    public double getHitPointZ() {return this.hitPointZ;}
    // hit true or false
    public boolean getHit() {return this.hit;}
    // get pixel index
    public double getPixelIndexX() {return this.pixelIndexX;}
    public double getPixelIndexY() {return this.pixelIndexY;}
    // get pixelPosX
    public double getPixelPosX() {return this.pixelPosX;}
    public double getPixelPosY() {return this.pixelPosY;}

    // get collidedObject
    public int getCollidedObject() {return this.collidedObject;}
    public double getLightAmplitude() {return this.lightAmplitude;}

    public double[][] getLuminanceArray() {return this.luminanceArray;}

    // setter
    // pos
    public void setPosX(double posX) {this.oX = posX;}
    public void setPosY(double posY) {this.oY = posY;}
    public void setPosZ(double posZ) {this.oZ = posZ;}

    // direction
    public void setDirX(double dirX) {this.dirX = dirX;}
    public void setDirY(double dirY) {this.dirY = dirY;}
    public void setDirZ(double dirZ) {this.dirZ = dirZ;}
    // hitpoint
    public void setHitPointX(double pointX) {this.hitPointX = pointX;}
    public void setHitPointY(double pointY) {this.hitPointY = pointY;}
    public void setHitPointZ(double pointZ) {this.hitPointZ = pointZ;}
    public void setHit(boolean hit){this.hit = hit;}
    // set current pixel
    public void setPixelX(int px) {this.px = px;}
    public void setPixelY(int py) {this.py = py;}
    // set pixelindex
    public void setPixelIndexX(double px) {this.pixelIndexX = px;}
    public void setPixelIndexY(double py) {this.pixelIndexY = py;}
    // set pixelpos
    public void setPixelPosX(double px) {this.pixelPosX = px;}
    public void setPixelPosY(double py) {this.pixelPosY = py;}

    // collidedObjectID
    public void setCollidedObject(int obj) {this.collidedObject = obj;}
    //set brightness
    public void addLightAmplitude(double luminance) {
        this.lightAmplitude = this.lightAmplitude + luminance;
    }



}