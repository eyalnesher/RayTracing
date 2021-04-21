package RayTracing;

import java.util.stream.Stream;

/**
 * An implementation of the camera, containing the camera parameters.
 */
public class Camera {
    public final Vector position;
    public final Vector towards;
    public final Vector upVector;
    public final Vector right;
    public final double screenDist;
    public final double screenWidth;
    public final double screenHeight;
    public final boolean fisheye;
    public final double fisheye_param;

    public Camera(Vector pos, Vector lookAt, Vector up, double screenDist, double screenWidth, double aspectRatio,
            boolean fisheye, double fisheye_param) {
        this.position = pos;
        this.towards = lookAt.sub(pos).normalize();
        this.right = this.towards.cross(up).normalize();
        this.upVector = fixUpVector(up, this.towards, this.right);
        this.screenDist = screenDist;
        this.screenWidth = screenWidth;
        this.screenHeight = screenWidth * aspectRatio;
        this.fisheye = fisheye;
        this.fisheye_param = fisheye_param;
    }

    public Camera(Vector pos, Vector lookAt, Vector up, double screenDist, double screenWidth, double aspectRatio,
            boolean fisheye) {
        this(pos, lookAt, up, screenDist, screenWidth, aspectRatio, fisheye, 0.5);
    }

    public Camera(Vector pos, Vector lookAt, Vector up, double screenDist, double screenWidth, double aspectRatio) {
        this(pos, lookAt, up, screenDist, screenWidth, aspectRatio, false, 0.5);
    }

    /**
     * Fix the up vector to be perpendicular to the looking direction of the camera
     * 
     * @param up     The previous up vector
     * @param lookAt The direction the camera is looking at
     * @return A vector perpendicular to `lookAt` with roughly the same direction as
     *         `up`
     */
    private static Vector fixUpVector(Vector up, Vector towards, Vector right) {
        Vector fixed = right.cross(towards).normalize();
        return Stream.of(fixed, fixed.neg()).min((u, v) -> up.compareDistances(u, v)).get();
    }

    public Ray pixelRay(double x, double y) {
        Vector center = this.position.add(this.towards.mul(this.screenDist));
        // Vector py = center.add(this.upVector.mul(this.screenHeight / 2));
        // Vector px = center.add(right.mul(this.screenWidth / 2));
        // Vector P = py.sub(this.upVector.mul(y
        // *this.screenHeight).add(px.sub(right.mul(x * this.screenWidth))));
        Vector P = center.sub(this.upVector.mul(y * this.screenHeight).sub(right.mul(x * this.screenWidth)));
        Ray pixelRay = new Ray(this.position, P.sub(position));
        return pixelRay;
    }
}
