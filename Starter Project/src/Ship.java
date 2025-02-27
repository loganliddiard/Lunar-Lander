import edu.usu.graphics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.w3c.dom.Text;

public class Ship {
    private double angle;
    private Vector2f velocity;
    private double fuel;

    private Texture galaga; //NAMC
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
        angle = 0f;

        x = -0.0f;
        y = -0.40f;
        velocity = new Vector2f(0.0f, 0.0f);
        fuel = 100;

        lengths = -.03f;
        position = new Vector2f(x, y);
        offset = new Vector2f(x-(lengths/2), y+(lengths/2));
        galaga = new Texture("resources/images/RedGalaga.png");
        gravity = 0.000025f;
    }

    public void thrust() {
        if (fuel > 0 && !crashed && !landed) {
            fuel -= 0.1; // Consume some fuel

            double radians = Math.toRadians(angle);

            float thrustPower = 0.0001f; // Adjust thrust power for balance

            // Update velocity based on ship's direction
            velocity.x += (float) Math.cos(radians) * thrustPower;
            velocity.y += (float) Math.sin(radians) * thrustPower;
        }
    }

    public void rotateRight(){

        if (fuel > 0 && !crashed && !landed){
            fuel -= .01;
            angle += turn_angle;
            if (angle > 360){

                angle = angle % 360;
            }

        }
    }
    public void rotateLeft(){
        if (fuel > 0 && !crashed && !landed){
            fuel -= .01;
            angle -= turn_angle;
            if (angle < 0){

                angle += 360;
            }

        }
    }

    public void renderShip(Graphics2D graphics) {
        ship = new Rectangle(position.x, position.y, lengths, lengths);
        double radians = Math.toRadians(angle-90);

        // Compute the true center of the ship for rotation
        Vector2f center = new Vector2f(position.x + (lengths / 2), position.y + (lengths / 2));

        // Rotate around the correct center
        graphics.draw(galaga,ship, (float) radians, center, Color.WHITE);

    }

    public void renderShipHUD(Graphics2D graphics, Font font) {



        String show_fuel = "Fuel  : "+fuel;
        String show_angle= "Angle : "+(angle+ 90) % 360;
        String show_speed= "Speed : "+getSpeed();

        float start_x = .04f;
        float start_y = -.45f;
        float textHeight = .05f;

        graphics.drawTextByHeight(font,show_fuel,start_x,start_y,textHeight,Color.PURPLE);
        graphics.drawTextByHeight(font,show_angle,start_x,start_y+textHeight,textHeight,Color.PURPLE);
        graphics.drawTextByHeight(font,show_speed,start_x,start_y+textHeight+textHeight,textHeight,Color.PURPLE);
        graphics.drawTextByHeight(font,show_speed,start_x,start_y+textHeight+textHeight,textHeight,Color.PURPLE);
        if(crashed) graphics.drawTextByHeight(font,"CRASHED",start_x,start_y+textHeight+textHeight+textHeight,textHeight,Color.RED);
        if(landed) graphics.drawTextByHeight(font,"LANDED",start_x,start_y+textHeight+textHeight+textHeight+textHeight,textHeight,Color.GREEN);
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
    }

    public boolean checkCollisions(Vector2f pt1, Vector2f pt2, float circleRadius,boolean isSafeSpace) {
        // Translate points to circle's coordinate system
        Vector2f d = new Vector2f(pt2).sub(pt1);
        Vector2f f = new Vector2f(pt1).sub(new Vector2f(position.x + (lengths / 2), position.y + (lengths / 2)));

        float a = d.dot(d);
        float b = 2 * f.dot(d);
        float c = f.dot(f) - circleRadius * circleRadius;

        float discriminant = b * b - 4 * a * c;

        // If the discriminant is negative, no real roots and thus no intersection
        if (discriminant < 0) {
            return false;
        }

        // Check if the intersection points are within the segment
        discriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - discriminant) / (2 * a);
        float t2 = (-b + discriminant) / (2 * a);

        if (t1 >= 0 && t1 <= 1) {
            if(!isSafeSpace){
                crashed = true;
            } else {

                if(checkAngle() && getSpeed() < 2){
                    landed = true;
                } else crashed = true;

            }
            return true;
        }
        if (t2 >= 0 && t2 <= 1) {
            if(!isSafeSpace){
                crashed = true;
            } else {

                if(checkAngle() && getSpeed() < 2){
                    landed = true;
                } else crashed = true;

            }
            return true;
        }

        return false;
    }

    private boolean checkAngle(){

        double tempangle = (angle+ 90) % 360;

        if( tempangle >=355 && tempangle <=360){
            return true;
        }
        if (tempangle >=0 && tempangle <=5){
            return true;
        }

        return false;
    }
    private double getSpeed() {
        return Math.abs(velocity.y)*1000;
    }
}
