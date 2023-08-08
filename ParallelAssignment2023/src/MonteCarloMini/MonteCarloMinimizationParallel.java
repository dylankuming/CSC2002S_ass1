package MonteCarloMini;
/* Serial  program to use Monte Carlo method to 
 * locate a minimum in a function
 * This is the reference sequential version (Do not modify this code)
 * Michelle Kuttel 2023, University of Cape Town
 * Adapted from "Hill Climbing with Montecarlo"
 * EduHPC'22 Peachy Assignment" 
 * developed by Arturo Gonzalez Escribano  (Universidad de Valladolid 2021/2022)
 */
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

class MonteCarloMinimizationParallel{

	static final ForkJoinPool FJPool = new ForkJoinPool();

	static final boolean DEBUG=false;
	
	static long startTime = 0;
	static long endTime = 0;

	//timers - note milliseconds
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static void tock(){
		endTime=System.currentTimeMillis(); 
	}

    public static void main(String[] args)  {

    	int rows, columns; //grid size
    	double xmin, xmax, ymin, ymax; //x and y terrain limits
    	TerrainArea terrain;  //object to store the heights and grid points visited by searches
    	double searches_density;	// Density - number of Monte Carlo  searches per grid position - usually less than 1!

     	int num_searches;		// Number of searches
    	Search [] searches;		// Array of searches
    	Random rand = new Random();  //the random number generator
    	
    	/*if (args.length!=7) {  
    		System.out.println("Incorrect number of command line arguments provided.");   	
    		System.exit(0);
    	}*/
    	/* Read argument values */
    	// rows = Integer.parseInt(args[0]);
    	// columns = Integer.parseInt(args[1]);
    	// xmin = Double.parseDouble(args[2]);
    	// xmax = Double.parseDouble(args[3]);
    	// ymin = Double.parseDouble(args[4]);
    	// ymax = Double.parseDouble(args[5]);
    	// searches_density = Double.parseDouble(args[6]);

		rows = 10000;
    	columns = 10000;
    	xmin = -10;
    	xmax = 10;
    	ymin = -10;
    	ymax =10;
    	searches_density = 0.25;
  
    	if(DEBUG) {
    		/* Print arguments */
    		System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
    		System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax );
    		System.out.printf("Arguments, searches_density: %f\n", searches_density );
    		System.out.printf("\n");
    	}
    	
    	// Initialize 
    	terrain = new TerrainArea(rows, columns, xmin,xmax,ymin,ymax);
    	num_searches = (int)( rows * columns * searches_density );
    	int[] rowPositions = new int[num_searches];
		int[] colPositions = new int[num_searches];
		int[] steps = new int[num_searches];
		boolean[] stopped = new boolean[num_searches];
		int[] finderID = new int[num_searches];
    	for (int i=0;i<num_searches;i++){
			rowPositions[i] = rand.nextInt(rows);
			colPositions[i] = rand.nextInt(columns);
			finderID[i] = i+1;
		}
    	
      	if(DEBUG) {
    		/* Print initial values */
    		System.out.printf("Number searches: %d\n", num_searches);
    		terrain.print_heights();
    	}
    	
    	//start timer
    	tick();
    	
    	//all searches
    	int local_min=Integer.MAX_VALUE;
    	int finder =-1;
    	int[] results =  FJPool.invoke(new SearchParallel(terrain, rowPositions, colPositions, steps, stopped, 0, num_searches));
		int min = results[0];
   		//end timer
   		tock();
   		
    	if(DEBUG) {
    		/* print final state */
    		terrain.print_heights();
    		terrain.print_visited();
    	}

		
    	
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
		System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min, terrain.getXcoord(results[1]), terrain.getYcoord(results[2]) );
    	//terrain.print_visited();
    }
}