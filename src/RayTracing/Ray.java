package RayTracing;

import java.util.Optional;

/**
 * A class representing a ray from a point in the scene.
 */
public class Ray {
    // every point on the ray is of the form `origin` + t * `direction`, for some
    // offset t > 0.
    public final Vector origin; // The origin point of the ray
    public final Vector direction; // The ray's direction

    public Ray(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    /**
     * Get the collision closest to the ray's origin.
     * 
     * @param scene The rendered scene.
     * @return A triple consisting of the surface of the collision closest to the
     *         origin, the point of intersection and the normal to the surface at
     *         the point of intersection.
     */
    public Optional<Triple<Surface, Vector, Vector>> closestCollision(Scene scene) {
        return scene.sceneObjects.stream()
                .map((Surface surface) -> new Pair<Surface, Optional<Pair<Vector, Vector>>>(surface,
                        surface.intersection(this)))
                .filter((Pair<Surface, Optional<Pair<Vector, Vector>>> intersect) -> intersect.second().isPresent())
                .map((Pair<Surface, Optional<Pair<Vector, Vector>>> intersect) -> new Triple<Surface, Vector, Vector>(
                        intersect.first(), intersect.second().get()))
                .min((Triple<Surface, Vector, Vector> intersection1,
                        Triple<Surface, Vector, Vector> intersection2) -> this.origin
                                .compareDistances(intersection1.second(), intersection2.second()));
    }

    /**
     * Get a point on the ray.
     * 
     * @param offset The distance of the desired point from the origin.
     * @return The point on the ray in distance `distance` from its origin.
     */
    public Vector point(double offset) {
        return this.origin.add(this.direction.mul(offset));

    }

    /**
     * Generates the ray that originates from the given ray's direction and is
     * opposite in direction
     * 
     * @return the reverse of the given ray
     */
    public static Ray reverse(Ray ray) {
        return new Ray(ray.direction, ray.origin);
    }

    /**
     * Traces the ray's path in the scene, calculating the color at its' first
     * collision.
     * 
     * @param s              The relevant scene
     * @param recursionDepth current recursion depth (call with 0)
     * @return A color vector representing the color of the point the ray first
     *         hits.
     */
    public Vector trace(Scene s) {
        Vector ret = this.trace(s, 0);
        if (ret.x > 1) {
            ret = new Vector(1, ret.y, ret.z);
        } else if (ret.x < 0) {
            ret = new Vector(0, ret.y, ret.z);
        }
        if (ret.y > 1) {
            ret = new Vector(ret.x, 1, ret.z);
        } else if (ret.y < 0) {
            ret = new Vector(ret.x, 0, ret.z);
        }
        if (ret.z > 1) {
            ret = new Vector(ret.x, ret.y, 1);
        } else if (ret.z < 0) {
            ret = new Vector(ret.x, ret.y, 0);
        }
        return ret;
    }

    /**
     * The recursive calculation of a trace.
     * 
     * @param scene          The relevant scene
     * @param recursionDepth current recursion depth
     * @return A color vector representing the color of the point the ray first
     *         hits.
     */
    private Vector trace(Scene scene, int recursionDepth) {
        if (recursionDepth >= scene.recursionDepth) {
            // Reached maximum recursion depth
            return scene.bgColor;
        }
        Optional<Triple<Surface, Vector, Vector>> collision = this.closestCollision(scene);
        if (!collision.isPresent()) {
            // Ray doesn't collide with anything, just veer off into the MAX_DOUBLE void
            return scene.bgColor;
        }
        Surface surface = collision.get().first();
        Vector point = collision.get().second();

        // Output = (Mdiff*Ldiff + Mspec*Lspec)(1-transparency) + bgColor*transperency +
        // Mreflect*(reflectedColor)
        // Start with the non-reflection values that we know:
        Vector baseOutput = surface.material.diffuse.pointMult(Light.lightAtPoint(point, scene, false))
        .add(surface.material.specular.pointMult(Light.lightAtPoint(point, scene, true)))
        .mul(1 - surface.material.transparency)
        .add(scene.bgColor.mul(surface.material.transparency));

        Vector normal = collision.get().third();

        Ray reflectedRay = new Ray(point, point.sub(normal.mul(2 * point.dot(normal))));
        // Add the Mreflect*(reflectedColor) part:
        return baseOutput.add(surface.material.reflection.pointMult(reflectedRay.trace(scene, recursionDepth + 1)));
    }
}
