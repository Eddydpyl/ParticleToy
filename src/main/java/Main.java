import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import physics.Integration;
import org.lwjgl.util.glu.GLU;
import physics.models.Collision;
import physics.models.Grid2D;
import physics.models.particles.Rectangle2D;
import physics.models.constraints.CircularConstraint2D;
import physics.models.constraints.Constraint;
import physics.models.forces.*;
import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle;
import physics.models.particles.Particle2D;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    private static final int ZOOM_0 = 0;
    private static final int ZOOM_1 = 1;

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 800;
    private static final double DELTA = 0.005;
    private static final double EPSILON = 0.05;
    private static final double RATIO = Math.pow(10, -15);
    private static final double H = 0.1;
    
    private long window;
    private int method;
    private int zoomLevel;
    private boolean showGrid;

    private List<Particle2D> particles;
    private List<Force> forces;
    private List<Constraint> constraints;
    private Grid2D grid;

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
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        window = glfwCreateWindow(WIDTH, HEIGHT, "Particle Toy", NULL, NULL);
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

        defaultState(); // Load the default simulation
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

            float winZ = 0f;
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
                mouseSpring = new SpringForce2D(mouseParticle, mouse, 5, 5, 0);
                forces.add(mouseSpring);
            }
        } else if (state == GLFW_RELEASE) {
            if (mouseSpring != null) forces.remove(mouseSpring);
            mouseSpring = null;
            mouseParticle = null;
        }
    }

    /**
     * Called every time a frame is created. Starts with a projection identity matrix enabled by default.
     * @param time Milliseconds since the simulation started.
     */
    private void simulate(double time) {
        updateZoom();
        updateParticles();

        draw(showGrid); // Draw all particles, forces and constraints.

        int key1State = glfwGetKey(window, GLFW_KEY_1);
        if (key1State == GLFW_PRESS) defaultState();

        int key2State = glfwGetKey(window, GLFW_KEY_2);
        if (key2State == GLFW_PRESS) {
            showGrid = false;
            reset(Integration.EULER, ZOOM_0);
            createCloth2D(5, 5, 0.1, 0.005, 5,5);
        }
        int key3State = glfwGetKey(window, GLFW_KEY_3);
        if (key3State == GLFW_PRESS) {
            showGrid = false;
            reset(Integration.EULER, ZOOM_0);
            createLiquidWithCloth();
        }
        int key4State = glfwGetKey(window, GLFW_KEY_4);
        if (key4State == GLFW_PRESS) {
            showGrid = false;
            reset(Integration.EULER, ZOOM_0);
            createLiquidWithRectangular();
        }
    }

    private void defaultState() {
        showGrid = false;
        reset(Integration.EULER, ZOOM_0);
        createLiquid(10, 10, 0.01, 0.00001);
    }

    private void reset(int method, int zoomLevel) {
        this.method = method;
        this.zoomLevel = zoomLevel;
        particles = new ArrayList<>();
        forces = new ArrayList<>();
        constraints = new ArrayList<>();
        grid = new Grid2D(new double[]{-1,1}, (int) (200 * H), H);
    }

    private void updateZoom() {
        if (zoomLevel == ZOOM_0) glScaled(1,1,1);
        if (zoomLevel == ZOOM_1) glScaled(0.4,0.4,1);
    }

    private void updateParticles() {
        // Clear force accumulators
        for (Particle particle : particles) particle.clearForce();

        // Calculate density for liquids
        grid.update(particles);

        // Compute and apply generic forces
        for (Force force : forces) force.apply();

        // Collisions between particles
        Collision.apply(particles, 0.2);

        // Compute and apply constraint forces
        Constraint.apply(particles, constraints, 5, 5);

        // Update all the particles' state
        Integration.apply(particles, DELTA, method);
    }

    private void draw(boolean drawGrid) {
        for (Particle particle : particles) particle.draw();
        for (Force force : forces) force.draw();
        for (Constraint constraint : constraints) constraint.draw();
        if (drawGrid) grid.draw();
    }

    /**
     * @param width Number of particles across the cloth.
     * @param height Number of particles down the cloth.
     * @param distance Space between each particle and its neighbors.
     * @param mass Weight of all of the particles.
     */
    private void createCloth2D(int width, int height, double distance, double mass, double ks, double kd) {
        if (width <= 1 || height <= 1) throw new IllegalArgumentException();
        double[] rightFix = new double[]{(width - 1) * distance / 2, (height - 1) * distance / 2};
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                particles.add(new Rectangle2D(new double[]{rightFix[0] - i * distance, rightFix[1] - j * distance}, 0.02, 0.02, mass, true, false, true));
            }
        }
        for (int i = 0; i < particles.size(); i++) {
            if ((i + 1) % height > 0) forces.add(new SpringForce2D(particles.get(i), particles.get(i+1), ks, kd, distance));
            if ((i + height) < particles.size()) forces.add(new SpringForce2D(particles.get(i), particles.get(i+height), ks, kd, distance));
            if ((i + 1) % height > 0 && (i + height + 1) < particles.size())
                forces.add(new SpringForce2D(particles.get(i), particles.get(i + height + 1), ks, kd, Math.sqrt(2) * distance));
            if (i % height > 0 && (i + height - 1) < particles.size())
                forces.add(new SpringForce2D(particles.get(i), particles.get(i + height - 1), ks, kd, Math.sqrt(2) * distance));
            forces.add(new GravityForce2D(particles.get(i)));
        }
        for (int i = 0; i < width; i++) {
            Particle2D particle = particles.get(i * height);
            constraints.add(new CircularConstraint2D(particles.get(i * height), new double[]{particle.getPosition()[0], particle.getPosition()[1] + 0.1}, 0));
        }
    }

    private void createLiquid(int width, int height, double distance, double mass) {
        if (width <= 1 || height <= 1) throw new IllegalArgumentException();
        double[] rightFix = new double[]{(width - 1) * distance / 2, (height - 1) * distance / 2};
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                particles.add(new FluidParticle2D(new double[]{rightFix[0] - i * distance, rightFix[1] - j * distance}, mass));
            }
        }
