import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.FileReader;

public class Serializer implements Runnable {
    private enum Activity {
        Nothing,
        Load,
        Save
    }

    private boolean done = false;
    private final Lock lockSignal = new ReentrantLock();
    private final Condition doSomething = lockSignal.newCondition();
    private Activity doThis = Activity.Nothing;

    private HighScores highScores;

    private final Thread tInternal;
    private static final String FILE_NAME = "highscores.json";

    public Serializer() {
        this.tInternal = new Thread(this);
        this.tInternal.start();
    }

    @Override
    public void run() {
        try {
            while (!done) {
                // Wait for a signal to do something
                lockSignal.lock();
                doSomething.await();
                lockSignal.unlock();

                // Based on what was requested, do something
                switch (doThis) {
                    case Nothing -> {}
                    case Save -> saveSomething();
                    case Load -> loadSomething();
                }
            }
        } catch (Exception ex) {
            System.out.printf("Something bad happened: %s\n", ex.getMessage());
        }
    }

    /// Public method used by client code to request the high scores be saved
    public void saveHighScores(HighScores scores) {
        lockSignal.lock();

        this.highScores = scores;
        doThis = Activity.Save;
        doSomething.signal();

        lockSignal.unlock();
    }

    /// Public method used by client code to request high scores be loaded.
    public void loadHighScores(HighScores scores) {
        lockSignal.lock();

        this.highScores = scores;
        doThis = Activity.Load;
        doSomething.signal();

        lockSignal.unlock();
    }

    /// Public method used to signal this code to perform a graceful shutdown
    public void shutdown() {
        try {
            lockSignal.lock();

            doThis = Activity.Nothing;
            done = true;
            doSomething.signal();

            lockSignal.unlock();

            tInternal.join();
        } catch (Exception ex) {
            System.out.printf("Failure to gracefully shut down thread: %s\n", ex.getMessage());
        }
    }

    /// This is where the actual serialization of the high scores is performed.
    private synchronized void saveSomething() {
        System.out.println("Saving high scores...");
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            Gson gson = new Gson();
            gson.toJson(this.highScores, writer);
        } catch (Exception ex) {
            System.out.println("Error saving high scores: " + ex.getMessage());
        }
    }

    /// This is where the actual deserialization of the high scores is performed.
    private synchronized void loadSomething() {
        System.out.println("Loading high scores...");
        try (FileReader reader = new FileReader(FILE_NAME)) {
            HighScores loadedScores = (new Gson()).fromJson(reader, HighScores.class);
            this.highScores.scores = loadedScores.scores;
            this.highScores.initialized = true;
        } catch (Exception ex) {
            System.out.println("Error loading high scores: " + ex.getMessage());
        }
    }
}
