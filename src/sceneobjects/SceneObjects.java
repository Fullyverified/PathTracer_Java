package sceneobjects;

import renderlogic.*;

public interface SceneObjects {

    // sceneObjects implement methods here so they can be automatically looped
    boolean objectCulling(Ray ray);
    boolean intersectionCheck(Ray ray);
    int getObjectID();
    double getPosX();
    double getPosY();
    double getPosZ();
    void calculateNormal(Ray nthRay);
    double getNormalX();
    double getNormalY();
    double getNormalZ();
    double getReflectivity();

    double getRBrightness();
    double getGBrightness();
    double getBBrightness();
    double getReflecR();
    double getReflecG();
    double getReflecB();

    double getRoughness();
    boolean getTransparent();
    double getRefractiveIndex();

    double[] getBounds();
    double[] distanceToEntryExit(Ray ray);
    void printType();
}