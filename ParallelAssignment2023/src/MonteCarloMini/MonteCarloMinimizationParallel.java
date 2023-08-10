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

import MonteCarloMini.SearchParallel.Direction;

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

     	int num_searches;	// Array of searches
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

		rows = 1000;
    	columns = 5000;
    	xmin = -10;
    	xmax = 10;
    	ymin = -10;
    	ymax =10;
    	searches_density = 0.5;
  
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
    	SearchInner[] searches = new SearchInner[num_searches];
    	
      	if(DEBUG) {
    		/* Print initial values */
    		System.out.printf("Number searches: %d\n", num_searches);
    		terrain.print_heights();
    	}
    	
    	//start timer
    	tick();
    	
    	//all searches
    	int local_min=Integer.MAX_VALUE;
    	//int finder =-1;
		for (int i=0; i<num_searches; i++){
			searches[i] =  new MonteCarloMinimizationParallel(). new SearchInner(i, rand.nextInt(rows), rand.nextInt(columns),terrain);
		}
    	int[] results =  FJPool.invoke(new SearchParallel(searches, 0, num_searches));
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
		int i = results[1];
		int pos_row = searches[i].getPos_row();
		int pos_col = searches[i].getPos_col();
		System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min, terrain.getXcoord(pos_row), terrain.getYcoord(pos_col) );
    	//terrain.print_visited();
    }

	public class SearchInner{

        private int id;				// Searcher identifier
        private int pos_row, pos_col;	// Position in the grid
        private int steps; //number of steps to end of search
        private boolean stopped;			// Did the search hit a previous trail?
        
        private TerrainArea terrain;

        public SearchInner(int id, int pos_row, int pos_col, TerrainArea terrain) {
            this.id = id;
            this.pos_row = pos_row; //randomly allocated
            this.pos_col = pos_col; //randomly allocated
            this.terrain = terrain;
            this.stopped = false;
        }
        
        public int find_valleys() {	
            int height=Integer.MAX_VALUE;
            Direction next = Direction.STAY_HERE;
            while(terrain.visited(pos_row, pos_col)==0) { // stop when hit existing path
                height=terrain.get_height(pos_row, pos_col);
                //System.out.println("Height: " + height);
                terrain.mark_visited(pos_row, pos_col, id); //mark current position as visited
                steps++;
                next = terrain.next_step(pos_row, pos_col);
                switch(next) {
                    case STAY_HERE: return height; //found local valley
                    case LEFT: 
                        //System.out.println("LEFT");
                        pos_row--;
                        break;
                    case RIGHT:
                        //System.out.println("RIGHT");
                        pos_row=pos_row+1;
                        break;
                    case UP: 
                        //System.out.println("UP");
                        pos_col=pos_col-1;
                        break;
                    case DOWN: 
                        //System.out.println("DOWN");
                        pos_col=pos_col+1;
                        break;
                }
            }
            stopped=true;
            //System.out.println("VALLEY (" + height + ")FOUND AT: " + pos_row + ", " + pos_col + "\n\n");
            return height;
        }

        public int getID() {
            return id;
        }

        public int getPos_row() {
            return pos_row;
        }

        public int getPos_col() {
            return pos_col;
        }

        public int getSteps() {
            return steps;
        }
        public boolean isStopped() {
            return stopped;
        }

    }

}