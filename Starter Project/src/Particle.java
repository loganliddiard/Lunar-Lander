import org.joml.Vector2f;

class Particle {
    Vector2f position;
    Vector2f velocity;
    float size;
    float lifetime;

    public Particle(Vector2f position, Vector2f velocity, float lifetime,float size) {
        this.position = new Vector2f(position);
        this.velocity = new Vector2f(velocity);
        this.lifetime = lifetime;
        this.size = size;
    }

    public void update(float deltaTime) {
        position.add(velocity.mul(deltaTime, new Vector2f()));
        lifetime -= deltaTime;
    }

    public boolean isAlive() {
        return lifetime > 0;
    }
}