public interface SceneObjects {

    // sceneObjects implement methods here so they can be automatically looped
    public boolean objectCulling(Ray ray);
    public boolean intersectionCheck(Ray ray);
    public void randomDirection(Ray nthRay);
    public void reflectionBounce(Ray nthRay);
    public int getObjectID();
    public double getPosX();
    public double getPosY();
    public double getPosZ();
    public double getLuminance();
    public void calculateNormal(Ray nthRay);
    public double getNormalX();
    public double getNormalY();
    public double getNormalZ();
}

