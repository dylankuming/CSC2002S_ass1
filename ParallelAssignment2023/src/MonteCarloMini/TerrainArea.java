package MonteCarloMini;

/**
 * Represents the terrain over which Monte Carlo minimization is performed.
 * Contains information about the terrain's heights and which positions were visited.
 */

public class TerrainArea {
	
	public static final int PRECISION = 10000; // Precision for height calculations

	private int rows, columns; //grid size
	private double xmin, xmax, ymin, ymax; //x and y terrain limits
	private int [][] heights, visit; // Matrices for storing height values and visitation records
	private int grid_points_visited, grid_points_evaluated;  // Counters for visited and evaluated grid points

    /**
     * Enum to represent possible movement directions on the terrain.
     */	
	enum Direction {
		STAY_HERE,
	    LEFT,
	    RIGHT,
	    UP,
	    DOWN
	  }
    
	 /**
     * Constructor to initialize a new TerrainArea with given dimensions and range.
     */
	public TerrainArea(int rows, int columns, double xmin, double xmax, double ymin, double ymax) {
		super();
		this.rows = rows;
		this.columns = columns;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		heights = new int[rows][columns];
		visit = new int[rows][columns];
		grid_points_visited=0;
		grid_points_evaluated=0;

		/* Terrain initialization */
		for(int i=0; i<rows; i++ ) {
			for( int j=0; j<columns; j++ ) {
				heights[i][j] = Integer.MAX_VALUE;
				visit[i][j] = 0;
			}
		}
	}

	 // has this site been visited before?
	 int visited( int x, int y) {return visit[x][y];}
	 
	 void mark_visited(int x, int y, int searcherID) { 
		 visit[x][y]=searcherID;
		 grid_points_visited++;
	
	 }
	
	int get_height( int x, int y) {
		if (heights[x][y]!=Integer.MAX_VALUE) {
			return heights[x][y]; 
		}
		/* Calculate the coordinates of the point in the ranges */
		double x_coord = xmin + ( (xmax - xmin) / rows ) * x;
		double y_coord = ymin + ( (ymax - ymin) / columns ) * y;
		/* Compute function value */
		double value = -2 * Math.sin(x_coord) * Math.cos(y_coord/2.0) + Math.log( Math.abs(y_coord - Math.PI*2) );
		
		// **** NB  Rosenbrock function below can be used instead for validation ****
		// double tmp = y_coord-Math.pow(x_coord,2);
		// tmp=100.0*Math.pow(tmp,2);
		// double tmp2=Math.pow(1-x_coord,2);
		// double value = tmp2+tmp; 
		
	
		/* Transform to fixed point precision */
		int fixed_point = (int)( PRECISION * value );
		heights[x][y]=fixed_point;
		grid_points_evaluated++;
		return fixed_point;
	}

	//work out where to go next - move downhill
	Direction next_step( int x, int y) {
		Direction climb_direction =Direction.STAY_HERE;
		int height;
		int local_min= get_height(x, y);
		if ( x > 0 ) {
			height=get_height(x-1, y);
			
			if (height<local_min) {
				local_min=height;
				climb_direction = Direction.LEFT;
			}
		}
		if ( x < (rows-1) ) {
			height=get_height(x+1, y);
		
			if (height<local_min) {
				local_min=height;
				climb_direction = Direction.RIGHT;
			}
		}
		if ( y > 0 ) {
			height=get_height(x, y-1);
			
			if (height<local_min) {
				local_min=height;
				climb_direction = Direction.UP;
			}
		}
		if ( y < (columns-1) ) {
			height=get_height(x, y+1);
		
			if (height<local_min) {
				local_min=height;
				climb_direction = Direction.DOWN;
			}
		}
		return climb_direction;
	}
	
	
	void print_heights( ) {
		int i,j;
		System.out.printf("Heights:\n");
		System.out.printf("+");
		for( j=0; j<columns; j++ ) System.out.printf("-------");
		System.out.printf("+\n");
		for( i=0; i<rows; i++ ) {
			System.out.printf("|");
			for( j=0; j<columns; j++ ) {
				if ( heights[i][j] != Integer.MAX_VALUE ) 
					System.out.printf(" %6d", heights[i][j] );
				else
					System.out.printf("       ");
			}
			System.out.printf("|\n");
		}
		System.out.printf("+");
		for( j=0; j<columns; j++ ) System.out.printf("-------");
		System.out.printf("+\n\n");
	}
	

	void print_visited( ) {
		int i,j;
		System.out.printf("Visited:\n");
		System.out.printf("+");
		for( j=0; j<columns; j++ ) System.out.printf("-------");
		System.out.printf("+\n");
		for( i=0; i<rows; i++ ) {
			System.out.printf("|");
			for( j=0; j<columns; j++ ) {
				System.out.printf(" %6d",visit[i][j] );
			}
			System.out.printf("|\n");
		}
		System.out.printf("+");
		for( j=0; j<columns; j++ ) System.out.printf("-------");
		System.out.printf("+\n\n");
	}

	public int getGrid_points_visited() {
		return grid_points_visited;
	}

	public int getGrid_points_evaluated() {
		return grid_points_evaluated;
	}
	
	public double getXcoord(int x) {
		return xmin + ( (xmax - xmin) / rows ) * x;
	}
	public double getYcoord(int y) {
		return ymin + ( (ymax - ymin) / columns ) * y;
	}

	public class Search$InnerClass{

		private int id;				// Searcher identifier
		private int pos_row, pos_col;	// Position in the grid
		private int steps; //number of steps to end of search
		private boolean stopped;			// Did the search hit a previous trail?
					
        private TerrainArea terrain;

        public Search$InnerClass(int id, int pos_row, int pos_col, TerrainArea terrain) {
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
			//System.out.println("BEEN VISITED - END OF SEARCH\n\n");
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
