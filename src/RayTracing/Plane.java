package RayTracing;

import java.util.Optional;

public class Plane extends Surface {
    private Vector normal;
    private double distance;

    public Plane(Vector normal, double distance, Material material) {
        super(material);
        this.normal = normal;
        this.distance = distance;
    }

    public boolean onPlane(Vector point) {
        return point.dot(this.normal) == distance;
    }

    @Override
    public Optional<Pair<Vector, Vector>> intersection(Ray ray) {
        double denominator = this.normal.dot(ray.direction);
        if (Math.abs(denominator) > 0) {
            Vector diff = this.normal.mul(distance).sub(ray.origin);
            double length = diff.dot(this.normal) / denominator;
            if (length >= 0) {
                return Optional.of(new Pair<Vector, Vector>(ray.point(length), this.normal));
            }
        }
        return Optional.empty();
    }
}
