package RayTracing;

import RayTracing.Ray;
import RayTracing.Vector;
import jdk.tools.jlink.resources.jlink_ja;

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
        Vector[][] originPoints = new Vector[N][N]; // A collection of N^2 points from which we shoot rays at the target.
        
        for (int i = -N/2; i < (N/2); i++) {
            for (int j = -N/2; j < (N/2); j++) {
                double t = (i+Random.nextDouble())*r/N;
                double s = (j+Random.nextDouble())*r/N;
                originPoints[i][j] = this.position.clone().add(u.clone().mult(t)).add(v.clone().mult(s));
            }
        }

        // Test collisions
        double totalCollisions = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Ray lightRay = new Ray(originPoints[i][j], point);
                if (lightRay.closestCollision(scene).isPresent()) {
                    if (point.equals(lightRay.closestCollision(scene).get().getValue())) {
                        totalCollisions += 1;
                    }
                }
            }
        }
        double lightIntensity = (1-this.shadowIntensity) + this.shadowIntensity*(totalCollisions / (N*N));
        return lightIntensity;
    }
    
}
