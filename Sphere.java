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
    public Optional<Vector> intersection(Ray ray) {
        // TODO
        return Optional.empty();
    }

}
