
import edu.usu.graphics.*;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Random;

public class Level {
    private static final float CORNER_LEFT = -0.6f;
    private static final float WIDTH = 1.2f;  // Screen width
    private static final float HEIGHT = 1.0f; // Screen height
    private static final int NUM_POINTS = 257; // Must be 2^n + 1
    //private static final int NUM_POINTS = 17; // Must be 2^n + 1
    private static final double INITIAL_ROUGHNESS = .6; // Adjust for more or less jaggedness
    private double[] terrain = new double[NUM_POINTS]; // Store heights
    private double[] safe_zones = new double[NUM_POINTS];
    private int safe_spaces;

    public Level() {

        safe_spaces = 2;
        generateTerrain();
        addLandingZones();

    }

    public void regenerate() {

        Arrays.fill(safe_zones, 0);

        safe_spaces -= 1;
        generateTerrain();
        addLandingZones();

    }

    private static double gaussianRandom(Random random) {
        return random.nextGaussian();
    }

    private void generateTerrain() {
        Random random = new Random();

        // Set initial endpoints
        terrain[0] = Math.max(-0.20f, Math.min(0.35f, gaussianRandom(random) +.5 ));
        terrain[NUM_POINTS - 1] = Math.max(-0.20f, Math.min(0.35f, gaussianRandom(random) +.5 ));

        midpointDisplacement(0, NUM_POINTS - 1, INITIAL_ROUGHNESS, random);
    }

    private void midpointDisplacement(int left, int right, double roughness, Random random) {

        if (right - left <= 1) {

            return;
        }

        int mid_point = (left + right) / 2;
        //if(safe_space[mid_point]){System.out.println("OVERLAPPING SAFESPACE" );}


        double average = (terrain[left] + terrain[right]) / 2;

        double range = Math.abs(right - left) / (double) NUM_POINTS;
        double r_val = roughness * gaussianRandom(random) * range;

        terrain[mid_point] = Math.max(-0.1f, Math.min(0.5f, average + r_val ));
        //System.out.println(terrain[mid_point] + "--");

        double newRoughness = roughness * .95;

        midpointDisplacement(left, mid_point, newRoughness, random);
        midpointDisplacement(mid_point, right, newRoughness, random);
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

            Triangle triangle = new Triangle(start, end, bottom);
            graphics.draw(triangle, Color.WHITE);

            if (i != safe_zones.length - 1) {
                if (safe_zones[i] == 0 && safe_zones[i + 1] == 1
                        || safe_zones[i] == 0 && safe_zones[i + 1] == 2
                        || safe_zones[i] == 1 && safe_zones[i + 1] == 2) {
                    graphics.draw(start, end, Color.GREEN);

                    // Calculate flag position (midpoint of safe zone)
                    float flag_x = (x1 + x2) / 2;
                    float flag_y = y1 - 0.05f; // Slightly above the landing zone

                    // Draw flagpole (a small vertical line)
                    Vector3f pole_bottom = new Vector3f(flag_x, y1, 0.0f);
                    Vector3f pole_top = new Vector3f(flag_x, flag_y, 0.0f);
                    graphics.draw(pole_bottom, pole_top, Color.WHITE);

                    // Draw flag (a small right triangle)
                    Vector3f flag_tip = new Vector3f(flag_x + 0.03f, flag_y, 0.0f);
                    Vector3f flag_base = new Vector3f(flag_x, flag_y - 0.01f, 0.0f);
                    Triangle flag = new Triangle(pole_top, flag_tip, flag_base);
                    graphics.draw(flag, Color.RED);
                }
            }
            graphics.draw(start, end, Color.BLACK);

            Vector3f under_start = new Vector3f(x2, y2, 0.0f);
            Vector3f under_end = new Vector3f(x2, .5f, 0.0f);
            Vector3f under_bottom = new Vector3f(x1, 0.5f, 0.0f);

            triangle = new Triangle(under_start, under_end, under_bottom);
            graphics.draw(triangle, Color.WHITE);
        }
    }
    private void addLandingZones() {
        int numZones = this.safe_spaces; // Number of flat landing zones
        //System.out.println("Landing zones to be spawned: " + numZones);

        int zoneWidth = (numZones != 2) ? NUM_POINTS / 16 : NUM_POINTS / 8;

        double lowerBound = 0.15 * NUM_POINTS;  // Ensure zones don't spawn too close to edges
        double upperBound = 0.85 * NUM_POINTS;

        Random random = new Random();
        int[] landingStarts = new int[numZones]; // Track landing zone start points
        Arrays.fill(landingStarts, -1); // Initialize with an invalid position

        for (int i = 0; i < numZones; i++) {
            boolean validZone = false;
            int start = 0;

            while (!validZone) {
                start = (int) (lowerBound + random.nextDouble() * (upperBound - lowerBound - zoneWidth));

                validZone = true;

                // Check if this landing zone is too close to an existing one
                for (int existingStart : landingStarts) {
                    if (existingStart != -1 && Math.abs(start - existingStart) < zoneWidth * 2) {
                        validZone = false;
                        break;
                    }
                }
            }

            // Save this landing zone start position
            landingStarts[i] = start;

            // Flatten terrain at the landing zone
            for (int j = 0; j < zoneWidth; j++) {
                terrain[start + j] = terrain[start]; // Flatten segment
                safe_zones[start + j] = i + 1;
            }

            // Reduce zone width for the next landing zone (but not too small)
            zoneWidth = Math.max(zoneWidth / 2, NUM_POINTS / 32);
        }
    }


    public double[] getTerrain(){

        return terrain;

    }
    public float getWidth(){

        return WIDTH;

    }

    public float getOffset(){

        return CORNER_LEFT;
    }
    public int getSafe_spaces(){
        return safe_spaces;
    }
    public double[] getSafe_zones(){
        return safe_zones;
    }
}