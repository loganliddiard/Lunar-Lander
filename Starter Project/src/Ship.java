import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Ship {
    private double angle;
    private Vector2f velocity;
    private double fuel;

    private float turn_angle;
    private float x;
    private float y;
    private Vector2f position;
    private Vector2f offset;
    private Rectangle ship;
    private float gravity;

    private float lengths;

    private boolean landed;
    private boolean crashed;

    public Ship(){

        turn_angle = 0.5f;
        angle = 90f;
        x = -0.0f;
        y = -0.40f;
        velocity = new Vector2f(0.0f, 0.0f);
        fuel = 100;

        lengths = -.03f;
        position = new Vector2f(x, y);
        offset = new Vector2f(x-(lengths/2), y+(lengths/2));

        gravity = 0.0000025f;
    }

    public void thrust() {
        if (fuel > 0 && !crashed && !landed) {
            fuel -= 0.1; // Consume some fuel

            double radians = Math.toRadians(angle);

            float thrustPower = 0.00002f; // Adjust thrust power for balance

            // Update velocity based on ship's direction
            velocity.x += (float) Math.cos(radians) * thrustPower;
            velocity.y += (float) Math.sin(radians) * thrustPower;
        }
    }

    public void rotateRight(){

        if (fuel > 0 && !crashed && !landed){
            fuel -= .01;
            angle -= turn_angle;
            if (angle < 0){

                angle += 360;
            }

        }
    }
    public void rotateLeft(){
        if (fuel > 0 && !crashed && !landed){
            fuel -= .01;
            angle += turn_angle;
            if (angle > 360){

                angle = angle % 360;
            }

        }
    }

    public void renderShip(Graphics2D graphics) {
        ship = new Rectangle(position.x, position.y, lengths, lengths);
        double radians = Math.toRadians(angle);

        // Compute the true center of the ship for rotation
        Vector2f center = new Vector2f(position.x + (lengths / 2), position.y + (lengths / 2));

        // Rotate around the correct center
        graphics.draw(ship, (float) radians, center, Color.BLUE);
    }



    public void updateShip(double elapsedTime){

        if(!crashed && !landed){
            for(var i = 0; i < elapsedTime; i++){

                velocity.y += gravity;

                position.x += velocity.x;
                position.y += velocity.y;

                System.out.println(position.y);

            }
        }


        checkCollisions();


    }


}
