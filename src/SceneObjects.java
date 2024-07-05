public interface SceneObjects {

    // sceneObjects implement methods here so they can be automatically looped
    public boolean objectCulling(Ray ray);
    public boolean intersectionCheck(Ray ray);
    public int getObjectID();
    public double getPosX();
    public double getPosY();
    public double getPosZ();
    public double getLuminance();
    public void calculateNormal(Ray nthRay);
    public double getNormalX();
    public double getNormalY();
    public double getNormalZ();
    public double getReflectivity();
    public void setPos(double x, double y, double z);

    /*public double getRBrightness();
    public double getGBrightness();
    public double getBBrightness();
    public double getReflecR();
    public double getReflecG();
    public double getReflecB();*/



}