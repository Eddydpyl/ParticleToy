package physics.models;

import physics.models.particles.Particle;
import physics.models.particles.RigidBody;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static physics.LinearSolver.*;

public class Collision {

    public static void apply(Collection<? extends Particle> particles, double e) {
        Set<Particle> bodies = particles.stream().filter(x -> x instanceof RigidBody).collect(Collectors.toSet());
        for (Particle body : bodies) {
            for (Particle particle : particles) {
                resolve((RigidBody) body, particle, e);
            }
        }
    }

    private static void resolve(RigidBody body, Particle particle, double e) {
        // TODO: Avoid penetration.
        if (particle instanceof RigidBody) {
            RigidBody pBody = (RigidBody) particle;
            Set<double[]> firstContains = Stream.of(pBody.calculatePoints()).filter(body::containsPoint).collect(Collectors.toSet());
            Set<double[]> secondContains = Stream.of(body.calculatePoints()).filter(pBody::containsPoint).collect(Collectors.toSet());
            if (firstContains.size() > 0)
                resolveBodies(firstContains, body, pBody, e);
            if (secondContains.size() > 0)
                resolveBodies(secondContains, pBody, body, e);
        } else if (body.containsPoint(particle.getPosition()))
            resolveParticle(body, particle, e);
    }
    
    private static void resolveBodies(Set<double[]> points, RigidBody b1, RigidBody b2, double e) {
        for (double[] point : points) {
            double[][] edge = b1.calculateClosestEdge(point);
            double[] edgeVector = vecDiff(edge[1], edge[0]);
            double[] normal = vecNorm(new double[]{- edgeVector[1], edgeVector[0]});
            double[] ra = vecDiff(point, b1.getPosition());
            double[] rb = vecDiff(point, b2.getPosition());
            double[] relativeVelocity = vecDiff(vecAdd(b1.getVelocity(), vecCross(ra, b1.getAngular())),
                    vecDiff(b2.getVelocity(), vecCross(rb, b2.getAngular())));
            double contactVelocity = vecDot(relativeVelocity, normal);
            if(contactVelocity > 0) return; // If velocities are separating, do nothing.
            double r1 = Math.pow(vecCross(ra, normal)[0], 2) / vecModule(b1.getInertia());
            double r2 = Math.pow(vecCross(rb, normal)[0], 2) / vecModule(b2.getInertia());
            double[] impulse = vecTimesScalar(normal, (-(1 + e) * contactVelocity) / (r1 + r2 + 1 / b1.getMass() + 1 / b2.getMass()) / points.size());
            if (b1.isActive()) {
                b1.setVelocity(vecDiff(b1.getVelocity(), vecTimesScalar(vecNegate(impulse), 1 / b1.getMass())));
                b1.setAngular(b1.getAngular() + vecCross(ra, impulse)[0] / vecModule(b1.getInertia()));
            }
            if (b2.isActive()) {
                b2.setVelocity(vecAdd(b2.getVelocity(), vecTimesScalar(vecNegate(impulse), 1 / b2.getMass())));
                b2.setAngular(b2.getAngular() - vecCross(rb, impulse)[0] / vecModule(b2.getInertia()));
            }
        }
    }

    private static void resolveParticle(RigidBody body, Particle particle, double e) {
        double[][] edge = body.calculateClosestEdge(particle.getPosition());
        double[] edgeVector = vecDiff(edge[1], edge[0]);
        double[] normal = vecNorm(new double[]{-edgeVector[1], edgeVector[0]});
        double[] radius = vecDiff(particle.getPosition(), body.getPosition());
        double[] relativeVelocity = vecDiff(vecAdd(body.getVelocity(), vecCross(body.getAngular(), radius)), particle.getVelocity());
        double contactVelocity = vecDot(relativeVelocity, normal);
        if (contactVelocity > 0) return; // If velocities are separating, do nothing.
        double rotation = Math.pow(vecCross(radius, normal)[0], 2) / vecModule(body.getInertia());
        double[] impulse = vecTimesScalar(normal, (-(1 + e) * contactVelocity) / (rotation + 1 / body.getMass() + 1 / particle.getMass()));
        if (body.isActive()) {
            body.setVelocity(vecDiff(body.getVelocity(), vecTimesScalar(vecNegate(impulse), 1 / body.getMass())));
            body.setAngular(body.getAngular() + vecCross(radius, impulse)[0] / vecModule(body.getInertia()));
        }
        if (particle.isActive())
            particle.setVelocity(vecAdd(particle.getVelocity(), vecTimesScalar(vecNegate(impulse), 1 / particle.getMass())));
    }

}
