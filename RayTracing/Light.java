package RayTracing;

import java.util.Optional;
import java.util.Random;
import java.util.AbstractMap.SimpleImmutableEntry;

import RayTracing.Ray;
import RayTracing.Surface;
import RayTracing.Vector;

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
     * @param s the Scene object.
     * @return light intensity at the given point.
     */
    public double lightIntensity(Vector point, Scene scene) {
        Vector lightVector = point.clone().add(this.position);
        Vector u = lightVector.getPerp();
        Vector v = lightVector.cross(u);
        Vector[][] originPoints = new Vector[scene.shadowRays][scene.shadowRays]; // A collection of N^2 points from which we shoot rays at the target.
        Random r = new Random();

        for (int i = -scene.shadowRays/2; i < (scene.shadowRays/2); i++) {
            for (int j = -scene.shadowRays/2; j < (scene.shadowRays/2); j++) {
                double t = (i+r.nextDouble())*this.radius/scene.shadowRays;
                double s = (j+r.nextDouble())*this.radius/scene.shadowRays;
                originPoints[i][j] = this.position.add(u.mul(t)).add(v.mul(s));
            }
        }

        // Test collisions
        double totalCollisions = 0;
        for (int i = 0; i < scene.shadowRays; i++) {
            for (int j = 0; j < scene.shadowRays; j++) {
                Ray lightRay = new Ray(originPoints[i][j], point);
                Optional<SimpleImmutableEntry<Surface, Vector>> closestCollision = lightRay.closestCollision(scene);
                if (closestCollision.isPresent()) {
                    if (point.equals(closestCollision.get().getValue())) {
                        totalCollisions += 1;
                    }
                }
            }
        }
        double lightIntensity = (1-this.shadowIntensity) + this.shadowIntensity*(totalCollisions / (scene.shadowRays*scene.shadowRays));
        return lightIntensity;
    }

    /**
     * Calculates the RGB value of the light multiplier at a given point
     * @param point the given point
     * @param s The relevant scene
     * @param specular Whether or not to calculate specular light; if true, multiplies each light by its' specular intensity
     * @return The diffuse color multiplier.
     */
    public static Vector LightAtPoint(Vector point, Scene s, boolean specular) {
        Vector ret = new Vector(1, 1, 1);
        double brightness = 1;
        for (Light l: s.lights) {
            // Calculate brightness of light at point.
            // brightness = dot(N, L) where N is the normal to the surface at point and L is the vector to the light
            Ray rayToPoint = new Ray(l.position, point);
            Optional<SimpleImmutableEntry<Surface, Vector>> collision = rayToPoint.closestCollision(s);
            if (collision.isPresent()) {
                Surface obj = collision.get().getKey();
                Vector collisionPoint = collision.get().getValue();
                if (point.equals(collisionPoint)) {
                    Optional<SimpleImmutableEntry<Vector, Vector>> intersection = obj.intersection(rayToPoint);
                    if (intersection.isPresent()) {
                        brightness = (intersection.get().getKey().add(l.position)).dot(intersection.get().getValue());
                        if (specular) {
                            brightness = Math.pow(brightness, obj.material.phong);
                        }
                    }
                }
            }
            if (specular) {
                ret = ret.pointMult(l.color.mul(l.lightIntensity(point, s)).mul(l.specularIntensity).mul(brightness));
            } else {
                ret = ret.pointMult(l.color.mul(l.lightIntensity(point, s)).mul(brightness));
            }
            
        }
        return ret;
    }
    
}
