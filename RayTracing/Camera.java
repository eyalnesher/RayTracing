package RayTracing;

/**
 * An implementation of the camera, containing the camera parameters.
 */
public class Camera {
    public final Vector position;
    public final Vector lookAt;
    public final Vector upVector;
    public final double screenDist;
    public final double screenWidth;
    public final boolean fisheye;
    public final double fisheye_param;

    public Camera(Vector pos, Vector lookAt, Vector up, double screenDist, double screenWidth, boolean fisheye, double fisheye_param) {
        this.position = pos;
        this.lookAt = lookAt;
        this.upVector = up;
        this.screenDist = screenDist;
        this.screenWidth = screenWidth;
        this.fisheye = fisheye;
        this.fisheye_param = fisheye_param;
    }

    public Camera(Vector pos, Vector lookAt, Vector up, double screenDist, double screenWidth, boolean fisheye) {
        this(pos, lookAt, up, screenDist, screenWidth, fisheye, 0.5);
    }

    public Camera(Vector pos, Vector lookAt, Vector up, double screenDist, double screenWidth) {
        this(pos, lookAt, up, screenDist, screenWidth, false, 0.5);
    }
}
