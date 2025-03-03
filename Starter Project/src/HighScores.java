import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores {

    private static final int MAX_SCORES = 5;
    public List<Score> scores = new ArrayList<>();
    public boolean initialized = false;

    public HighScores() {
        initialized = false;
    }

    public void addScore(Score score) {
        scores.add(score);
        scores.sort((s1, s2) -> Integer.compare(s2.score, s1.score)); // Sort descending
        if (scores.size() > MAX_SCORES) {
            scores.remove(scores.size() - 1); // Keep only top 5
        }
        initialized = true;
    }

    public List<Score> getScores() {
        return Collections.unmodifiableList(scores);
    }
}
