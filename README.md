# README - MonteCarloMinimizationParallel Program

## Introduction

This is the MonteCarloMinimizationParallel program which uses the Monte Carlo method in parallel to perform minimization tasks over a defined terrain. 

## Prerequisites

1. Ensure that you have Java installed on your machine. This program specifically points to `/usr/bin/javac` for compiling and `/usr/bin/java` for execution.
2. Ensure you have `make` utility installed to utilize the provided Makefile.

## Directory Structure

- `src/MonteCarloMini` contains the Java source files.
- `bin` is the directory where compiled `.class` files will be placed.

## How to Compile and Run the Program

1. **Compile the Program**:  
   Navigate to the directory containing the Makefile using the terminal and type:
   ```
   make
   ```
   This command will compile the necessary Java files and store the `.class` files in the `bin` directory.

2. **Run the Program**:  
   After successful compilation, run the program with the following command:
   ```
   make run ARGS="row column xmin xmax ymin ymax search_density"
   ```
   Replace `row`, `column`, `xmin`, `xmax`, `ymin`, `ymax`, and `search_density` with appropriate values.

   For example:
   ```
   make run ARGS="5 5 0 100 0 100 0.5"
   ```

## Parameters Explanation

- `row`: The number of rows for the terrain grid.
- `column`: The number of columns for the terrain grid.
- `xmin`: Minimum x-coordinate value for the terrain.
- `xmax`: Maximum x-coordinate value for the terrain.
- `ymin`: Minimum y-coordinate value for the terrain.
- `ymax`: Maximum y-coordinate value for the terrain.
- `search_density`: The density at which the search operation will be performed in the Monte Carlo method.

## Cleaning Up

To remove the compiled `.class` files from the `bin` directory, simply run:
```
make clean
```

## Conclusion

Follow the steps mentioned above to successfully compile and execute the MonteCarloMinimizationParallel program. Ensure that you input the correct parameters during the run phase for accurate results.
