package RayTracing;

import java.util.Optional;
import java.util.Random;

/**
 * A class representing a light source in the scene
 */
public class Light {
    public final Vector position;
    public final Vector color;
    public final double specularIntensity;
    public final double shadowIntensity;
    public final double radius;

    public Light(Vector position, Vector color, double specular, double shadow, double radius) {
        this.position = position;
        this.color = color;
        this.specularIntensity = specular;
        this.shadowIntensity = shadow;
        this.radius = radius;
    }

    /**
     * Check if a point in the scene is lit from a light source.
     * 
     * @param point  The given point in the scene
     * @param origin The position of the light source
     * @param scene  The scene object
     * @return If there are no surfaces between `point` and `origin`.
     */
    private static boolean isLit(Vector point, Vector origin, Scene scene) {
        Vector direction = origin.sub(point);
        Ray lightRay = new Ray(point.add(direction.mul(0.00000001)), direction);
        Optional<Triple<Surface, Vector, Vector>> closestCollision = lightRay.closestCollision(scene);
        return closestCollision.isEmpty() || point.compareDistances(closestCollision.get().second(), origin) >= 0;
    }

    /**
     * Calculates the intensity of this light at given point
     * 
     * @param scene the Scene object
     * @return Light intensity at the given point.
     */
    public double lightIntensity(Vector point, Scene scene) {
        Vector lightVector = point.sub(this.position);
        Vector u = lightVector.getPerp().normalize();
        Vector v = lightVector.cross(u).normalize();
        Vector[][] originPoints = new Vector[scene.shadowRays][scene.shadowRays]; // A collection of N^2 points from
                                                                                  // which we shoot rays at the target.
        Random r = new Random();

        for (int i = 0; i < scene.shadowRays; i++) {
            for (int j = 0; j < scene.shadowRays; j++) {
                double t = (i + r.nextDouble() - (scene.shadowRays / 2)) * this.radius / scene.shadowRays;
                double s = (j + r.nextDouble() - (scene.shadowRays / 2)) * this.radius / scene.shadowRays;
                originPoints[i][j] = this.position.add(u.mul(t)).add(v.mul(s));
            }
        }

        // TODO: Test collisions
        double totalCollisions = 0;
        for (int i = 0; i < scene.shadowRays; i++) {
            for (int j = 0; j < scene.shadowRays; j++) {
                // Check if there are surfaces between the point and the light source
                if (isLit(point, originPoints[i][j], scene)) {
                    // The ray to the light source collides with a surface before it hits the light
                    // source
                    totalCollisions += 1;
                }
            }
        }
        // double totalCollisions = scene.shadowRays;
        return (1 - this.shadowIntensity)
                + (this.shadowIntensity * (totalCollisions / (scene.shadowRays * scene.shadowRays)));
    }

    /**
     * Calculates the RGB value of the light multiplier at a given ray collision
     * 
     * @param scene    The relevant scene
     * @param surface  The surface intersected by the ray
     * @param point    The point of collision
     * @param normal   The normal to the surface at the point of collision
     * @param specular Whether or not to calculate specular light; if true,
     *                 multiplies each light by its' specular intensity
     * @return The diffuse/specular color multiplier.
     */
    public static Vector lightAtPoint(Scene scene, Surface surface, Vector point, Vector normal, boolean specular) {
        Vector ret = new Vector(0, 0, 0);
        for (Light light : scene.lights) {

            // if (Light.isLit(point, light.position, scene)) {
            // Calculate brightness of light at point.
            Vector baseLight = light.color.mul(light.lightIntensity(point, scene));
            Vector lightVector = light.position.sub(point).normalize();
            if (specular) {
                // for specular light:
                // brightness = dot(R, V) where R is the ray's reflection from the surface at
                // point and V is the vector to the camera
                // specular light(R, G, B) = Color*intensity*specularIntensity*(dot(N,L)^phong)
                Vector reflection = normal.mul(2 * lightVector.dot(normal)).normalize().sub(lightVector);
                double brightness = Math.pow((reflection.dot(scene.camera.position.sub(point).normalize())),
                        surface.material.phong);
                baseLight = baseLight.mul(light.specularIntensity * brightness);
            } else {
                // for diffuse light:
                // brightness = dot(N, L) where N is the normal to the surface at point and L is
                // the vector to the light
                // diffuse light(R, G, B) = Color*intensity*dot(N,L)
                double brightness = lightVector.dot(normal);
                baseLight = baseLight.mul(brightness);
            }
            ret = ret.add(baseLight);
            // }
        }
        return ret;
    }

}