//        particles.add(new Rectangle2D(new double[]{0.1, -0.2}, 0.2, 0.2, 0.1));
//        particles.add(new Rectangle2D(new double[]{0.1, -0.5}, 0.2, 0.2, 0.1));
        for (Particle particle : particles) {
            if (particle instanceof Particle2D) {
                if (particle instanceof FluidParticle2D) {
                    FluidParticle2D fluidParticle = (FluidParticle2D) particle;
                    forces.add(new LiquidForces2D(fluidParticle, grid, 12.75, 1, 100, H, mass * RATIO));
                    forces.add(new GravityForce2D((Particle2D) particle));
                }
            }
        }
    }
    private void createLiquidWithRectangular() {
      createLiquid(10, 30, 0.01, 0.00001);
      particles.add(new Rectangle2D(new double[]{0, -0.4}, 0.05, 0.05, 0.01));
      particles.add(new Rectangle2D(new double[]{0, -0.5}, 0.5, 0.1, 0.1,false,false,true));
      particles.add(new Rectangle2D(new double[]{-0.25, -0.4}, 0.1, 0.3, 0.1,false,false,true));
      particles.add(new Rectangle2D(new double[]{0.25, -0.4}, 0.1, 0.3, 0.1,false,false,true));
    }
    private void createLiquidWithCloth() {
      createCloth2D(5, 5, 0.05, 0.005, 5,5);
      createLiquid(10, 30, 0.01, 0.00001);
    
      particles.add(new Rectangle2D(new double[]{0, -0.5}, 0.5, 0.1, 0.1,false,false,true));
      particles.add(new Rectangle2D(new double[]{-0.25, -0.4}, 0.1, 0.3, 0.1,false,false,true));
      particles.add(new Rectangle2D(new double[]{0.25, -0.4}, 0.1, 0.3, 0.1,false,false,true));
    }

}
