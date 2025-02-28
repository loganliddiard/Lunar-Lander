import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;

public class Menu {

    String[] options;

    private int choice;
    public String start_game;
    public String high_scores;
    public String controls;
    public String credits;
    private SoundManager audio;
    private Sound blip;
    private Sound countdown;
    public Menu(SoundManager audio){
        choice = 0;

        start_game = "Start Game";
        high_scores = "View High Scores";
        String controls = "Customize Controls";
        String credits = "Credits";

        this.audio = audio;
        blip = audio.load("blip", "resources/audio/retro-blip.ogg", false);
        countdown = audio.load("timer", "resources/audio/countdown-sound-effect.ogg", false);

        options = new String[] {start_game, high_scores, controls,credits};


    }

    public void upOption(){
        if (choice > 0){
            choice -= 1;
            blip.play();
        }
    }
    public void downOption(){
        if (choice < options.length-1){
            choice += 1;
            blip.play();
        }
    }

    public String getHovering(){
        return options[choice];
    }

    public String[] getOptions(){
        return options;
    }



}
