package RayTracing;

import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;

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

    public Optional<SimpleEntry<Surface,Vector>> closestCollision(Scene s) {
        Optional<SimpleEntry<Surface,Vector>> closestCollision = Optional.empty();
        double distance = -1;
        for (Surface obj: s.sceneObjects) {
            Optional<Vector> current = obj.intersection(this);
            if (current.isPresent()) {
                if (distance < 0) {
                    closestCollision = Optional.of(new SimpleEntry<>(obj, current.get()));
                }
                double currDist = this.origin.distance(current.get());
                if (currDist < distance) {
                    closestCollision = Optional.of(new SimpleEntry<>(obj, current.get()));
                    distance = currDist;
                }
            }
        }
        return closestCollision;
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
     * Generates the ray that originates from the given ray's direction and is opposite in direction
     * 
     * @return the reverse of the given ray
     */
    public static Ray reverse(Ray ray) {
        return new Ray(ray.direction, ray.origin);
    }

    /**
     * Traces the ray's path in the scene, calculating the color at its' first collision.
     * @param s The relevant scene
     * @param recursionDepth current recursion depth (call with 0)
     * @return A color vector representing the color of the point the ray first hits.
     */
    public Vector trace(Scene s) {
        return this.trace(s, 0);
    }

    /**
     * The recursive calculation of trace
     * @param s The relevant scene
     * @param recursionDepth current recursion depth
     * @return A color vector representing the color of the point the ray first hits.
     */
    private Vector trace(Scene s, int recursionDepth) {
        if (recursionDepth >= s.recursionDepth) {
            return new Vector(0, 0, 0);
        }
        Optional<SimpleEntry<Surface, Vector>> collision = this.closestCollision(s);
        if (!collision.isPresent()) {
            return new Vector(0, 0, 0);
        }
        Surface obj = collision.get().getKey();
        Vector point = collision.get().getValue();
        Vector baseOutput = obj.material.diffuse.pointMult(Light.lightAtPoint(point, s, false));
        baseOutput = baseOutput.add(obj.material.specular.pointMult(Light.lightAtPoint(point, s, true)));
        baseOutput = baseOutput.mul(1-obj.material.transparency);
        baseOutput = baseOutput.add(s.bgColor.mul(obj.material.transparency));
        Optional<Vector> normal = obj.normal(point);
        if (!normal.isPresent()) {
            return baseOutput;
        }

        Ray reflectedRay = new Ray(point, point.sub(normal.get().mul(2*point.dot(normal.get()))));
        return baseOutput.add(obj.material.reflection.pointMult(reflectedRay.trace(s, recursionDepth + 1)));
    }
}
