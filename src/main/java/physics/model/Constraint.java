package physics.model;

public interface Constraint {

    double calculateC0();
    double calculateC1();
    double[][] calculateJ0();
    double[][] calculateJ1();
    void draw();

}
