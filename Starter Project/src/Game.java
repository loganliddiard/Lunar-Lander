import edu.usu.graphics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Game {

    private final Graphics2D graphics;
    private Ship player_ship;

    private enum gameStates{
        menu,
        game,
        scores,
        options,
        credits,
        pause,
    };

    private Level level;
    private Font font;
    private Font title_font;
    private Font sub_font;

    private KeyboardInput input;
    private KeyboardInput menu_input;
    private gameStates current_state;
    private Menu menu;

    private boolean pause;

    //Draw boarder
    final float screen_size = 1.2f;
    final float screen_height = 1.0f;
    final float FRAMETHICKNESS = 0.005f;
    private static final float LHS = -0.6f;
    private static final float TOP = -0.5f;

    private Texture background;


    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        player_ship = new Ship();
        current_state = gameStates.menu;

        menu = new Menu();
        input = new KeyboardInput();
        menu_input = new KeyboardInput();
        pause = false;

        level = new Level();

        font = new Font("Arial", java.awt.Font.PLAIN, 84, false);

        background = new Texture("resources/images/galaxy.png");

        // Key size is 36
        // Best height is .05
        title_font = new Font("resources/fonts/karmatic-arcade/ka1.ttf", 36, true);

        //max size is 60
        //needs to be size 64 with font height of .07
        sub_font = new Font("resources/fonts/dedicool/Dedicool.ttf", 64, true);
        input.registerCommand(GLFW_KEY_SPACE,false,(double elapsedTime) -> {
            System.out.println("UP KEY PRESSED");
            player_ship = new Ship();
            level = new Level();
        });
        input.registerCommand(GLFW_KEY_LEFT,false,(double elapsedTime) -> {
            player_ship.rotateLeft();

        });
        input.registerCommand(GLFW_KEY_RIGHT,false,(double elapsedTime) -> {
            player_ship.rotateRight();


        });
        input.registerCommand(GLFW_KEY_UP,false,(double elapsedTime) -> {
            player_ship.thrust();

        });
        input.registerCommand(GLFW_KEY_ESCAPE,true,(double elapsedTime) -> {
            pause = !pause;

        });


        //different game states use different controls
        menu_input.registerCommand(GLFW_KEY_UP,true,(double elapsedTime) -> {
            System.out.println("UP KEY PRESSED");

            menu.upOption();


        });

        menu_input.registerCommand(GLFW_KEY_DOWN,true,(double elapsedTime) -> {
            System.out.println("DOWN KEY PRESSED");
            menu.downOption();
        });
        menu_input.registerCommand(GLFW_KEY_RIGHT,true,(double elapsedTime) -> {
            System.out.println("RIGHT KEY PRESSED");
        });
        menu_input.registerCommand(GLFW_KEY_LEFT,true,(double elapsedTime) -> {
            System.out.println("LEFT KEY PRESSED");
        });

        menu_input.registerCommand(GLFW_KEY_ESCAPE,true,(double elapsedTime) -> {
            if (current_state == gameStates.menu){
                glfwSetWindowShouldClose(graphics.getWindow(), true);
            } else if (current_state == gameStates.game){
                //pause = !pause;

            }
            else current_state = gameStates.menu;

        });

    }

    public void shutdown() {
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();

        input.update(elapsedTime,graphics);
        if (current_state != gameStates.game){
            menu_input.update(elapsedTime,graphics);

        } else{
            menu_input.update(elapsedTime,graphics);
        }
        // If user presses ESC, then exit the program
        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ENTER) == GLFW_PRESS){

            if(current_state.equals(gameStates.menu)){
                String change_to = menu.getHovering();
                switch(change_to){
                    case ("Start Game"):
                        current_state = gameStates.game;

                    break;
                    case ("View High Scores"):
                        current_state = gameStates.scores;

                        break;
                    case ("Credits"):
                        current_state = gameStates.credits;

                        break;
                    case ("Customize Controls"):
                        current_state = gameStates.options;

                        break;
                }

            }
            System.out.println("Enter was pressed");

        }



    }

    private void update(double elapsedTime) {

        switch (current_state) {
            case menu:
                // Code to execute if expression equals value1
                break;
            case game:
                // Code to execute if expression equals value2

                //If paused hold on updating
                if(!pause){
                    player_ship.updateShip(elapsedTime);

                    double[] terrain = level.getTerrain();
                    double width = level.getWidth();
                    float offset = level.getOffset();

                    for (int i = 0; i < terrain.length-2;i++){
                        float x1 = (float) (((i * width) / (terrain.length - 1)) + offset);
                        float y1 = (float) terrain[i]; // Ensure values are properly scaled
                        float x2 = (float) ((((i+1) * width) / (terrain.length - 1)) + offset);
                        float y2 = (float) terrain[i+1]; // Ensure values are properly scaled
                        Vector2f point_1 = new Vector2f(x1,y1) ;
                        Vector2f point_2 = new Vector2f(x2,y2);

                        boolean safespace = y1 == y2;

                        player_ship.checkCollisions(point_1,point_2,.015f,safespace);
                    }

                }


                break;
            case options:
                // Code to execute if expression equals value3
                break;
            case scores:
                // Code to execute if expression equals value4
                break;
            // ... more cases
            case credits:
                break;
        }
    }

    private void render(double elapsedTime) {
        graphics.begin();



        //Draw boarder
        Rectangle line1 = new Rectangle(LHS,TOP,screen_size,FRAMETHICKNESS);
        Rectangle line2 = new Rectangle(LHS,-TOP,screen_size,FRAMETHICKNESS);
        Rectangle line3 = new Rectangle(-LHS,TOP,FRAMETHICKNESS,screen_height);
        Rectangle line4 = new Rectangle(LHS,TOP,FRAMETHICKNESS,screen_height);

        Color frame_color = Color.WHITE;

        graphics.draw(line1,frame_color);
        graphics.draw(line2,frame_color);
        graphics.draw(line3,frame_color);
        graphics.draw(line4,frame_color);

        float title_position_y = -0.35f;
        float title_position_x = -0.5f;
        float title_textHeight = .05f;

        float position_x = -0.5f;
        float position_y = -0.15f;
        float textHeight = .045f;



        switch (current_state) {
            case menu:

                graphics.drawTextByHeight(title_font, "LUNAR LANDER", title_position_x, title_position_y, title_textHeight, Color.WHITE);


                for(String option: menu.getOptions()){
                    if(Objects.equals(option, menu.getHovering())){
                        graphics.drawTextByHeight(sub_font, option, position_x, position_y, textHeight, Color.YELLOW);

                    }
                    else{
                        graphics.drawTextByHeight(sub_font, option, position_x, position_y, textHeight, Color.WHITE);
                    }

                    position_y = position_y + .075f;
                }


                break;
            case game:

                level.render_level(graphics);
                player_ship.renderShip(graphics);
                player_ship.renderShipHUD(graphics,sub_font);
                // Code to execute if expression equals value2


                break;
            case options:
                graphics.drawTextByHeight(title_font, "OPTIONS", position_x, position_y, title_textHeight, Color.WHITE);
                // Code to execute if expression equals value2
                break;
            case scores:
                graphics.drawTextByHeight(title_font, "SCORES", position_x, position_y, title_textHeight, Color.WHITE);

                // Code to execute if expression equals value2
                break;
            // ... more cases
            case credits:
                graphics.drawTextByHeight(title_font, "CREDITS", position_x, position_y, title_textHeight, Color.WHITE);
                break;
        }


        graphics.end();
    }
}
