import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import edu.usu.graphics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Game {

    private final Graphics2D graphics;
    private Ship player_ship;
    private double gameStartTime = -1;
    private final double transitionDelay = 3.0;
    private enum gameStates{
        menu,
        game,
        scores,
        options,
        credits,
        transition,
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

    private Serializer serializer;
    private boolean pause;

    //Draw boarder
    final float screen_size = 1.2f;
    final float screen_height = 1.0f;
    final float FRAMETHICKNESS = 0.005f;
    private static final float LHS = -0.6f;
    private static final float TOP = -0.5f;
    private float scrollOffset; // Initial position, will move up over time
    private float scrollSpeed; // Adjust speed for smooth scrolling
    private Texture background;

    private SoundManager audio;
    private Sound level_music;
    private Sound countdown_sound;
    private HighScores highScores;
    private int score;
    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {

        scrollOffset = 0; // Initial position, will move up over time
        scrollSpeed = 0.05f; // Adjust speed for smooth scrolling

        highScores = new HighScores();
        serializer = new Serializer();
        serializer.loadHighScores(highScores);

        current_state = gameStates.menu;





        audio = new SoundManager();

        level_music = audio.load("level_music", "resources/audio/Outer Space - Super Paper Mario.ogg", false);
        countdown_sound = audio.load("countdown", "resources/audio/countdown-sound-effect.ogg", false);
        level_music.setGain(.5f);
        player_ship = new Ship(audio,score);
        menu = new Menu(audio);

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

        input.registerCommand(GLFW_KEY_LEFT,false,(double elapsedTime) -> {
            player_ship.rotateLeft(elapsedTime);

        });
        input.registerCommand(GLFW_KEY_RIGHT,false,(double elapsedTime) -> {
            player_ship.rotateRight(elapsedTime);

        });
        input.registerCommand(GLFW_KEY_UP,false,(double elapsedTime) -> {
            player_ship.thrust(elapsedTime);

        });
        input.registerCommand(GLFW_KEY_ESCAPE,true,(double elapsedTime) -> {
            pause = !pause;
            if(pause){
                level_music.pause();
            } else{
                level_music.play();
            }

        });

        //FOR DEBUGGING TERRAIN GENERATION
        //input.registerCommand(GLFW_KEY_SPACE,true,(double elapsedTime) -> {
        //    level = new Level();
        //});


        //different game states use different controls
        menu_input.registerCommand(GLFW_KEY_UP,true,(double elapsedTime) -> {

            if(current_state == gameStates.menu) menu.upOption();;
            if(current_state == gameStates.game && pause) menu.pauseOptionChange();

        });

        menu_input.registerCommand(GLFW_KEY_DOWN,true,(double elapsedTime) -> {
            if(current_state == gameStates.menu) menu.downOption();
            if(current_state == gameStates.game && pause) menu.pauseOptionChange();

        });


        menu_input.registerCommand(GLFW_KEY_ESCAPE,true,(double elapsedTime) -> {
            if (current_state == gameStates.menu){
                serializer.shutdown();
                glfwSetWindowShouldClose(graphics.getWindow(), true);
            } else if (current_state == gameStates.game){
                //pause = !pause;

            }
            else current_state = gameStates.menu;

        });

        menu_input.registerCommand(GLFW_KEY_ENTER,true,(double elapsedTime)->{
            if(current_state.equals(gameStates.menu)){
                String change_to = menu.getHovering();
                switch(change_to){
                    case ("Start Game"):
                        level = new Level();
                        score = 0;
                        pause = false;
                        current_state = gameStates.transition;

                        break;
                    case ("View High Scores"):

                        serializer.loadHighScores(highScores);
                        current_state = gameStates.scores;

                        break;
                    case ("Credits"):
                        scrollOffset = 0; // Initial position, will move up over time
                        current_state = gameStates.credits;

                        break;
                    case ("Customize Controls"):
                        current_state = gameStates.options;

                        break;
                    case ("Continue"):
                        pause = !pause;
                        break;
                    case ("Quit"):
                        current_state = gameStates.menu;

                        break;
                }
            }
            if(current_state.equals(gameStates.game) && pause){
                String change_to = menu.pauseHovering();
                switch(change_to){
                    case ("Continue"):
                        pause = false;
                        if(!level_music.isPlaying()) level_music.play();
                        break;
                    case ("Quit"):
                        current_state = gameStates.menu;
                        break;
                }
            }

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

        if(current_state == gameStates.game && !pause) input.update(elapsedTime,graphics);

        if (current_state != gameStates.game && current_state != gameStates.transition){
            menu_input.update(elapsedTime,graphics);
        } else if(current_state == gameStates.game && pause){
            menu_input.update(elapsedTime,graphics);
        }

    }

    private void update(double elapsedTime) {

        if (gameStartTime != -1) { // Check if timer has started
            double currentTime = glfwGetTime();
            if (currentTime - gameStartTime >= transitionDelay) {

                switch (current_state){
                    case transition:
                            current_state = gameStates.game;
                            if(!level_music.isPlaying()){level_music.play();}
                            player_ship = new Ship(audio,score);

                            break;

                        case game:
                            if(player_ship.getCrash()){
                                current_state = gameStates.menu;
                                if(player_ship.getScore() > 0){
                                    highScores.addScore(new Score(score));
                                    serializer.saveHighScores(highScores);
                                }


                            }
                            else{
                                if(player_ship.getLanded()){

                                    score = player_ship.getScore();

                                    if(level.getSafe_spaces() <= 1){

                                        if(level_music.isPlaying()){level_music.stop();}

                                        highScores.addScore(new Score(score));
                                        serializer.saveHighScores(highScores);

                                        current_state = gameStates.menu;
                                    } else{

                                        level.regenerate();
                                        current_state = gameStates.transition;
                                        break;
                                    }

                                }
                            }
                }

                gameStartTime = -1; // Reset timer
            }
        }

        switch (current_state) {
            case menu:
                // Code to execute if expression equals value1
                break;
            case game:
                // Code to execute if expression equals value2

                //If paused hold on updating
                if(!pause){

                    player_ship.updateShip(elapsedTime,level_music);

                    double[] terrain = level.getTerrain();
                    double[] safe_spaces = level.getSafe_zones();
                    double width = level.getWidth();
                    float offset = level.getOffset();

                    for (int i = 0; i < terrain.length-2;i++){
                        float x1 = (float) (((i * width) / (terrain.length - 1)) + offset);
                        float y1 = (float) terrain[i]; // Ensure values are properly scaled
                        float x2 = (float) ((((i+1) * width) / (terrain.length - 1)) + offset);
                        float y2 = (float) terrain[i+1]; // Ensure values are properly scaled
                        Vector2f point_1 = new Vector2f(x1,y1) ;
                        Vector2f point_2 = new Vector2f(x2,y2);

                        boolean safespace = 0 != safe_spaces[i] && 0 != safe_spaces[i + 1];

                        player_ship.checkCollisions(point_1,point_2,.015f,safespace,(int) safe_spaces[i]);

                    }

                }


                if (player_ship.getCrash() || player_ship.getLanded()){

                    if (gameStartTime == -1) { // Check if timer has started
                        gameStartTime = glfwGetTime();

                    }
                }

                break;
            case transition:
                if (gameStartTime == -1) { // Start timer only if it hasn't started
                    gameStartTime = glfwGetTime();
                    countdown_sound.play();
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
                // Move text up


                    scrollOffset -= scrollSpeed * elapsedTime; // Decrease offset over time




                break;
        }
    }

    private void render(double elapsedTime) {
        graphics.begin();

        Rectangle box = new Rectangle(LHS,TOP,screen_size,screen_height,-1);
        graphics.draw(background,box,Color.WHITE);


        //Draw boarder
        Rectangle line1 = new Rectangle(LHS,TOP,screen_size,FRAMETHICKNESS);
        Rectangle line2 = new Rectangle(LHS,-TOP,screen_size,FRAMETHICKNESS);
        Rectangle line3 = new Rectangle(-LHS,TOP,FRAMETHICKNESS,screen_height+FRAMETHICKNESS);
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

                if(pause){
                    for(String option: menu.getPauseOptions()){
                        if(Objects.equals(option, menu.pauseHovering())){
                            graphics.drawTextByHeight(sub_font, option, position_x, position_y, textHeight, Color.YELLOW);

                        }
                        else{
                            graphics.drawTextByHeight(sub_font, option, position_x, position_y, textHeight, Color.WHITE);
                        }

                        position_y = position_y + .075f;
                    }
                }

                break;
            case transition:
                graphics.drawTextByHeight(sub_font, "Starting in : "+String.format("%.2f",transitionDelay - (glfwGetTime() - gameStartTime)), title_position_x, title_position_y, title_textHeight, Color.WHITE);
                break;
            case options:
                graphics.drawTextByHeight(title_font, "OPTIONS", title_position_x, title_position_y, title_textHeight, Color.WHITE);

                break;
            case scores:
                graphics.drawTextByHeight(title_font, "SCORES", title_position_x, title_position_y, title_textHeight, Color.WHITE);
                if(this.highScores != null && this.highScores.initialized){

                    int temp = 0;
                    for(Score score : this.highScores.getScores()){
                        graphics.drawTextByHeight(sub_font, "Rank "+(temp+1)+" - "+score.score, position_x, position_y+(textHeight*(temp+1)), title_textHeight, Color.WHITE);
                        temp +=1;
                    }

                } else graphics.drawTextByHeight(sub_font, "No scores recorded...", position_x, position_y+textHeight, title_textHeight, Color.WHITE);


                // Code to execute if expression equals value2
                break;
            // ... more cases
            case credits:

                // Starting Y position (beginning from the bottom of the screen)
                float startY = screen_height-(screen_height/2); // Start slightly off-screen
                float yOffset = startY + scrollOffset; // Apply scroll effect
                textHeight = textHeight -.01f;
                // Draw title
                graphics.drawTextByHeight(title_font, "CREDITS", title_position_x, yOffset, textHeight, Color.WHITE);
                yOffset += textHeight * 2; // Extra space after title

                // Credits data
                String[][] credits = {
                        {"Dev", "Logan Liddiard"},
                        {"Background", "ChatGPT"},
                        {"Sound Effects", "Pixabay.com"},
                        {"Ship Sprite", "Galaga - NAMCO - Freepik.com"},
                        {"Music", "Title: Outer Space - Super Paper Mario"},
                        {"Composers", "Naoko Mitome, Chika Sekigawa, Yasuhisa Baba"},
                        {"Code Contributors", "Dean Mathias & ChatGPT"},
                        {"Special Thanks", "Dean Mathias & ChatGPT"}
                };

                float sectionSpacing = textHeight * 1.5f; // Spacing between sections

                for (String[] credit : credits) {
                    // Only draw if text is inside the screen bounds
                    if (yOffset + textHeight > (-screen_height / 2)+textHeight*2 && yOffset < (screen_height / 2)-textHeight*2) {
                        graphics.drawTextByHeight(sub_font, credit[0], position_x, yOffset, textHeight, Color.WHITE);
                        yOffset += textHeight;
                        graphics.drawTextByHeight(sub_font, "\t" + credit[1], position_x, yOffset, textHeight, Color.WHITE);
                        yOffset += sectionSpacing;
                    } else {
                        yOffset += textHeight +sectionSpacing; // Still update position, even if not drawn
                    }
                }


                break;
        }


        graphics.end();
    }
}
