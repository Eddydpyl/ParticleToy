[![Build Status](https://travis-ci.com/Eddydpyl/ParticleToy.svg?token=Fpth26ybpqjsTJdNeqmb&branch=master)](https://travis-ci.com/Eddydpyl/ParticleToy)

# 2IMV25 - Interactive Virtual Environments

### Setting up the project
As it is, the project in the master branch should be compatible with both InteliJ and Eclipse.
Extra steps might be necessary in order to make it work, depending on your personal setup.

The java library chosen for handling OpenGL is LWJGL, while we are using the EJML library for matrix operations,
 instead of the provided methods over matrices in the "linearSolver" file in the original C++ skeleton code. Said code
 can be found inside of the "original" directory.

Useful links:
- https://www.lwjgl.org/guide
- http://ejml.org/wiki/index.php?title=SimpleMatrix
