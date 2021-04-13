package RayTracing;

import java.util.List;

/**
 * A representation of the overall scene holding the camera, scene parameters
 * and entities.
 */
public class Scene {
    public final Vector bgColor;
    public final int shadowRays;
    public final int recursionDepth;
    public final boolean fisheye;
    public final Camera camera;

    public List<Surface> sceneObjects;
    public List<Light> lights;

    public Scene(Vector bgColor, int shadowRays, int recursionDepth, boolean fisheye, Camera camera,
            List<Surface> objects, List<Light> lights) {
        this.bgColor = bgColor;
        this.shadowRays = shadowRays;
        this.recursionDepth = recursionDepth;
        this.fisheye = fisheye;
        this.camera = camera;
        this.sceneObjects = objects;
        this.lights = lights;
    }

    public void addObject(Surface s) {
        this.sceneObjects.add(s);
    }

    public void addLight(Light l) {
        this.lights.add(l);
    }

}
