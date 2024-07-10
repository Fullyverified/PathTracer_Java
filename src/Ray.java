public class Ray {
    private double oX, dirX;
    private double oY, dirY;
    private double oZ, dirZ;
    private double dirMagnitude;
    private double posX;
    private double posY;
    private double posZ;
    private double hitPointX, hitPointY, hitPointZ, tscalar;
    private boolean hit = false;
    private double px, py, pixelIndexX, pixelIndexY, pixelPosX, pixelPosY;
    private int collidedObject;

    private double avgR = 0, avgG = 0, avgB = 0;
    private double absoluteR = 0, absoluteG = 0, absoluteB = 0;

    private SceneObjects hitObject;
    private long numHits = 0;

    // constructor
    public Ray(double oX, double oY, double oZ)
    {
        this.oX = oX;
        this.oY = oY;
        this.oZ = oZ;
        this.posX = oX;
        this.posY = oY;
        this.posZ = oZ;
    }

    // update magnitude and normalised directions vectors
    public void updateNormalisation()
    {
       // division trick doesn't help here
        this.dirMagnitude = Math.sqrt(this.dirX*this.dirX + this.dirY*this.dirY + this.dirZ*this.dirZ);
        this.dirX = (this.dirX / this.dirMagnitude);
        this.dirY = (this.dirY / this.dirMagnitude);
        this.dirZ = (this.dirZ / this.dirMagnitude);
    }

    // p = o + td
    // p new ray position
    // o ray origin
    // t tscalar (amount to march the ray by)
    // d direction vector
    public void marchRay(double distance)
    {
        this.posX = oX + (distance * dirX);
        this.posY = oY + (distance * dirY);
        this.posZ = oZ + (distance * dirZ);
    }

    // used to march the ray slightly after giving it a random direction
    public void updateOrigin(double distance)
    {
        this.oX = oX + (distance * dirX);
        this.oY = oY + (distance * dirY);
        this.oZ = oZ + (distance * dirZ);
        this.posX = oX;
        this.posY = oY;
        this.posZ = oZ;
    }

    public void initializeRay(Ray primaryRay){
        // initialise ray pos
        setOrigin(primaryRay.getHitPointX(), primaryRay.getHitPointY(), primaryRay.getHitPointZ());
        // give the ray a direction
        marchRay(0);
        setHitPoint(primaryRay.getHitPointX(), primaryRay.getHitPointY(), primaryRay.getHitPointZ());
        setHitObject(primaryRay.getHitObject());
        setDirection(primaryRay.getDirX(), primaryRay.getDirY(), primaryRay.getDirZ());
        setHit(true);
    }

    public void updateHitProperties(SceneObjects hitObject){
        setHit(true);
        setHitPoint(posX, posY, posZ);
        setOrigin(hitPointX, hitPointY, hitPointZ);
        setHitObject(hitObject);
    }

    // getter
    // origin
    public double getOriginX() {return this.oX;}
    public double getOriginY() {return this.oY;}
    public double getOriginZ() {return this.oZ;}
    public String getOrigin() {return this.oX + ", " + this.oY + ", " + this.oZ;}
    // pos
    public double getPosX() {return this.posX;}
    public double getPosY() {return this.posY;}
    public double getPosZ() {return this.posZ;}
    public String getPosition() {return this.posX + ", " + this.posY + ", " + this.posZ;}
    // direction
    public double getDirX() {return this.dirX;}
    public double getDirY() {return this.dirY;}
    public double getDirZ() {return this.dirZ;}
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
    public SceneObjects getHitObject() {return this.hitObject;}
    // get brightness
    public double getAvgRed() {return this.avgR;}
    public double getAvgGreen() {return this.avgG;}
    public double getAvgBlue() {return this.avgB;}

    public double getAbsoluteR() {return this.absoluteR;}
    public double getAbsoluteG() {return this.absoluteG;}
    public double getAbsoluteB() {return this.absoluteB;}

    public int getHitObjectID(){return this.collidedObject;}
    public long getNumHits() {return this.numHits;}
    public void addNumHits() {this.numHits++;}

    // setter
    // pos
    public void setPosX(double posX) {this.posX = posX;}
    public void setPosY(double posY) {this.posY = posY;}
    public void setPosZ(double posZ) {this.posZ = posZ;}
    public void setPos(double posX, double posY, double posZ) {this.posX = posX; this.posY = posY; this.posZ = posZ;}
    // origin
    public void setOriginX(double oX) {this.oX = oX;}
    public void setOriginY(double oY) {this.oY = oY;}
    public void setOriginZ(double oZ) {this.oZ = oZ;}
    public void setOrigin(double oX, double oY, double oZ) {this.oX = oX; this.oY = oY; this.oZ = oZ;}
    // direction
    public void setDirX(double dirX) {this.dirX = dirX;}
    public void setDirY(double dirY) {this.dirY = dirY;}
    public void setDirZ(double dirZ) {this.dirZ = dirZ;}
    public void setDirection(double dirX, double dirY, double dirZ) {this.dirX = dirX; this.dirY = dirY; this.dirZ = dirZ;}
    // hitpoint
    public void setHitPointX(double pointX) {this.hitPointX = pointX;}
    public void setHitPointY(double pointY) {this.hitPointY = pointY;}
    public void setHitPointZ(double pointZ) {this.hitPointZ = pointZ;}
    public void setHitPoint(double pointX, double pointY, double pointZ) {this.hitPointX = pointX; this.hitPointY = pointY; this.hitPointZ = pointZ;}
    // boolean hit
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
    public void setHitObject(SceneObjects hitObject) {this.hitObject = hitObject;}
    public void setHitObjectID(int obj) {this.collidedObject = obj;}
    //set brightness
    public void addLightRGBAbsolute(double R, double G, double B) {
        this.absoluteR = this.absoluteR + R;
        this.absoluteG = this.absoluteG + G;
        this.absoluteB = this.absoluteB + B;
    }
    public void setAvgRed(double red) {this.avgR = red;}
    public void setAvgGreen(double green) {this.avgG = green;}
    public void setAvgBlue(double blue) {this.avgB = blue;}


}