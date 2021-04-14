package RayTracing;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Optional;

/**
 * A class representing a surface (an object) in the scene.
 */
public abstract class Surface {
    protected final Material material; // The material the surface is made from

    public Surface(Material material) {
        this.material = material;
    }

    /**
     * Check if a ray intersects with the surface, and if so, calculate the point of
     * interseciton and the normal of the surface at the intersection point.
     * 
     * @return A pair of the point of intersection of this and `ray` and the normal
     *         to the surface, if such an intersection exists.
     */

    public abstract Optional<SimpleImmutableEntry<Vector, Vector>> intersection(Ray ray);
}
