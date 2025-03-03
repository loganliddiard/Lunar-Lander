import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScores {
    private static final int MAX_SCORES = 5;
    private List<Scores> highScores;
    private Serializer serializer;

    public HighScores() {
        highScores = new ArrayList<>();
        serializer = new Serializer();
        loadScores();
    }

    // Add a new score while keeping only the top 5
    public void addScore(int score) {
        highScores.add(new Scores(score));
        highScores.sort(Comparator.comparingInt(s -> -s.score)); // Sort in descending order
        if (highScores.size() > MAX_SCORES) {
            highScores.remove(highScores.size() - 1); // Keep only top 5
        }
        saveScores();
    }

    // Save the scores using Serializer
    private void saveScores() {
        serializer.saveGameState(new ScoresContainer(highScores));
    }

    // Load scores from file
    private void loadScores() {
        ScoresContainer loadedScores = new ScoresContainer();
        serializer.loadScores(loadedScores);
        if (loadedScores.scores != null) {
            highScores = loadedScores.scores;
        }
    }

    public List<Scores> getHighScores() {
        return highScores;
    }

    // Wrapper class for serialization
    private static class ScoresContainer extends Scores {
        public List<Scores> scores;

        public ScoresContainer() {
            this.scores = new ArrayList<>();
        }

        public ScoresContainer(List<Scores> scores) {
            this.scores = scores;
        }
    }
}