import java.util.Date;

public class Scores {

    public Scores() {
        initialized = false;
    }

    public Scores(int score) {
        this.score = score;
        this.timeStamp = new Date();
        this.initialized = true;
    }


    public int score;
    public Date timeStamp;
    public boolean initialized = false;
}
