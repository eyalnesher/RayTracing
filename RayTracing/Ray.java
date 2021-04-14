package RayTracing;

import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
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

    public Optional<SimpleEntry<Surface, Vector>> closestCollision(Scene s) {
        Optional<SimpleEntry<Surface, Vector>> closestCollision = Optional.empty();
        double distance = -1;
        for (Surface obj : s.sceneObjects) {
            Optional<SimpleImmutableEntry<Vector, Vector>> current = obj.intersection(this);
            if (current.isPresent()) {
                Vector currentPoint = current.get().getKey();
                if (distance < 0) {
                    closestCollision = Optional.of(new SimpleEntry<>(obj, currentPoint));
                }
                double currDist = this.origin.distance(currentPoint);
                if (currDist < distance) {
                    closestCollision = Optional.of(new SimpleEntry<>(obj, currentPoint));
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
}
