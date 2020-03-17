# Paticle Toy [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) ![Status: Abandoned](https://img.shields.io/badge/Status-Abandoned-red.svg)

### Setting up the project
As it is, the project in the master branch should be compatible with both InteliJ and Eclipse.
Extra steps might be necessary in order to make it work, depending on your personal setup.

The java library chosen for handling OpenGL is LWJGL, while we are using the EJML library for matrix operations,
 instead of the provided methods over matrices in the "linearSolver" file in the original C++ skeleton code. Said code
 can be found inside of the "original" directory.

Useful links:
- https://www.lwjgl.org/guide
- http://ejml.org/wiki/index.php?title=SimpleMatrix
