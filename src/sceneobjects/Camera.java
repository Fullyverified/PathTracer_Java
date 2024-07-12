package sceneobjects;

import java.lang.Math;

public class Camera{

    private double posX, dirX, normDirX, upX, normUpX, rightX, normRightX;
    private double posY, dirY, normDirY, upY, normUpY, rightY, normRightY;
    private double posZ, dirZ, normDirZ, upZ, normUpZ, rightZ, normRightZ;
    private double dirMagnitude, upMagnitude, rightMagnitude;
    private double fOV, planeWidth, planeHeight;
    private double aspectRatioX, aspectRatioY, resX, resY;
    private double ISO = 1;
    private double movementSpeed = 0;
    private boolean camUpdate = false;

    public static void main(String[] args) {}

    // constructor
    public Camera(double ISO, double resX, double fOV, double aspectX, double aspectY, double posX, double posY, double posZ, double dirX, double dirY, double dirZ, double upX, double upY, double upZ)
    {
        this.ISO = ISO;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.dirX = dirX;
        this.dirY = dirY;
        this.dirZ = dirZ;
        this.upX = upX;
        this.upY = upY;
        this.upZ = upZ;
        this.fOV = fOV;
        this.aspectRatioX = aspectX;
        this.aspectRatioY = aspectY;
        this.resX = resX;
        this.resY = this.resX / (this.aspectRatioX / this.aspectRatioY);
        // round the vertical resolution of the camera up to fix an array index out of bounds error
        this.resY = Math.ceil(this.resY);
        directionVector();
        upVector();
        rightVector();
        imagePlane();
    }

    public void moveForward() {
        this.posX = posX + movementSpeed * dirX;
        this.posY = posY + movementSpeed * dirY;
        this.posZ = posZ + movementSpeed * dirZ;
        camUpdate = true;
    }

    public void moveBackward() {
        this.posX = posX + movementSpeed * -dirX;
        this.posY = posY + movementSpeed * -dirY;
        this.posZ = posZ + movementSpeed * -dirZ;
        camUpdate = true;
    }

    public void strafeLeft() {
        this.posZ = posZ + movementSpeed;
        camUpdate = true;
    }

    public void strafeRight() {
        this.posZ = posZ - movementSpeed;
        camUpdate = true;
    }

    public void strafeUp() {
        this.posY = posY + movementSpeed;
        camUpdate = true;
    }

    public void strafeDown() {
        this.posZ = posZ - movementSpeed;
        camUpdate = true;
    }

    public void directionVector()
    {
        // calculate magnitude and normalised direction vector
        this.dirMagnitude = Math.sqrt(this.dirX*this.dirX + this.dirY*this.dirY + this.dirZ*this.dirZ);
        this.normDirX = (this.dirX / this.dirMagnitude);
        this.normDirY = (this.dirY / this.dirMagnitude);
        this.normDirZ = (this.dirZ / this.dirMagnitude);
    }

    public void upVector()
    {
        // in cases where the camera is looking straight up or down, the cross product will be NaN.
        if ((this.dirY == 1 || this.dirY == -1) && this.dirX == 0 && this.dirZ == 0) {
        this.normUpX = 1;
        this.normUpY = 0;
        this.normUpZ = 0;
        }

        else
        {
            // calculate magnitude and normalised up vector
            this.upMagnitude = Math.sqrt(this.upX * this.upX + this.upY * this.upY + this.upZ * this.upZ);
            this.normUpX = (this.upX / this.upMagnitude);
            this.normUpY = (this.upY / this.upMagnitude);
            this.normUpZ = (this.upZ / this.upMagnitude);
        }
    }

    public void rightVector()
    {
        // calculate the right vector of the camera
        // rvector = cross product: normalised direction * up vector
        // a * b = (a2b3 - a3b2, a3b1 - a1b3, a1b2 - a2b1)
        this.rightX = (this.normUpY * this.normDirZ) - (this.normUpZ * this.normDirY);
        this.rightY = (this.normUpZ * this.normDirX) - (this.normUpX * this.normDirZ);
        this.rightZ = (this.normUpX * this.normDirY) - (this.normUpY * this.normDirX);

        // calculate magnitude and normalised right vector
        this.rightMagnitude = Math.sqrt(this.rightX*this.rightX + this.rightY*this.rightY + this.rightZ*this.rightZ);
        this.normRightX = this.rightX / this.rightMagnitude;
        this.normRightY = this.rightY / this.rightMagnitude;
        this.normRightZ = this.rightZ / this.rightMagnitude;
    }

    // calculate the image plane size
    // image plane width = 2 * tan(fov/2) * distanceToImagePlane
    // image plane height = width / aspectRatio
    public void imagePlane()
    {
        this.planeWidth = 2 * Math.tan((Math.toRadians(this.fOV) / 2) * 1);
        this.planeHeight = this.planeWidth / (this.aspectRatioX/this.aspectRatioY);
    }



    // getter
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
    public double getUpMagnitude() {return this.upMagnitude;}
    public double getRightMagnitude() {return this.rightMagnitude;}

    public int getResX() {return (int) this.resX;}
    public int getResY() {return (int) this.resY;}

    public double getCamWidth() {return this.planeWidth;}
    public double getCamHeight() {return this.planeHeight;}

    public double getISO() {return this.ISO;}
    public boolean getMoved() {return this.camUpdate;}

    // setter
    // pos
    public void setPosX(double posX) {this.posX = posX;}
    public void setPosY(double posY) {this.posY = posY;}
    public void setPosZ(double posZ) {this.posZ = posZ;}

    // dir
    public void setDirX(double dirX) {this.dirX = dirX;}
    public void setDirY(double dirY) {this.dirY = dirY;}
    public void setDirZ(double dirZ) {this.dirZ = dirZ;}

    // up vector (rotation)
    public void setUpX(double upX) {this.upX = upX;}
    public void setUpY(double upY) {this.upY = upY;}
    public void setUpZ(double upZ) {this.upZ = upZ;}

    // fov and aspect ratio
    public void setFOV(int fOV) {this.fOV = fOV;}
    public void setAspectX(int aspectRatioX) {this.aspectRatioX = aspectRatioX;}
    public void setAspectY(int aspectRatioY) {this.aspectRatioY = aspectRatioY;}
    public void setMoved(boolean update) {this.camUpdate = update;}

}
