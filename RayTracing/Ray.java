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

    public Optional<Pair<Surface,Vector>> closestCollision(Scene s) {
        Optional<Pair<Surface,Vector>> closestCollision = new Optional<>();
        double distance = -1;
        for (Surface obj: s.sceneObjects) {
            Optional<Vector> current = obj.intersection(this);
            if (current.isPresent()) {
                if (distance < 0) {
                    closestCollision = Optional.of(new Pair<>(obj, current.get()));
                }
                double currDist = this.origin.distance(current.get());
                if (currDist < distance) {
                    closestCollision = Optional.of(new Pair<>(obj, current.get()));
                    distance = currDist;
                }
            }
        }
        return closestCollision;
    }
}
