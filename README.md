# mandelbrot
An interactive Mandlebrot-set generator utilizing the C pthread library for calculations and a Java swing GUI for interaction.

Programmed by Micah Adams, Jeremy Klyn, and Jay Timmer.

USAGE
=========
The repository contains a script to automatically compile and run the GUI interface. Naviagte to /mandlebrot/src/javaGUI. Set run permissions on the file `run` and execute the file. You will need `gcc`, `java`, and `javac`, available in your PATH for this stript to execute correctly.

Note: This program was built for Linux, but my work with Bash on Windows. You will have to use an x11 forwarder if you go this route,
such as Xming. There is no offical support for Windows however.

Command Line
------------
Compile the mandelbrotcalculator.c code with `gcc -o CompiledMandelbrot mandlebrotcalculator.c -pthread -lm`. You can then run `CompiledMandelbrot [xmin] [xmax] [ymin] [ymax]` to calculate the Mandelbrot set between the given x and y coordinates. 

TO-DO
===========
**High priority**
- ~~Extensive code cleanup~~
- ~~Independent command line operation~~
- ~~Create compilation scripts and remove calling of GCC in the swing GUI~~

Low Priority
- ~~Automatically update GUI without window resize~~
test test
