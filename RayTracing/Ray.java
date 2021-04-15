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
        this.direction = direction;
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
        return this.trace(s, 0);
    }

    /**
     * The recursive calculation of trace
     * 
     * @param s              The relevant scene
     * @param recursionDepth current recursion depth
     * @return A color vector representing the color of the point the ray first
     *         hits.
     */
    private Vector trace(Scene s, int recursionDepth) {
        if (recursionDepth >= s.recursionDepth) {
            // Reached maximum recursion depth
            return s.bgColor;
        }
        Optional<Triple<Surface, Vector, Vector>> collision = this.closestCollision(s);
        if (!collision.isPresent()) {
            // Ray doesn't collide with anything, just veer off into the MAX_DOUBLE void
            return s.bgColor;
        }
        Surface obj = collision.get().first();
        Vector point = collision.get().second();

        // Output = (Mdiff*Ldiff + Mspec*Lspec)(1-transparency) + bgColor*transperency +
        // Mreflect*(reflectedColor)
        // Start with the non-reflection values that we know:
        Vector baseOutput = obj.material.diffuse.pointMult(Light.lightAtPoint(point, s, false));
        baseOutput = baseOutput.add(obj.material.specular.pointMult(Light.lightAtPoint(point, s, true)));
        baseOutput = baseOutput.mul(1 - obj.material.transparency);
        baseOutput = baseOutput.add(s.bgColor.mul(obj.material.transparency));

        Optional<Pair<Vector, Vector>> intersection = obj.intersection(this);
        if (!intersection.isPresent()) {
            // should be impossible as we confirmed that the ray did hit, but just to be
            // safe
            return baseOutput;
        }

        Ray reflectedRay = new Ray(point,
                point.sub(intersection.get().second().mul(2 * point.dot(intersection.get().second()))));
        // Add the Mreflect*(reflectedColor) part:
        return baseOutput.add(obj.material.reflection.pointMult(reflectedRay.trace(s, recursionDepth + 1)));
    }
}
