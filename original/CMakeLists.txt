cmake_minimum_required(VERSION 3.10)
project(ParticleToy)

set(CMAKE_CXX_STANDARD 11)

include_directories(.)
include_directories(include)
include_directories(include/gfx)

add_executable(ParticleToy
        include/gfx/config.h
        include/gfx/geom3d.h
        include/gfx/geom4d.h
        include/gfx/gfx.h
        include/gfx/mat2.h
        include/gfx/mat3.h
        include/gfx/mat4.h
        include/gfx/quat.h
        include/gfx/symmat2.h
        include/gfx/symmat3.h
        include/gfx/symmat4.h
        include/gfx/vec2.h
        include/gfx/vec3.h
        include/gfx/vec4.h
        CircularWireConstraint.cpp
        CircularWireConstraint.h
        imageio.cpp
        imageio.h
        linearSolver.cpp
        linearSolver.h
        Particle.cpp
        Particle.h
        ParticleToy.cpp
        RodConstraint.cpp
        RodConstraint.h
        Solver.cpp
        SpringForce.cpp
        SpringForce.h)
