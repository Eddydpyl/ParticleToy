import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import physics.Integration;
import physics.model.*;
import org.lwjgl.util.glu.GLU;

import physics.Integration;
import physics.model.*;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 800;
    private static final double KS = 0.1;
    private static final double KD = 0.1;
    private static final double DELTA = 0.001;
    private static final double EPSILON = 0.1;

    private long window;
    private int method;

    private List<Particle2D> particles;
    private List<Force> forces;
    private List<Constraint> constraints;

    private Particle2D mouseParticle;
    private Force mouseSpring;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            // Center the window
            glfwSetWindowPos(window,(vidmode.width() - pWidth.get(0)) / 2,(vidmode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically
        glfwMakeContextCurrent(window); // Make the OpenGL context current
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window); // Make the window visible
        loadElements();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Remember the start time.
        long startTime = System.nanoTime();

        //set cursor mode
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

        // Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glViewport(0, 0, WIDTH, HEIGHT); // Make the viewport always fill the whole window.
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();

            // Build time difference between current and start time.
            long currentTime = System.nanoTime();
            double diff = (currentTime - startTime) / 1E6f;

            simulate(diff); // Draw the scene.

            // Mouse interaction.
            int state = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT);
            MouseSpring(state);

            glfwSwapBuffers(window); // Swap the color buffers.
            glfwPollEvents(); // Poll for window events. The key callback above will only be invoked during this call.
        }
    }

    private void MouseSpring(int state) {
        if (state == GLFW_PRESS) {
            DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(window, xpos, ypos);
            float winX = (float) (xpos.get());
            float winY = (float) (HEIGHT-ypos.get());
            IntBuffer viewport = BufferUtils.createIntBuffer(16);
            FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
            FloatBuffer projection = BufferUtils.createFloatBuffer(16);
            FloatBuffer position = BufferUtils.createFloatBuffer(3);
            GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelview);
            GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

            float winZ = (float) 0;
            GLU.gluUnProject(winX, winY, winZ, modelview, projection, viewport, position);
            double[] pos = new double[]{position.get(0),position.get(1)};
            Particle2D mouse = new Particle2D(pos, 1);
            if (mouseParticle == null) {
                for (Particle2D particle : particles) {
                    double distance = Math.sqrt(Math.pow((pos[0]-particle.getPosition()[0]),2)+Math.pow((pos[1]-particle.getPosition()[1]),2));
                    if (distance <= EPSILON) {
                        mouseParticle = particle;
                    }
                }
            }
            if (mouseParticle != null) {
                if (mouseSpring != null) forces.remove(mouseSpring);
                mouseSpring = new SpringForce2D(mouseParticle, mouse, KS, KD, 0);
                forces.add(mouseSpring);
            }
        } else if (state == GLFW_RELEASE) {
            if (mouseSpring != null) forces.remove(mouseSpring);
            mouseSpring = null;
            mouseParticle = null;
        }
    }

    /**
     * Called once at the start of the simulation.
     */
    private void loadElements() {
        method = Integration.RUNGE_KUTA;
        particles = new ArrayList<>();
        forces = new ArrayList<>();
        constraints = new ArrayList<>();
        createCloth2D(4, 4, 0.2, 0.0001);
    }

    /**
     * Called every time a frame is created. Starts with a projection identity matrix enabled by default.
     * @param time Milliseconds since the simulation started.
     */
    private void simulate(double time) {
        updateParticles();
        draw(); // Draw all particles, forces and constraints.
    }

    private void updateParticles() {
        // Clear force accumulators
        for (Particle particle : particles) particle.clearForces();
        // Compute and apply generic forces
        for (Force force : forces) force.apply();

        // Compute and apply constraint forces
        Constraint.apply(particles, constraints, KS, KD);

        // Update all the particles' state
        Integration.apply(particles, DELTA, method);
    }

    private void draw() {
        for (Particle particle : particles) particle.draw();
        for (Force force : forces) force.draw();
        for (Constraint constraint : constraints) constraint.draw();
    }
    /**
     *
     * @param width Number of particles across the cloth.
     * @param height Number of particles down the cloth.
     * @param distance Space between each particle and its neighbors.
     * @param mass Weight of all of the particles.
     */
    private void createCloth2D(int width, int height, double distance, double mass) {
        if (width <= 1 || height <= 1) throw new IllegalArgumentException();
        double[] rightFix = new double[]{(width / 2) * distance, (height / 2) * distance};
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                particles.add(new Particle2D(new double[]{rightFix[0] - i * distance, rightFix[1] - j * distance}, mass));
            }
        }
        for (int i = 0; i < particles.size(); i++) {
            if ((i + 1) % height > 0) forces.add(new SpringForce2D(particles.get(i), particles.get(i+1), KS, KD, distance));
            if ((i + height) < particles.size()) forces.add(new SpringForce2D(particles.get(i), particles.get(i+height), KS, KD, distance));
        } forces.add(new GravityForce2D(particles));
        for (int i = 0; i < width; i++) {
            Particle2D particle = particles.get(i * height);
            constraints.add(new CircularConstraint2D(particles.get(i * height), new double[]{particle.getPosition()[0], particle.getPosition()[1] + 0.1}, 0));
        }
    }

}
