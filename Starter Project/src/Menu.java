public class Menu {

    String[] options;

    private int choice;
    public String start_game;
    public String high_scores;
    public String controls;
    public String credits;

    public Menu(){
        choice = 0;

        start_game = "Start Game";
        high_scores = "View High Scores";
        String controls = "Customize Controls";
        String credits = "Credits";

        options = new String[] {start_game, high_scores, controls,credits};


    }

    public void upOption(){
        if (choice > 0){
            choice -= 1;
        }
    }
    public void downOption(){
        if (choice < options.length-1){
            choice += 1;
        }
    }

    public String getHovering(){
        return options[choice];
    }

    public String[] getOptions(){
        return options;
    }



}
