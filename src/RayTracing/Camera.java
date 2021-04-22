package RayTracing;

import java.util.Optional;
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
    
    public Vector mapPixel(Vector center, double x, double y) {
        return center.sub(this.upVector.mul(y * this.screenHeight).add(right.mul(x * this.screenWidth)));
    }

    public double reverseFishEye(double R) {
        if (this.fisheye_param > 0) {
            return Math.atan(R*this.fisheye_param/this.screenDist)/this.fisheye_param;
        } else if (this.fisheye_param == 0) {
            return R/this.screenDist;
        } else {
            return Math.asin(R*this.fisheye_param/this.screenDist)/this.fisheye_param;
        }
    }

    public Optional<Ray> pixelRay(double xRatio, double yRatio) {
        Vector center = this.position.add(this.towards.mul(this.screenDist));
        if (fisheye) {
            double newR = Math.sqrt(xRatio*xRatio + yRatio*yRatio);
            double oldTheta= this.reverseFishEye(newR);
            double oldR = this.screenDist*Math.tan(oldTheta);
            if (newR != 0 && oldR/newR >= 1) {
                xRatio *= oldR/newR;
                yRatio *= oldR/newR;
            } else {
                return Optional.empty();
            }
        }
        Vector P = mapPixel(center, xRatio, yRatio);
        return Optional.of(new Ray(this.position, P.sub(position)));
    } 
}
