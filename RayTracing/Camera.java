package RayTracing;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * An implementation of the camera, containing the camera parameters.
 */
public class Camera {
    public final Vector position;
    public final Vector lookAt;
    public final Vector upVector;
    public final double screenDist;
    public final double screenWidth;

    public Camera(Vector position, Vector lookAt, Vector up, double screenDist, double screenWidth) {
        this.position = position;
        this.lookAt = lookAt;
        this.upVector = fixUpVector(up, lookAt);
        this.screenDist = screenDist;
        this.screenWidth = screenWidth;
    }

    /**
     * Fix the up vector to be perpendicular to the looking direction of the camera
     * 
     * @param up     The previous up vector
     * @param lookAt The direction the camera is looking at
     * @return A vector perpendicular to `lookAt` with roughly the same direction as
     *         `up`
     */
    private static Vector fixUpVector(Vector up, Vector lookAt) {
        Vector fixed = up.cross(lookAt).cross(lookAt);
        return Stream.of(fixed, fixed.neg()).min((u, v) -> up.compareDistances(u, v)).get();
    }
}
