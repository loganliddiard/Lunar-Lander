import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class ParticleSystem {
    private ArrayList<Particle> particles;
    private Random random;

    public ParticleSystem() {
        particles = new ArrayList<>();
        random = new Random();
    }

    public void generateParticles(Vector2f position, int count, Vector2f velocityRange, float lifetimeRange) {
        for (int i = 0; i < count; i++) {
            Vector2f velocity = new Vector2f(
                    (random.nextFloat() - 0.5f) * velocityRange.x,
                    (random.nextFloat() - 0.5f) * velocityRange.y
            );
            float lifetime = random.nextFloat() * lifetimeRange + 0.5f;
            particles.add(new Particle(position, velocity, lifetime));
        }
    }

    public void update(float deltaTime) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update(deltaTime);
            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }

    public void render(Graphics2D graphics) {

        Color[] choice = {Color.WHITE,Color.YELLOW,Color.RED};
        for (Particle particle : particles) {
            Rectangle rec = new Rectangle(particle.position.x,particle.position.y,.0025f,.0025f);
            graphics.draw(rec, choice[random.nextInt(choice.length)]);

        }
    }
}