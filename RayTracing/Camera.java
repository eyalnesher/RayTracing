package RayTracing;

import RayTracing.Vector;
/**
 * An implementation of the camera, containing the camera parameters.
 */
public class Camera {
    public final Vector position;
    public final Vector lookAt;
    public final Vector upVector;
    public final double screenDist;
    public final double screenWidth;

    public Camera(Vector pos, Vector lookAt, Vector up, double screenDist, double screenWidth) {
        this.position = pos;
        this.lookAt = lookAt;
        this.upVector = up;
        this.screenDist = screenDist;
        this.screenWidth = screenWidth;
    }
}
