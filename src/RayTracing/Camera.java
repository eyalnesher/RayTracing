package RayTracing;

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
    public final boolean fisheye;
    public final double fisheye_param;

    public Camera(Vector pos, Vector lookAt, Vector up, double screenDist, double screenWidth, boolean fisheye,
            double fisheye_param) {
        this.position = pos;
        this.lookAt = lookAt;
        this.upVector = fixUpVector(up, lookAt);
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

    public Ray pixelRay(int imageWidth, int imageHeight, int x, int y) {
        Vector xAxisVector = this.lookAt.cross(this.upVector);
        Vector pixel = this.position.add(
            this.lookAt.sub(this.position)
            .mul(1/Math.sqrt(this.lookAt.sub(this.position).dot(this.lookAt.sub(this.position))))
            .mul(this.screenDist))
				.add(xAxisVector.mul((double)(x-(imageWidth/2))/imageWidth))
				.add(this.upVector.mul((double)(y-(imageHeight/2))/imageHeight));
        if (this.fisheye) {
            // TODO: Apply fisheye transformation on pixel location
        }
        Ray pixelRay = new Ray(this.position, pixel);
        return pixelRay;
    }
}
