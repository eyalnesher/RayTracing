package RayTracing;

import java.util.ArrayList;
import java.util.Arrays;
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

        public double pointAxis(Vector point) {
            switch (this) {
            case X: {
                return point.x;
            }
            case Y: {
                return point.y;
            }
            default: {
                // case Z:
                return point.z;
            }

            }
        }
    }

    /**
     * Get the two faces perpendicular to a given axis (the box is axis aligned).
     * 
     * @param axis The axis which identifies the faces (x, y or z).
     * @return The planes which extend the two faces perpendicular to `axis`.
     */
    private Pair<Plane, Plane> faces(Axis axis) {
        double position = axis.pointAxis(this.position);

        return new Pair<Plane, Plane>(new Plane(axis.axis, position - this.length / 2, this.material),
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
        return Math.abs(axis.pointAxis(point) - axis.pointAxis(this.position));
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
        boolean xBounds = distanceFromAxis(point, Axis.X) <= this.length / 2;
        boolean yBounds = distanceFromAxis(point, Axis.Y) <= this.length / 2;
        boolean zBounds = distanceFromAxis(point, Axis.Z) <= this.length / 2;

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
    public Optional<Pair<Vector, Vector>> intersection(Ray ray) {
        return Arrays.stream(Axis.values())
                .flatMap(axis -> Pair.stream(this.faces(axis)).map(face -> face.intersection(ray))
                        .filter(intersection -> intersection.isPresent()
                                && this.inFaceBounds(intersection.get().first(), axis)))
                .map((Optional<Pair<Vector, Vector>> intersection) -> intersection.get())
                .min((Pair<Vector, Vector> intersection1, Pair<Vector, Vector> intersection2) -> ray.origin
                        .compareDistances(intersection1.first(), intersection2.first()));

    }
}
