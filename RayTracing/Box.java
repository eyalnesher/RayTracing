package RayTracing;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Optional;

public class Box extends Surface {
    private Vector position;
    private double length;

    public Box(Vector position, double length, Material material) {
        super(material);
        this.position = position;
        this.length = length;
    }

    /**
     * An enum representation of the three 3D axes x, y and z.
     */
    private enum Axis {
        X(new Vector(1, 0, 0)), Y(new Vector(0, 1, 0)), Z(new Vector(0, 0, 1));

        public final Vector axis;

        private Axis(Vector axis) {
            this.axis = axis;
        }
    }

    /**
     * Get the two faces perpendicular to a given axis (the box is axis aligned).
     * 
     * @param axis The axis which identifies the faces (x, y or z).
     * @return The planes which extend the two faces perpendicular to `axis`.
     */
    private SimpleEntry<Plane, Plane> faces(Axis axis) {
        double position = 0;

        switch (axis) {
        case X: {
            position = this.position.x;
            break;
        }
        case Y: {
            position = this.position.x;
            break;
        }
        case Z: {
            position = this.position.x;
            break;
        }

        }

        return new SimpleEntry<Plane, Plane>(new Plane(axis.axis, position - this.length / 2, this.material),
                new Plane(axis.axis, position + this.length / 2, this.material));
    }

    /**
     * Calculate the distance of a point from the box's center position in a single
     * axis.
     * 
     * @param point A point in space.
     * @param axis  The axis (x, y or z) that the distance is calculated relative
     *              to.
     * @return The absolute distance between `this.position` and `point` in the
     *         `axis` axis.
     */
    private double distanceFromAxis(Vector point, Axis axis) {
        double distance = 0;
        switch (axis) {
        case X: {
            distance = point.y - this.position.y;
        }
        case Y: {
            distance = point.y - this.position.y;
            break;
        }
        case Z: {
            distance = point.y - this.position.y;
            break;
        }

        }
        return Math.abs(distance);
    }

    /**
     * Check if a point is in the bounds defined by a box face. Notice that the
     * bounds of two parallel faces are equal, and thus the bounds are defined by a
     * single axis (the one perpendicular to the two faces).
     * 
     * If the point is on one of the planes perpendicular to the axis, and also in
     * bounds, then the point is on the box's surface.
     * 
     * @param point A point in space.
     * @param axis  The axis (x, y or z) perpendicular to the face(s) that defines
     *              the bounds.
     * @return If the point is in the bounds of the faces defined by `axis`.
     */
    private boolean inFaceBounds(Vector point, Axis axis) {
        boolean xBounds = distanceFromAxis(point, Axis.X) < this.length / 2;
        boolean yBounds = distanceFromAxis(point, Axis.Y) < this.length / 2;
        boolean zBounds = distanceFromAxis(point, Axis.Z) < this.length / 2;

        switch (axis) {
        case X: {
            return yBounds && zBounds;
        }
        case Y: {
            return xBounds && zBounds;
        }
        default: {
            // case Z:
            return xBounds && yBounds;
        }

        }
    }

    @Override
    public Optional<Vector> intersection(Ray ray) {
        ArrayList<Vector> intersections = new ArrayList<>();
        for (Axis axis : Axis.values()) {
            SimpleEntry<Plane, Plane> faces = this.faces(axis);
            Optional<Vector> intersection1 = faces.getKey().intersection(ray);
            Optional<Vector> intersection2 = faces.getValue().intersection(ray);
            if (!intersection1.isEmpty()) {
                intersections.add(intersection1.get());
            }
            if (!intersection2.isEmpty()) {
                intersections.add(intersection2.get());
            }
        }

        return intersections.stream()
                .max((Vector point1, Vector point2) -> ((Double) point1.squaredDistance(ray.origin))
                        .compareTo((Double) point2.squaredDistance(ray.origin)));

    }
}
