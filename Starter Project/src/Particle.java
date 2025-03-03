import org.joml.Vector2f;

class Particle {
    Vector2f position;
    Vector2f velocity;
    float lifetime;

    public Particle(Vector2f position, Vector2f velocity, float lifetime) {
        this.position = new Vector2f(position);
        this.velocity = new Vector2f(velocity);
        this.lifetime = lifetime;
    }

    public void update(float deltaTime) {
        position.add(velocity.mul(deltaTime, new Vector2f()));
        lifetime -= deltaTime;
    }

    public boolean isAlive() {
        return lifetime > 0;
    }
}