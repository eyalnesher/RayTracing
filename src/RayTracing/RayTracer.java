package RayTracing;

import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Main class for ray tracing exercise.
 */
public class RayTracer {

	public int imageWidth;
	public int imageHeight;

	/**
	 * Runs the ray tracer. Takes scene file, output image file and image size as
	 * input.
	 */
	public static void main(String[] args) {

		try {

			RayTracer tracer = new RayTracer();

			// Default values:
			tracer.imageWidth = 500;
			tracer.imageHeight = 500;

			if (args.length < 2)
				throw new RayTracerException(
						"Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

			String sceneFileName = args[0];
			String outputFileName = args[1];

			if (args.length > 3) {
				tracer.imageWidth = Integer.parseInt(args[2]);
				tracer.imageHeight = Integer.parseInt(args[3]);
			}

			// Parse scene file:
			Scene scene = tracer.parseScene(sceneFileName);

			// Render scene:
			tracer.renderScene(scene, outputFileName);

			// } catch (IOException e) {
			// System.out.println(e.getMessage());
		} catch (RayTracerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Parses the scene file and creates the scene. Change this function so it
	 * generates the required objects.
	 */
	public Scene parseScene(String sceneFileName) throws IOException, RayTracerException {
		FileReader fr = new FileReader(sceneFileName);

		BufferedReader r = new BufferedReader(fr);
		String line = null;
		int lineNum = 0;
		System.out.println("Started parsing scene file " + sceneFileName);

		Camera cam = null;
		Scene scene = null;
		List<Material> materials = new ArrayList<>();
		while ((line = r.readLine()) != null) {
			line = line.trim();
			++lineNum;

			if (line.isEmpty() || (line.charAt(0) == '#')) { // This line in the scene file is a comment
				continue;
			} else {
				String code = line.substring(0, 3).toLowerCase();
				// Split according to white space characters:
				String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

				if (code.equals("cam")) {
					/*
					* Camera input format: 0:pos(x) 1:pos(y) 2:pos(z)
					*						3:lookat(x) 4:lookat(y) 5:lookat(z)
					*						6:up(x) 7:up(y) 8:up(z)
					*						9:screenDistance 10:screenWidth
					*						11:fisheye(optional) 12:fisheyeParam(optional)
					*/
					if (params.length >= 13) {
						cam = new Camera(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2])),
									new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5])),
									new Vector(Double.parseDouble(params[6]),Double.parseDouble(params[7]), Double.parseDouble(params[8])),
									Double.parseDouble(params[9]), Double.parseDouble(params[10]),
									Boolean.parseBoolean(params[11]),
									Double.parseDouble(params[12]));
					} else if (params.length == 12) {
						cam = new Camera(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2])),
									new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5])),
									new Vector(Double.parseDouble(params[6]),Double.parseDouble(params[7]), Double.parseDouble(params[8])),
									Double.parseDouble(params[9]), Double.parseDouble(params[10]),
									Boolean.parseBoolean(params[11]));
					} else {
						cam = new Camera(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2])),
									new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5])),
									new Vector(Double.parseDouble(params[6]),Double.parseDouble(params[7]), Double.parseDouble(params[8])),
									Double.parseDouble(params[9]), Double.parseDouble(params[10]));
					}
					
					System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
				} else if (code.equals("set")) {
					/*
					* Scene input format: 0:bgColor(r) 1:bgColor(g) 2:bgColor(b) 3:shadowRays 4:recursionDepth
					*/
					scene = new Scene(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2])),
									Integer.parseInt(params[3]), Integer.parseInt(params[4]),
									cam,
									new ArrayList<Surface>(), new ArrayList<Light>());
					System.out.println(String.format("Parsed general settings (line %d)", lineNum));
				} else if (code.equals("mtl")) {
					/*
					* Material input format: 0:diffuse(r) 1:diffuse(g) 2:diffuse(b) 
					*						3:specular(r) 4:specular(g) 5:specular(b)
					*						6:reflection(r) 7:reflection(g) 8:reflection(b)
					*						9:phong 10:transparency
					*/
						materials.add(new Material(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2])),
												new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5])),
												Double.parseDouble(params[9]),
												new Vector(Double.parseDouble(params[6]), Double.parseDouble(params[7]), Double.parseDouble(params[8])),
												Double.parseDouble(params[10])));

					System.out.println(String.format("Parsed material (line %d)", lineNum));
				} else if (code.equals("sph")) {
					/*
					* Sphere input format: 0:pos(x) 1:pos(y) 2:pos(z) 3:radius 4:mat_index
					*/
					scene.addObject(new Sphere(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2])),
									Double.parseDouble(params[3]),
									materials.get(Integer.parseInt(params[4]))));
					System.out.println(String.format("Parsed sphere (line %d)", lineNum));
				} else if (code.equals("pln")) {
					/*
					* Plane input format: 0:pos(x) 1:pos(y) 2:pos(z) 3:offset 4:mat_index
					*/
					scene.addObject(new Plane(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2])),
									Double.parseDouble(params[3]),
									materials.get(Integer.parseInt(params[4]))));
					System.out.println(String.format("Parsed plane (line %d)", lineNum));
				} else if (code.equals("lgt")) {
					/*
					* Light input format: 0:pos(x) 1:pos(y) 2:pos(z)
					*						3:r 4:g 5:b
					*						6:specular 7:shadow 8: radius
					*/
					scene.addLight(new Light(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2])),
									new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5])),
									Double.parseDouble(params[6]),
									Double.parseDouble(params[7]),
									Double.parseDouble(params[8])));
					System.out.println(String.format("Parsed light (line %d)", lineNum));
				} else if (code.equals("box")){
					/*
					* Box input format: 0:pos(x) 1:pos(y) 2:pos(z) 3:length 4:mat_index
					*/
					scene.addObject(new Box(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2])),
											Double.parseDouble(params[3]),
											materials.get(Integer.parseInt(params[4]))));
				}else {
					System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
				}
			}
		}

		// It is recommended that you check here that the scene is valid,
		// for example camera settings and all necessary materials were defined.

		System.out.println("Finished parsing scene file " + sceneFileName);
		return scene;
	}

	/**
	 * Renders the loaded scene and saves it to the specified file location.
	 */
	public void renderScene(Scene s, String outputFileName) {
		long startTime = System.currentTimeMillis();

		// Create a byte array to hold the pixel data:
		byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];

		// Put your ray tracing code here!
		//
		// Write pixel color values in RGB format to rgbData:
		// Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
		// green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
		// blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
		//
		// Each of the red, green and blue components should be a byte, i.e. 0-255

		long endTime = System.currentTimeMillis();
		Long renderTime = endTime - startTime;

		System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

		// This is already implemented, and should work without adding any code.
		saveImage(this.imageWidth, rgbData, outputFileName);

		System.out.println("Saved file " + outputFileName);

	}

	// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT

	/*
	 * Saves RGB data as an image in png format to the specified location.
	 */
	public static void saveImage(int width, byte[] rgbData, String fileName) {
		try {

			BufferedImage image = bytes2RGB(width, rgbData);
			ImageIO.write(image, "png", new File(fileName));

		} catch (IOException e) {
			System.out.println("ERROR SAVING FILE: " + e.getMessage());
		}

	}

	/*
	 * Producing a BufferedImage that can be saved as png from a byte array of RGB
	 * values.
	 */
	public static BufferedImage bytes2RGB(int width, byte[] buffer) {
		int height = buffer.length / width / 3;
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		DataBufferByte db = new DataBufferByte(buffer, width * height);
		WritableRaster raster = Raster.createWritableRaster(sm, db, null);
		BufferedImage result = new BufferedImage(cm, raster, false, null);

		return result;
	}

	public static class RayTracerException extends Exception {
		public RayTracerException(String msg) {
			super(msg);
		}
	}

}
