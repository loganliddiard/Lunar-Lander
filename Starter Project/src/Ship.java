import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import edu.usu.graphics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.w3c.dom.Text;

import javax.swing.*;

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
    private int perfect_angle = 0;
    private int perfect_speed = 0;
    private double max_fuel;
    private enum fuel_status{
        full,
        medium,
        low
    }
    private int score = 0;
    private fuel_status status;
    private boolean landed;
    private boolean crashed;
    private SoundManager audio;
    private Sound crash_effect;
    private Sound win_effect;
    private Sound thrust_effect;
    private final float base_thrust;
    private boolean finished;
    private ParticleSystem particleSystem;
    private int landed_value;
    public Ship(SoundManager audio, int score){

        this.score = score;

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
        gravity = 	0.015f;
        base_thrust = .04f;


        particleSystem = new ParticleSystem();

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

            // Generate thrust particles
            Vector2f center = new Vector2f(position.x + (lengths / 2), position.y + (lengths / 2));

            Vector2f exhaustPosition = new Vector2f(center).sub(new Vector2f((float) Math.cos(radians) * 0.015f, (float) Math.sin(radians) * 0.015f));


            particleSystem.generateThrusterParticles(exhaustPosition, 5, 0.15f, 1.0f, radians);
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

        if(!crashed){
            ship = new Rectangle(position.x, position.y, lengths, lengths);
            double radians = Math.toRadians(angle-90);

            // Compute the true center of the ship for rotation
            Vector2f center = new Vector2f(position.x + (lengths / 2), position.y + (lengths / 2));

            // Rotate around the correct center
            graphics.draw(galaga,ship, (float) radians, center, Color.WHITE);
        }
        // Render particles
        particleSystem.render(graphics);
    }

    public void renderShipHUD(Graphics2D graphics, Font font) {



        String show_fuel = "Fuel  : "+String.format("%.2f", fuel) + "s";
        String show_angle= "Angle : "+String.format("%.2f",(angle+ 90) % 360);
        String show_speed= "Speed : "+String.format("%.2f",getSpeed())+" m/s";

        float start_x = .04f;
        float start_y = -.45f;
        float textHeight = .05f;

        switch (status){

            case full:
                graphics.drawTextByHeight(font,show_fuel,start_x,start_y,textHeight,Color.GREEN);
                break;
            case medium:
                graphics.drawTextByHeight(font,show_fuel,start_x,start_y,textHeight,Color.YELLOW);
                break;
            default:
                graphics.drawTextByHeight(font,show_fuel,start_x,start_y,textHeight,Color.WHITE);
                break;
        }
        if(getSpeed() <= 2){
            graphics.drawTextByHeight(font,show_speed,start_x,start_y+textHeight,textHeight,Color.GREEN);
        }
        else{
            graphics.drawTextByHeight(font,show_speed,start_x,start_y+textHeight,textHeight,Color.WHITE);
        }
        if(checkAngle()){
            graphics.drawTextByHeight(font,show_angle,start_x,start_y+textHeight+textHeight,textHeight,Color.GREEN);
        } else{
            graphics.drawTextByHeight(font,show_angle,start_x,start_y+textHeight+textHeight,textHeight,Color.WHITE);
        }

        if(crashed) graphics.drawTextByHeight(font,"CRASHED",start_x,start_y+textHeight+textHeight+textHeight,textHeight,Color.RED);
        if(landed && !crashed) graphics.drawTextByHeight(font,"LANDED",start_x,start_y+textHeight+textHeight+textHeight+textHeight,textHeight,Color.GREEN);
        if(score != 0) graphics.drawTextByHeight(font,"POINTS - "+score,start_x,start_y+textHeight+textHeight+textHeight+textHeight+textHeight,textHeight,Color.YELLOW);
    }



    public void updateShip(double elapsedTime,Sound level_music){

        particleSystem.update((float) elapsedTime);
        if(!crashed && !landed){

            if (fuel > max_fuel /2){
                status = fuel_status.full;
            } else if(fuel > max_fuel /4){
                status = fuel_status.medium;
            } else{ status = fuel_status.low;}



            velocity.y += gravity * (float) elapsedTime;

            position.x += velocity.x * (float) elapsedTime;;
            position.y += velocity.y * (float) elapsedTime;;


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

    public boolean checkCollisions(Vector2f pt1, Vector2f pt2, float circleRadius,boolean isSafeSpace,int value) {
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
                    landed_value = value;
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
            if (tempangle >= 358){
                perfect_angle = 1;
            } else perfect_angle = 0;
            return true;
        }
        if (tempangle >=0 && tempangle <=5){

            if (tempangle <= 1){
                perfect_angle = 1;
            } else perfect_angle = 0;

            return true;
        }

        return false;
    }

    private void shipCrash(){
        crash_effect.play();
        Vector2f center = new Vector2f(position.x + (lengths / 2), position.y + (lengths / 2));
        particleSystem.generateParticles(center, 50, new Vector2f(0.05f, 0.05f), 2.0f);

    }
    private void shipWin(){
        win_effect.play();
        score += fuel * 5;
        score +=  500 * landed_value;
        score += perfect_speed * 500;
        score += perfect_angle * 500;

    }

    private double getSpeed() {
        double speed = Math.abs(velocity.y)*75;

        if (speed < .5){
            perfect_speed = 1;
        }
        else {
            perfect_speed = 0;
        }

        return speed;
    }
    public boolean getCrash() {
        return crashed;
    }
    public boolean getLanded() {
        return landed;
    }
    public int getScore(){

        return score;
    }
}
