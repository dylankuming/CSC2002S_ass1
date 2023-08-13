package MonteCarloMini;
import java.util.concurrent.ForkJoinPool;
import java.util.Random;

import MonteCarloMini.TerrainArea.Search$InnerClass;
import MonteCarloMini.TerrainArea.Direction;

/**
 * Class for parallel minimization using the Monte Carlo method.
 * Uses the ForkJoin framework to perform parallel tasks for better performance.
 */

class MonteCarloMinimizationParallel{
	static final boolean DEBUG=false;  // Toggle for enabling/disabling debug mode

	static final ForkJoinPool fjPool = new ForkJoinPool(); // ForkJoinPool for executing parallel tasks

	// Variables to capture start and end times for measuring execution duration
	static long startTime = 0;
	static long endTime = 0;
	
	// Method to capture the start time
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	// Method to capture the end time
	private static void tock(){
		endTime=System.currentTimeMillis(); 
	}

    public static void main(String[] args)  {

		int rows, columns; //grid size
    	double xmin, xmax, ymin, ymax; //x and y terrain limits
    	TerrainArea terrain;  //object to store the heights and grid points visited by searches
    	double searches_density;	// Density - number of Monte Carlo  searches per grid position - usually less than 1!

     	int num_searches;		// Number of searches
    	Random rand = new Random();  //the random number generator
    	
    	if (args.length!=7) {  
    		System.out.println("Incorrect number of command line arguments provided.");   	
    		System.exit(0);
    	}
    	/* Read argument values */
    	rows = Integer.parseInt(args[0]);
    	columns = Integer.parseInt(args[1]);
    	xmin = Double.parseDouble(args[2]);
    	xmax = Double.parseDouble(args[3]);
    	ymin = Double.parseDouble(args[4]);
    	ymax = Double.parseDouble(args[5]);
    	searches_density = Double.parseDouble(args[6]);



  
    	if(DEBUG) {
    		/* Print arguments */
    		System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
    		System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax );
    		System.out.printf("Arguments, searches_density: %f\n", searches_density );
    		System.out.printf("\n");
    	}
    	
    	// Initialize terrain and searches based on the given arguments or defaults 
    	terrain = new TerrainArea(rows, columns, xmin,xmax,ymin,ymax);
    	num_searches = (int)( rows * columns * searches_density );
		Search$InnerClass[] searches = new Search$InnerClass[num_searches];
		
		// Initialize search tasks with random starting positions
		for (int i=0; i< num_searches; i++){
			searches[i] =  terrain.new Search$InnerClass(i, rand.nextInt(rows), rand.nextInt(columns),terrain);
		}
    	
      	if(DEBUG) {
    		/* Print initial values */
    		System.out.printf("Number searches: %d\n", num_searches);
    		terrain.print_heights();
    	}
    	
    	// Start the timer, perform parallel search, then stop the timer
    	tick();
    	int[] myresult =  fjPool.invoke(new SearchParallel(searches, 0, num_searches, terrain));
   		tock();
   		
    	if(DEBUG) {
    		/* print final state */
    		terrain.print_heights();
    		terrain.print_visited();
    	}

		 // Output the results and performance details  
    	System.out.println("Parallel program");
		System.out.printf("Run parameters\n");
		System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
		System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax );
		System.out.printf("\t Search density: %f (%d searches)\n", searches_density,num_searches );

		/*  Total computation time */
		System.out.printf("Time: %d ms\n",endTime - startTime );
		int tmp=terrain.getGrid_points_visited();
		System.out.printf("Grid points visited: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
		tmp=terrain.getGrid_points_evaluated();
		System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
	
		/* Results*/
		int min =  myresult[0];
		int finder = myresult[1];
		int pos_row = searches[finder].getPos_row();
		int pos_col = searches[finder].getPos_col();
		System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min, terrain.getXcoord(pos_row), terrain.getYcoord(pos_col) );
  
    }

}
