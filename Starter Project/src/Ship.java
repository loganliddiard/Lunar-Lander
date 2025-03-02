import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
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
    private float fuel_usage;
    private float lengths;

    private double max_fuel;
    private enum fuel_status{
        full,
        medium,
        low
    }
    private fuel_status status;
    private boolean landed;
    private boolean crashed;
    private SoundManager audio;
    private Sound crash_effect;
    private Sound win_effect;
    private Sound thrust_effect;
    private final float base_thrust;
    private boolean finished;
    public Ship(SoundManager audio){


        crash_effect = audio.load("crash","resources/audio/retro-explode.ogg",false);
        win_effect = audio.load("win","resources/audio/8-bit-victory.ogg",false);
        thrust_effect = audio.load("thrust","resources/audio/thruster_noise.ogg",false);

        finished = false;

        turn_angle = 120f;
        angle = 0f;
        status = fuel_status.full;

        max_fuel = 20.0;
        x = -0.0f;
        y = -0.40f;
        velocity = new Vector2f(0.0f, 0.0f);
        fuel = 20.0;

        lengths = -.03f;
        position = new Vector2f(x, y);
        offset = new Vector2f(x-(lengths/2), y+(lengths/2));
        galaga = new Texture("resources/images/RedGalaga.png");
        gravity = 	0.000002f;
        base_thrust = .0006f;
    }

    public void thrust(double elapsedTime) {
        if (fuel > 0 && !crashed && !landed) {
            fuel -=  elapsedTime; // Scale fuel consumption

            if (fuel <= 0 || fuel > max_fuel + 1) {
                fuel = 0;
            }

            if (!thrust_effect.isPlaying()) {
                thrust_effect.play();
            }

            double radians = Math.toRadians(angle);
            float thrustPower = base_thrust * (float) elapsedTime; // Scale thrust by elapsed time

            // Update velocity based on ship's direction
            velocity.x += (float) Math.cos(radians) * thrustPower;
            velocity.y += (float) Math.sin(radians) * thrustPower;
        }
    }
    public void rotateRight(double elapsedTime) {
        if (fuel > 0 && !crashed && !landed) {
            fuel -= elapsedTime;
            angle += turn_angle * elapsedTime; // Adjust rotation based on elapsed time
            if (angle > 360) {
                angle = angle % 360;
            }
        }
    }
    public void rotateLeft(double elapsedTime) {
        if (fuel > 0 && !crashed && !landed) {
            fuel -= elapsedTime;
            angle -= turn_angle * elapsedTime; // Adjust rotation based on elapsed time
            if (angle < 0) {
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



        String show_fuel = "Fuel  : "+String.format("%.2f", fuel);
        String show_angle= "Angle : "+String.format("%.2f",(angle+ 90) % 360);
        String show_speed= "Speed : "+String.format("%.2f",getSpeed())+" m/s";

        float start_x = .04f;
        float start_y = -.45f;
        float textHeight = .05f;

        graphics.drawTextByHeight(font,show_fuel,start_x,start_y,textHeight,Color.WHITE);
        graphics.drawTextByHeight(font,show_angle,start_x,start_y+textHeight,textHeight,Color.WHITE);

        switch (status){

            case full:
                graphics.drawTextByHeight(font,show_fuel,start_x,start_y,textHeight,Color.GREEN);
                break;
            case medium:
                graphics.drawTextByHeight(font,show_fuel,start_x,start_y,textHeight,Color.YELLOW);
                break;
            default:
                graphics.drawTextByHeight(font,show_fuel,start_x,start_y,textHeight,Color.RED);
                break;
        }

        graphics.drawTextByHeight(font,show_speed,start_x,start_y+textHeight+textHeight,textHeight,Color.WHITE);
        graphics.drawTextByHeight(font,show_speed,start_x,start_y+textHeight+textHeight,textHeight,Color.WHITE);
        if(crashed) graphics.drawTextByHeight(font,"CRASHED",start_x,start_y+textHeight+textHeight+textHeight,textHeight,Color.RED);
        if(landed) graphics.drawTextByHeight(font,"LANDED",start_x,start_y+textHeight+textHeight+textHeight+textHeight,textHeight,Color.GREEN);
    }



    public void updateShip(double elapsedTime,Sound level_music){

        if(!crashed && !landed){

            if (fuel > max_fuel /2){
                status = fuel_status.full;
            } else if(fuel > max_fuel /4){
                status = fuel_status.medium;
            } else{ status = fuel_status.low;}

            for(var i = 0; i < elapsedTime; i++){

                velocity.y += gravity;

                position.x += velocity.x;
                position.y += velocity.y;

            }
        }
        else if (!finished&&crashed){
            finished = true;
            level_music.stop();
            shipCrash();

        }
        else if (!finished&&landed){
            finished = true;
            shipWin();

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

    private void shipCrash(){
        crash_effect.play();

    }
    private void shipWin(){
        win_effect.play();

    }

    private double getSpeed() {
        return Math.abs(velocity.y)*4000;
    }
    public boolean getCrash() {
        return crashed;
    }
    public boolean getLanded() {
        return landed;
    }
}
