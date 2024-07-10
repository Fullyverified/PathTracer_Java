package bvh;

import bvh.*;
import sceneobjects.*;
import renderlogic.*;

import java.util.concurrent.*;

public class BVHNodeMultiThreaded {

    private boolean hitLeft, hitRight;
    private BVHNodeMultiThreaded nodeLeft, nodeRight;
    private BoundingBox boundingBox;
    SceneObjects sceneObject;

    // leaf node constructor
    public BVHNodeMultiThreaded(BoundingBox boundingBox, SceneObjects sceneObject) {
        this.boundingBox = boundingBox;
        this.sceneObject = sceneObject;

    }

    // root / node constructor
    public BVHNodeMultiThreaded(BoundingBox boundingBox, BVHNodeMultiThreaded left, BVHNodeMultiThreaded right) {
        this.boundingBox = boundingBox;
        this.nodeLeft = left;
        this.nodeRight = right;
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


    public BVHNodeMultiThreaded searchBVHTree(Ray ray, ExecutorService executor, int depth, int maxDepth) {
        // if the ray does not intersect with the nodes bounding box return -1
        if (!boundingBox.objectCulling(ray)) {
            return null;
        }
        // if sceneObject != null we are at a leaf node
        if (sceneObject != null && sceneObject.objectCulling(ray)) {
            return this;
        }

        // If maximum depth is reached, do not create new threads
        if (depth >= maxDepth) {
            BVHNodeMultiThreaded hitLeft = nodeLeft == null ? null : nodeLeft.searchBVHTree(ray, executor, depth + 1, maxDepth);
            BVHNodeMultiThreaded hitRight = nodeRight == null ? null : nodeRight.searchBVHTree(ray, executor, depth + 1, maxDepth);

            // Determine which child has the closest intersection, if any
            if (hitLeft != null && hitRight != null) {
                return (hitLeft.getIntersectionDistance(ray)[0] < hitRight.getIntersectionDistance(ray)[0]) ? hitLeft : hitRight;
            }
            if (hitLeft == null && hitRight == null) {
                return null;
            }
            // if only one is null, return the other
            return (hitLeft != null) ? hitLeft : hitRight;
        }

        // Create CompletableFuture tasks for left and right child searches
        CompletableFuture<BVHNodeMultiThreaded> futureLeft = nodeLeft == null ? CompletableFuture.completedFuture(null) : CompletableFuture.supplyAsync(() -> nodeLeft.searchBVHTree(ray, executor, depth + 1, maxDepth), executor);
        CompletableFuture<BVHNodeMultiThreaded> futureRight = nodeRight == null ? CompletableFuture.completedFuture(null) : CompletableFuture.supplyAsync(() -> nodeRight.searchBVHTree(ray, executor, depth + 1, maxDepth), executor);

        // Wait for the results
        BVHNodeMultiThreaded hitLeft = null;
        BVHNodeMultiThreaded hitRight = null;
        try {
            hitLeft = futureLeft.get();
            hitRight = futureRight.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Determine which child has the closest intersection, if any
        if (hitLeft != null && hitRight != null) {
            return (hitLeft.getIntersectionDistance(ray)[0] < hitRight.getIntersectionDistance(ray)[0]) ? hitLeft : hitRight;
        }
        if (hitLeft == null && hitRight == null) {
            return null;
        }
        // if only one is null, return the other
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
    public BVHNodeMultiThreaded getLeft() {
        return this.nodeLeft;
    }
    // get right
    public BVHNodeMultiThreaded getRight() {
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