import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;

public class Menu {

    String[] options;
    String[] pause_options;
    private int choice;
    private int pause_choice;
    public String start_game;
    public String high_scores;
    public String controls;
    public String credits;
    public String quit;
    public String cont;

    private SoundManager audio;
    private Sound blip;
    private Sound countdown;
    public Menu(SoundManager audio){
        choice = 0;
        pause_choice = 0;
        start_game = "Start Game";
        high_scores = "View High Scores";
        controls = "Customize Controls";
        credits = "Credits";

        quit = "Quit";
        cont = "Continue";
        this.audio = audio;
        blip = audio.load("blip", "resources/audio/retro-blip.ogg", false);
        countdown = audio.load("timer", "resources/audio/countdown-sound-effect.ogg", false);

        options = new String[] {start_game, high_scores,credits};
        pause_options = new String[] {cont,quit};

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
    public void pauseOptionChange(){
        blip.play();
        System.out.println("Pause choice change");
        if (pause_choice == 1)pause_choice = 0;
        else pause_choice = 1;
    }

    public String getHovering(){
        return options[choice];
    }
    public String pauseHovering(){
        return pause_options[pause_choice];
    }

    public String[] getOptions(){
        return options;
    }
    public String[] getPauseOptions(){
        return pause_options;
    }



}
