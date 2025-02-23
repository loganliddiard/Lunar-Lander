import edu.usu.graphics.Graphics2D;

import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;

//Dean provided code from Unit 4
//Thanks Dean!
public class KeyboardInput {
    // Record used to track which keys are registered with a command entry
    private record CommandEntry(int key, boolean keyPressOnly, ICommand callback) { }
    // Table of registered callbacks
    private final HashMap<Integer, CommandEntry> commandEntries = new HashMap<>();
    // Table of registered callback keys previous pressed state
    private final HashMap<Integer, Boolean> keysPressed = new HashMap<>();

    public KeyboardInput(){

    }

    public void registerCommand(int key, boolean keyPressOnly, ICommand callback) {
        commandEntries.put(key, new CommandEntry(key, keyPressOnly, callback));
    // Start out by assuming the key isn't currently pressed
        keysPressed.put(key, false);
    }

    private boolean isKeyNewlyPressed(int key, Graphics2D graphics) {
        return (glfwGetKey(graphics.getWindow(), key) == GLFW_PRESS) && !keysPressed.get(key);
    }

    public void update(double elapsedTime,Graphics2D graphics) {
        //glfwPollEvents();

        for (var entry : commandEntries.entrySet()) {
            if (entry.getValue().keyPressOnly && isKeyNewlyPressed(entry.getValue().key,graphics)) {
                entry.getValue().callback.invoke(elapsedTime); // User provided lambda
            } else if (!entry.getValue().keyPressOnly &&
                    glfwGetKey(graphics.getWindow(), entry.getKey()) == GLFW_PRESS) {
                entry.getValue().callback.invoke(elapsedTime); // User provided lambda
            }
// For the next time around, remember the
// current state of the key (pressed or not)
            keysPressed.put(entry.getKey(),
                    glfwGetKey(graphics.getWindow(), entry.getKey()) == GLFW_PRESS);
        }
    }

}
