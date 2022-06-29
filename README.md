# Racket ISL Interpreter

An interpreter for Racket's ISL. To use, do the following:

- Download Northeastern's javalib and tester libraries and add them to the project
- Set your run configuration to the Main class, and pass in the filename of the file you wish to interpret as the argument
- Ensure you put the file you want to run in the source directory (can be a txt file or a rkt file)

There are still many features of ISL that have not been implemented. Imaginary numbers and characters are not supported in the interpreter, 
check-expect is the only check function that is supported, and many smaller functions are not supported. If you program uses one of these unsupported
functions, it will tell you which one.

THere is limited support for the image and universe libraries. Basic images have been implemented with a limited range of colors, and big-bang is
implemented but only with the handlers to-draw, on-tick, on-key, and stop-when.

I'm still working on testing this interpreter, so feel free to email me if you have found any bugs: davidob323@gmail.com
