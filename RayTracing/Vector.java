package RayTracing;

/**
 * An implementation of three-dimentional vector and vector arithmetics.
 */
public class Vector {
    private double x;
    private double y;
    private double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Vector clone() {
        return new Vector(this.x, this.y, this.z);
    }

    // Getters

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    // Arithmetics

    /**
     * Add `other` to this (element-wise), in-place.
     */
    public void add(Vector other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    /**
     * Multiply this by a scalar (element-wise), in-place.
     */
    public void mul(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
    }

    /**
     * Calculate the negated vector - `this.add(this.neg())` should be equal to a
     * zero vector.
     * 
     * @return the negated vector of this.
     */
    public Vector neg() {
        Vector vector = this.clone();
        vector.mul(-1);
        return vector;
    }

    /**
     * Subtract `other` from this (element-wise), in-place.
     */
    public void sub(Vector other) {
        this.add(other.neg());
    }

    /**
     * Calculate the dot product of two vectors.
     * 
     * @return the dot product of this and `other`.
     */
    public double dot(Vector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    /**
     * Calculate the cross product of two vectors.
     * 
     * @return the cross product of this and `other`.
     */
    public Vector cross(Vector other) {
        return new Vector(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x);
    }

    /**
     * Calculates the euclidian distance from the other vector
     * 
     * @return the euclidian distance from other
     */
    public double distance(Vector other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
    }

    /**
     * Generates a vector perpendicular to this one given desired x, y
     * @return A vector v that satisfies dot(this, v) == 0
     */
    public Vector getPerp() {
        if (this.x == 0 && this.y == 0) {
            return new Vector(1, 1, 0);
        }
        return new Vector(-this.y, this.x, 0);
    }

    @Override
    public boolean equals(Object other) {
        if (!other.getClass().equals(this.getClass())) {
            return false;
        }
        if ((this.x == other.x) && (this.y == other.y) && (this.z == other.z)) {
            return true;
        } else {
            return false;
        }
    }

}
