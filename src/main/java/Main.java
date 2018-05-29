import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import physics.Integration;
import physics.model.*;
import org.lwjgl.util.glu.GLU;

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
    
    private long window;
    private int method;
    private int zoomLevel;

    private List<Particle2D> particles;
    private List<Force> forces;
    private List<Constraint> constraints;
    private List<Solid> solids;

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

        reset(Integration.RUNGE_KUTA, ZOOM_0); // Set default integration method & zoom level
        createCloth2D(4, 4, 0.1, 0.01, 5, 5, false);
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
        draw(); // Draw all particles, forces and constraints.

        int key1State = glfwGetKey(window, GLFW_KEY_1);
        if (key1State == GLFW_PRESS) {
            reset(Integration.RUNGE_KUTA, ZOOM_0);
            createCloth2D(4, 4, 0.1, 0.01, 5, 5, false);
        }
        int key2State = glfwGetKey(window, GLFW_KEY_2);
        if (key2State == GLFW_PRESS) {
            reset(Integration.RUNGE_KUTA, ZOOM_1);
            createCloth2D(20, 10, 0.1, 0.01, 5,5, true);
        }
        int key3State = glfwGetKey(window, GLFW_KEY_3);
        if (key3State == GLFW_PRESS) {
            reset(Integration.RUNGE_KUTA, ZOOM_0);
            createHair2D(9, 3, 0.1, 0.01, 1, 1);
        }
    }

    private void reset(int method, int zoomLevel) {
        this.method = method;
        this.zoomLevel = zoomLevel;
        particles = new ArrayList<>();
        forces = new ArrayList<>();
        constraints = new ArrayList<>();
        solids = new ArrayList<>();
    }

    private void updateZoom() {
        if (zoomLevel == ZOOM_1) glScaled(1,1,1);
        if (zoomLevel == ZOOM_1) glScaled(0.4,0.4,1);
    }

    private void updateParticles() {
        // Clear force accumulators
        for (Particle particle : particles) particle.clearForces();

        // Compute and apply generic forces
        for (Force force : forces) force.apply();

        // Collisions
        for (Solid solid : solids) solid.apply();

        // Compute and apply constraint forces
        Constraint.apply(particles, constraints, 5, 5);

        // Update all the particles' state
        Integration.apply(particles, DELTA, method);
    }

    private void draw() {
        for (Particle particle : particles) particle.draw();
        for (Force force : forces) force.draw();
        for (Constraint constraint : constraints) constraint.draw();
        for (Solid solid : solids) solid.draw();
    }

    /**
     * @param width Number of particles across the cloth.
     * @param height Number of particles down the cloth.
     * @param distance Space between each particle and its neighbors.
     * @param mass Weight of all of the particles.
     */
    private void createCloth2D(int width, int height, double distance, double mass, double ks, double kd, boolean floor) {
        if (width <= 1 || height <= 1) throw new IllegalArgumentException();
        double[] rightFix = new double[]{(width - 1) * distance / 2, (height - 1) * distance / 2};
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                particles.add(new Particle2D(new double[]{rightFix[0] - i * distance, rightFix[1] - j * distance}, mass));
            }
        }
        for (int i = 0; i < particles.size(); i++) {
            if ((i + 1) % height > 0) forces.add(new SpringForce2D(particles.get(i), particles.get(i+1), ks, kd, distance));
            if ((i + height) < particles.size()) forces.add(new SpringForce2D(particles.get(i), particles.get(i+height), ks, kd, distance));
            if ((i + 1) % height > 0 && (i + height + 1) < particles.size())
                forces.add(new SpringForce2D(particles.get(i), particles.get(i + height + 1), ks, kd, Math.sqrt(2) * distance));
            if (i % height > 0 && (i + height - 1) < particles.size())
                forces.add(new SpringForce2D(particles.get(i), particles.get(i + height - 1), ks, kd, Math.sqrt(2) * distance));
        } forces.add(new GravityForce2D(particles));
        for (int i = 0; i < width; i++) {
            Particle2D particle = particles.get(i * height);
            constraints.add(new CircularConstraint2D(particles.get(i * height), new double[]{particle.getPosition()[0], particle.getPosition()[1] + 0.1}, 0));
        }
        if (floor) solids.add(new Floor(particles, new double[]{0,-height*distance}, 0.5, 0.001));
    }

    private void createHair2D(int width, int height,double distance, double mass, double ks, double kd) {
        if (width <= 1) throw new IllegalArgumentException();
        double[] rightFix = new double[]{(width / 2) * distance,(height/2)*3*distance};
        for (int i = 0; i < width; i++) {
        	for(int j = 0; j< height;j++) {
        		createSingleHair(new double[]{rightFix[0] - i * distance, rightFix[1] - j * 3*distance}, mass, ks, kd);
        	}
            
        }
    }

    private void createSingleHair(double[] pos, double mass, double ks, double kd) {
    	Particle2D p1 = new Particle2D(pos, mass);
    	double[]pos2 = new double[] {pos[0]-0.05,pos[1]-0.3};
    	Particle2D p2 = new Particle2D(pos2, mass);
    	double[]pos3 = new double[] {pos[0]+0.05,pos[1]-0.3};
    	Particle2D p3 = new Particle2D(pos3, mass);
    	particles.add(p1);
    	particles.add(p2);
    	particles.add(p3);
    	forces.add(new AngularSpringForce2D(p2, p3, p1,0.0002,0.0002,60));
    	forces.add(new AngularSpringForce2D(p3, p1, p2,0.0002,0.0002,60));
    	forces.add(new SpringForce2D(p1, p2, ks, kd, 0.1));
    	forces.add(new SpringForce2D(p1, p3, ks, kd, 0.1));
    	forces.add(new SpringForce2D(p2, p3, ks, kd, 0.05));
    	constraints.add(new CircularConstraint2D(p2, new double[] {p2.getPosition()[0],p2.getPosition()[1]+0.01},0));
    	constraints.add(new CircularConstraint2D(p3, new double[] {p3.getPosition()[0],p3.getPosition()[1]+0.01},0));
    }

}
