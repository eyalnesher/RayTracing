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
    public Optional<Vector> intersection(Ray ray) {
        // TODO
        return Optional.empty();
    }
}
