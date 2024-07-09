import org.w3c.dom.ls.LSOutput;

public class BVHNode {

    private boolean hitLeft, hitRight;
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
        // if the ray does not intersect with the nodes bounding box return -1
        if (!boundingBox.objectCulling(ray)) {
            return null;
        }
        // if sceneObject != null we are at a leaf node
        if (sceneObject != null && sceneObject.objectCulling(ray)) {
            return this;
        }

        // recursively check the left and right children
        BVHNode hitLeft = nodeLeft == null ? null : nodeLeft.searchBVHTree(ray);
        BVHNode hitRight = nodeRight == null ? null : nodeRight.searchBVHTree(ray);

        // determine which child has the closest intersection, if any
        if (hitLeft != null && hitRight != null) {
            return (hitLeft.getIntersectionDistance(ray)[0] < hitRight.getIntersectionDistance(ray)[0]) ? hitLeft : hitRight;
        }
        if (hitLeft == null && hitRight == null) {
            return null;
        }
        // if only one is null, return the other
        return (hitLeft != null) ? hitLeft : hitRight;
    }

    // getter
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
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
}