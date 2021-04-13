package RayTracing;

import java.util.Random;

import RayTracing.Ray;
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
        // TODO
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
                if (lightRay.closestCollision(scene).isPresent()) {
                    if (point.equals(lightRay.closestCollision(scene).get().getValue())) {
                        totalCollisions += 1;
                    }
                }
            }
        }
        double lightIntensity = (1-this.shadowIntensity) + this.shadowIntensity*(totalCollisions / (scene.shadowRays*scene.shadowRays));
        return lightIntensity;
    }
    
}
