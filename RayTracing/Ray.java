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
}
