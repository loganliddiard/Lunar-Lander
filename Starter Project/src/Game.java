import edu.usu.graphics.*;
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

    private KeyboardInput input;
    private KeyboardInput menu_input;
    private gameStates current_state;
    private Menu menu;

    private boolean pause;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        player_ship = new Ship();
        current_state = gameStates.game;

        menu = new Menu();
        input = new KeyboardInput();
        menu_input = new KeyboardInput();
        pause = false;

        level = new Level();

        font = new Font("Arial", java.awt.Font.PLAIN, 42, true);

        input.registerCommand(GLFW_KEY_SPACE,false,(double elapsedTime) -> {
            System.out.println("UP KEY PRESSED");

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
                pause = !pause;
                glfwSetWindowShouldClose(graphics.getWindow(), true);
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
            case gameStates.menu:
                // Code to execute if expression equals value1
                break;
            case gameStates.game:
                // Code to execute if expression equals value2

                player_ship.updateShip(elapsedTime);
                break;
            case gameStates.options:
                // Code to execute if expression equals value3
                break;
            case gameStates.scores:
                // Code to execute if expression equals value4
                break;
            // ... more cases
            case gameStates.credits:
                break;
        }
    }

    private void render(double elapsedTime) {
        graphics.begin();
        float position_x = -0.5f;
        float position_y = -0.25f;
        switch (current_state) {
            case gameStates.menu:
                // Code to execute if expression equals value1

                for(String option: menu.getOptions()){
                    if(Objects.equals(option, menu.getHovering())){
                        graphics.drawTextByHeight(font, option, position_x, position_y, 0.075f, Color.YELLOW);

                    }
                    else{
                        graphics.drawTextByHeight(font, option, position_x, position_y, 0.075f, Color.WHITE);
                    }

                    position_y = position_y + .075f;
                }


                break;
            case gameStates.game:
                graphics.drawTextByHeight(font, "GAME", position_x, position_y, 0.075f, Color.WHITE);

                level.render_level(graphics);
                player_ship.renderShip(graphics);
                // Code to execute if expression equals value2
                break;
            case gameStates.options:
                graphics.drawTextByHeight(font, "OPTIONS", position_x, position_y, 0.075f, Color.WHITE);
                // Code to execute if expression equals value2
                break;
            case gameStates.scores:
                graphics.drawTextByHeight(font, "SCORES", position_x, position_y, 0.075f, Color.WHITE);

                // Code to execute if expression equals value2
                break;
            // ... more cases
            case gameStates.credits:
                graphics.drawTextByHeight(font, "CREDITS", position_x, position_y, 0.075f, Color.WHITE);
                break;

        }


        graphics.end();
    }
}
