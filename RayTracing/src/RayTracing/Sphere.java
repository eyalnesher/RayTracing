package RayTracing;

import java.util.Optional;

public class Sphere extends Surface {
    Vector center;
    double radius;

    public Sphere(Vector center, double radius, Material material) {
        super(material);
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Optional<Pair<Vector, Vector>> intersection(Ray ray) {

        // Calculates the intersection of a ray and a sphere as seen in class

        Vector diff = this.center.sub(ray.origin);
        double projection = diff.dot(ray.direction);

        if (projection < 0) {
            return Optional.empty();
        }

        double squaredDistance = diff.dot(diff) - projection * projection;

        double squaredLength = this.radius * this.radius - squaredDistance;
        if (squaredLength < 0) {
            return Optional.empty();
        }

        Vector intersectionPoint = ray.point(projection - Math.sqrt(squaredLength));

        return Optional.of(new Pair<Vector, Vector>(intersectionPoint, intersectionPoint.sub(this.center)));
    }

}
