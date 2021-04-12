package RayTracing;

import java.util.Optional;

public class Box extends Surface {
    private Vector position;
    private double length;

    public Box(Vector position, double length, Material material) {
        super(material);
        this.position = position;
        this.length = length;
    }

    @Override
    public Optional<Vector> intersection(Ray ray) {
        // TODO
        return Optional.empty();
    }
}
