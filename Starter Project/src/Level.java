
import edu.usu.graphics.*;
import org.joml.Vector3f;
import java.util.Random;

public class Level {
    private static final float CORNER_LEFT = -0.5f;
    private static final float WIDTH = 1.0f;  // Screen width
    private static final float HEIGHT = 1.0f; // Screen height
    private static final int NUM_POINTS = 257; // Must be 2^n + 1
    //private static final int NUM_POINTS = 17; // Must be 2^n + 1
    private static final double INITIAL_ROUGHNESS = 1; // Adjust for more or less jaggedness
    private double[] terrain = new double[NUM_POINTS]; // Store heights


    public Level() {
        generateTerrain();
        addLandingZones();

    }

    private static double gaussianRandom(Random random) {
        return random.nextGaussian(); // Mean 0, Variance 1
    }

    private void generateTerrain() {
        Random random = new Random();

        // Set initial endpoints
        terrain[0] = Math.max(-0.20f, Math.min(0.4f, gaussianRandom(random) +.5 ));
        terrain[NUM_POINTS - 1] = Math.max(-0.25f, Math.min(0.5f, gaussianRandom(random) +.5 ));


        // Perform midpoint displacement
        midpointDisplacement(0, NUM_POINTS - 1, INITIAL_ROUGHNESS, random);
    }

    private void midpointDisplacement(int left, int right, double roughness, Random random) {
        if (right - left <= 1) return;

        int mid = (left + right) / 2;
        double avg = (terrain[left] + terrain[right]) / 2.0;

        double range = Math.abs(right - left) / (double) NUM_POINTS;
        double r = roughness * gaussianRandom(random) * range;

        // Ensure y values stay within range



        terrain[mid] = Math.max(-0.25f, Math.min(0.5f, avg+r));
        System.out.println(terrain[mid]);

        // Reduce roughness dynamically for smoother transitions
        double newRoughness = roughness * 0.9; // Adjust decay factor as needed

        // Recursively apply midpoint displacement
        midpointDisplacement(left, mid, newRoughness, random);
        midpointDisplacement(mid, right, newRoughness, random);
    }

    public void render_level(Graphics2D graphics) {

        for (int i = 0; i < NUM_POINTS - 1; i++) {
            float x1 = ((i * WIDTH) / (NUM_POINTS - 1)) + CORNER_LEFT;
            float y1 = (float) terrain[i]; // Ensure values are properly scaled
            float x2 = (((i + 1) * WIDTH) / (NUM_POINTS - 1)) + CORNER_LEFT;
            float y2 = (float) terrain[i + 1];

            // Ensure y values stay within display range
            y1 = Math.max(-0.5f, Math.min(0.5f, y1));
            y2 = Math.max(-0.5f, Math.min(0.5f, y2));

            Vector3f start = new Vector3f(x1, y1, 0.0f);
            Vector3f end = new Vector3f(x2, y2, 0.0f);
            Vector3f bottom = new Vector3f(x1, 0.5f, 0.0f);

            Triangle triangle = new Triangle(start,end,bottom);
            graphics.draw(triangle, Color.WHITE);

            graphics.draw(start, end, Color.BLACK);

            Vector3f under_start = new Vector3f(x2, y2, 0.0f);
            Vector3f under_end = new Vector3f(x2, .5f, 0.0f);
            Vector3f under_bottom = new Vector3f(x1, 0.5f, 0.0f);

            triangle = new Triangle(under_start,under_end,under_bottom);
            graphics.draw(triangle, Color.WHITE);

        }
    }
    private void addLandingZones() {
        int numZones = 2; // Number of flat landing zones
        int zoneWidth = NUM_POINTS / 8; // Width of each zone

        Random random = new Random();
        for (int i = 0; i < numZones; i++) {
            int start = random.nextInt(NUM_POINTS - zoneWidth - 1);
            for (int j = 0; j < zoneWidth; j++) {
                terrain[start + j] = terrain[start]; // Flatten segment


            }
            zoneWidth = zoneWidth / 2;
        }
    }
}