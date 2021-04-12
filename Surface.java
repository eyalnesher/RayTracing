import java.util.Optional;

/**
 * A class representing a surfece (an object) in the scene.
 */
public abstract class Surface {
    protected final Material material; // The material the surface is made from

    public Surface(Material material) {
        this.material = material;
    }

    /**
     * Check if a ray intersects with the surface, and if so, calculate the point of
     * interseciton.
     * 
     * @return the point of intersection of this and `ray`, if exists.
     */
    public abstract Optional<Vector> intersection(Ray ray);
}
