package RayTracing;

/**
 * An aggregate class of material properties.
 */
public class Material {
    public final Vector diffuse; // The diffuse color of the material
    public final Vector specular; // The specular color of the material
    public final double phong; // The Phong specularity coefficient of the material
    public final Vector reflection; // The reflection color of the material
    public final double transparency; // The transparency of the material

    public Material(Vector diffuse, Vector specular, double phong, Vector reflection, double transparency) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.phong = phong;
        this.reflection = reflection;
        this.transparency = transparency;
    }

}
