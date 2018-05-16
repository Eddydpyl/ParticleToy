import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
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

    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;
    private static final double KS = 0.01;
    private static final double KD = 0.01;
    private static final double DELTA = 0.1;

    private long window;
    private int method;

    private List<Particle2D> particles;
    private List<Force> forces;
    private List<Constraint> constraints;

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
        window = glfwCreateWindow(800, 500, "Hello World!", NULL, NULL);
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
            
            
            int state = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT);
            if (state == GLFW_PRESS) {
                MouseInteract();
            }
            
            
            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents(); // Poll for window events. The key callback above will only be invoked during this call.
        }
    }

    private void MouseInteract() {
    	DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
    	DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
    	glfwGetCursorPos(window, xpos, ypos);
    	float winX = (float) (xpos.get());
    	float winY = (float) (500-ypos.get());
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
     	for (Particle2D particle : particles) {
    		System.out.println("particle: " + particle.getPosition()[0]+","+particle.getPosition()[1]);;
    	}
    	Particle2D p = new Particle2D(pos, 0.1);
    	p.draw();
    	System.out.println("mouse: " + pos[0]+","+pos[1]);;
	}

	/**
     * Called once at the start of the simulation.
     */
    private void loadElements() {
        method = Integration.RUNGE_KUTA;
        particles = new ArrayList<>();
        forces = new ArrayList<>();
        constraints = new ArrayList<>();
        particles.add(new Particle2D(new double[]{0,0}, 3.0));
        constraints.add(new CircularConstraint2D(particles.get(0), new double[]{1,1}, 1));
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
        for (Particle particle : particles) particle.setForces(new double[]{0,0});

        // Compute and apply generic forces
        for (Force force : forces) force.apply();

        // Compute and apply constraint forces
        Constraint.apply(particles, constraints, KS, KD);

        // Update all particle's state
        Integration.apply(particles, DELTA, method);
    }

    private void draw() {
        for (Particle particle : particles) particle.draw();
        for (Force force : forces) force.draw();
        for (Constraint constraint : constraints) constraint.draw();
    }

}
