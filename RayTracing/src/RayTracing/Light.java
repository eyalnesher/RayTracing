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
     * Calculates the intensity of this light at given point
     * 
     * @param s the Scene object.
     * @return light intensity at the given point.
     */
    public double lightIntensity(Vector point, Scene scene) {
        Vector lightVector = point.clone().add(this.position);
        Vector u = lightVector.getPerp();
        Vector v = lightVector.cross(u);
        Vector[][] originPoints = new Vector[scene.shadowRays][scene.shadowRays]; // A collection of N^2 points from
                                                                                  // which we shoot rays at the target.
        Random r = new Random();

        for (int i = -scene.shadowRays / 2; i < (scene.shadowRays / 2); i++) {
            for (int j = -scene.shadowRays / 2; j < (scene.shadowRays / 2); j++) {
                double t = (i + r.nextDouble()) * this.radius / scene.shadowRays;
                double s = (j + r.nextDouble()) * this.radius / scene.shadowRays;
                originPoints[i][j] = this.position.add(u.mul(t)).add(v.mul(s));
            }
        }

        // Test collisions
        double totalCollisions = 0;
        for (int i = 0; i < scene.shadowRays; i++) {
            for (int j = 0; j < scene.shadowRays; j++) {
                Ray lightRay = new Ray(originPoints[i][j], point);
                Optional<Triple<Surface, Vector, Vector>> closestCollision = lightRay.closestCollision(scene);
                if (closestCollision.isPresent()) {
                    if (point.equals(closestCollision.get().second())) {
                        totalCollisions += 1;
                    }
                }
            }
        }
        return (1 - this.shadowIntensity)
                + this.shadowIntensity * (totalCollisions / (scene.shadowRays * scene.shadowRays));
    }

    /**
     * Calculates the RGB value of the light multiplier at a given point
     * 
     * @param point    the given point
     * @param scene    The relevant scene
     * @param specular Whether or not to calculate specular light; if true,
     *                 multiplies each light by its' specular intensity
     * @return The diffuse color multiplier.
     */
    public static Vector lightAtPoint(Vector point, Scene scene, boolean specular) {
        Vector ret = new Vector(1, 1, 1);
        double brightness = 1;
        for (Light light : scene.lights) {
            // Calculate brightness of light at point.
            // brightness = dot(N, L) where N is the normal to the surface at point and L is
            // the vector to the light
            Ray rayToPoint = new Ray(light.position, point);
            Optional<Triple<Surface, Vector, Vector>> collision = rayToPoint.closestCollision(scene);
            if (collision.isPresent()) {
                Surface surface = collision.get().first();
                Vector collisionPoint = collision.get().second();
                if (point.equals(collisionPoint)) {
                    Vector normal = collision.get().third();
                    brightness = (collisionPoint.add(light.position)).dot(normal);
                    if (specular) {
                        brightness = Math.pow(brightness, surface.material.phong);
                    }
                }
            }
            if (specular) {
                // specular light(R, G, B) = Color*intensity*specularIntensity*(dot(N,L)^phong)
                ret = ret.pointMult(light.color.mul(light.lightIntensity(point, scene)).mul(light.specularIntensity)
                        .mul(brightness));
            } else {
                // diffuse light(R, G, B) = Color*intensity*dot(N,L)
                ret = ret.pointMult(light.color.mul(light.lightIntensity(point, scene)).mul(brightness));
            }

        }
        return ret;
    }

}
