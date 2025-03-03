import java.util.Date;

public class Score {

    public Score() {
        initialized = false;
    }

    public Score(int score) {
        this.score = score;
        this.timeStamp = new Date();
        this.initialized = true;
    }


    public int score;
    public Date timeStamp;
    public boolean initialized = false;
}
