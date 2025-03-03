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
    private float sizeRange;
    public ParticleSystem() {
        particles = new ArrayList<>();
        random = new Random();
        sizeRange = .005f;
    }
    public void generateThrusterParticles(Vector2f position, int count, float speed, float lifetimeRange, double angle) {
        for (int i = 0; i < count; i++) {
            float particleAngle = (float) (angle + (random.nextFloat() - 0.5) * Math.PI / 3); // spread
            Vector2f velocity = new Vector2f(
                    (float) Math.cos(particleAngle) * -speed,
                    (float) Math.sin(particleAngle) * -speed
            );
            float lifetime = random.nextFloat() * lifetimeRange + 0.5f;
            float size = random.nextFloat() * sizeRange;

            particles.add(new Particle(position, velocity, lifetime, size));
        }
    }

    public void generateParticles(Vector2f position, int count, Vector2f velocityRange, float lifetimeRange) {
        for (int i = 0; i < count; i++) {
            Vector2f velocity = new Vector2f(
                    (random.nextFloat() - 0.5f) * velocityRange.x,
                    (random.nextFloat() - 0.5f) * velocityRange.y
            );
            float lifetime = random.nextFloat() * lifetimeRange + 0.5f;
            float size = random.nextFloat() * sizeRange;
            particles.add(new Particle(position, velocity, lifetime, size));
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
            Rectangle rec = new Rectangle(particle.position.x-(particle.size/2),particle.position.y-(particle.size/2),particle.size,particle.size);
            graphics.draw(rec, choice[random.nextInt(choice.length)]);

        }
    }
}