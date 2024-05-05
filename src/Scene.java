import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<SceneObjects> sceneObjects = new ArrayList<>();
    private Camera camera;

    public void addObject(SceneObjects obj)
    {
        sceneObjects.add(obj);
    }

    public void setCamera(Camera cam)
    {
        this.camera = cam;
    }

    public List<SceneObjects> getObjects()
    {
        return sceneObjects;
    }

}
