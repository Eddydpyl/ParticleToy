package physics.models.collisions;

import physics.models.particles.Particle;

import java.util.Collection;

public interface Collision {

    void apply(Collection<? extends Particle> particles);
    void draw();

}
