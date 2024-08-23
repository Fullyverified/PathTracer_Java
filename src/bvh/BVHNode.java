package bvh;

import bvh.*;
import sceneobjects.*;
import renderlogic.*;
public class BVHNode {

    private BVHNode nodeLeft, nodeRight;
    private BoundingBox boundingBox;
    SceneObjects sceneObject;

    // leaf node constructor
    public BVHNode(BoundingBox boundingBox, SceneObjects sceneObject) {
        this.boundingBox = boundingBox;
        this.sceneObject = sceneObject;
    }

    // root / node constructor
    public BVHNode(BoundingBox boundingBox, BVHNode left, BVHNode right) {
        this.boundingBox = boundingBox;
        this.nodeLeft = left;
        this.nodeRight = right;
    }

    // copy constructor
    public BVHNode(BVHNode original){
        this.boundingBox = original.boundingBox;
        this.sceneObject = original.sceneObject;
        this.nodeLeft = original.nodeLeft;
        this.nodeRight = original.nodeRight;
    }

    // get total number of children belonging to a node
    public int getNumChildren() {

        // if the node has a scene object it is a leaf node
        if (sceneObject != null) {
            return 1;
        }
        else {
            int leftChildren = (nodeLeft != null) ? nodeLeft.getNumChildren() : 0;
            int rightChildren = (nodeRight != null) ? nodeRight.getNumChildren() : 0;
            return leftChildren + rightChildren;
        }
    }

    public double[] getIntersectionDistance(Ray ray) {
        return boundingBox.getIntersectionDistance(ray);
    }

    public BVHNode searchBVHTree(Ray ray) {
        // ray does not intersect with the nodes bounding box
        if (!boundingBox.objectCulling(ray)) {
            //System.out.println("Returning nullptr");
            return null;
        }
        // if sceneObject != null we are at a leaf node
        if (sceneObject != null) {
            //sceneObject.printType();
        }
        if (sceneObject != null && sceneObject.objectCulling(ray)) {
            //System.out.println("Returning this");
            return this;
        }

        //System.out.println("Determining left and right nodes");
        // recursively check the left and right children
        BVHNode hitLeft = nodeLeft == null ? null : nodeLeft.searchBVHTree(ray);
        BVHNode hitRight = nodeRight == null ? null : nodeRight.searchBVHTree(ray);

        // determine which child has the closest intersection, if any
        if (hitLeft != null && hitRight != null) { // both nodes exist
            double leftDistance = hitLeft.getIntersectionDistance(ray)[0];
            double rightDistance = hitRight.getIntersectionDistance(ray)[0];
            if (leftDistance < 0 && rightDistance < 0) { // both objects are behind the ray
                //System.out.println("DistanceLeft < 0 && DistanceRight < 0");
                return null;
            }
            if (leftDistance < 0) {
                //System.out.println("DistanceLeft < 0 - return right");
                return hitRight;
            }
            if (rightDistance < 0) {
                //System.out.println("DistanceRight < 0 - return left");
                return hitLeft;
            }
            //System.out.println("Returning closest");
            return (leftDistance < rightDistance) ? hitLeft : hitRight;
        }

        if (hitLeft == null && hitRight == null) { // both nodes are null
            //System.out.println("Both null");
            return null;
        }
        // if only one is null, return the other
        //System.out.println("One null, returning other");
        return (hitLeft != null) ? hitLeft : hitRight;
    }


    // getters

    // bounding box
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    // get box area
    public double getArea() {
        return boundingBox.getArea();
    }
    // get left
    public BVHNode getLeft() {
        return this.nodeLeft;
    }
    // get right
    public BVHNode getRight() {
        return this.nodeRight;
    }
    public SceneObjects getSceneObject() {
        return sceneObject;
    }
    // get center of position
    public double getX() {
        return this.sceneObject.getPosX();
    }
    public double getY() {
        return this.sceneObject.getPosY();
    }
    public double getZ() {
        return this.sceneObject.getPosZ();
    }
}