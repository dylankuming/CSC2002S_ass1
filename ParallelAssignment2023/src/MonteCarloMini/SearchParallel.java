package MonteCarloMini;

import java.util.concurrent.RecursiveTask;


public class SearchParallel extends RecursiveTask<int[]>{

   // private Search searchArray[];
    private TerrainArea terrain;
    private int[] rowPositions, colPositions;
    private int[] steps;
    private boolean[] stopped;
    private int low;
    private int high;
    private int minHeight = Integer.MAX_VALUE;
    enum Direction {
		STAY_HERE,
	    LEFT,
	    RIGHT,
	    UP,
	    DOWN
	  }

    private static int SEQUENTIAL_CUTOFF = 10000;

//    public SearchParallel(Search[] searchArray, int low, int high){
//         this.searchArray = searchArray;
//         this.high = high;
//         this.low = low;
//    }

    public SearchParallel(TerrainArea terrain, int[] pos_row, int[] pos_col, int[] steps, boolean[] stopped, int low, int high){
        this.terrain = terrain;
        this.rowPositions = pos_row;
        this.colPositions = pos_col;
        this.steps = steps;
        this.stopped = stopped;
        this.low = low;
        this.high = high;
    }
    

    protected int[] compute(){
        int[] results = new int[3];
        if((high-low) <= SEQUENTIAL_CUTOFF){
            //System.out.print("High:" + high + "\tLow:" + low);
            for(int i = low; i< high; i++){
                int local_min = find_valleys(i);
                if (local_min <= minHeight){
                    minHeight = local_min;
                    int xMin = getPos_col(i);
                    int yMin = getPos_row(i);
                    //System.out.println("xMin: " + xMin + "\nyMin: " + yMin);
                    results = new int[]{minHeight, xMin, yMin};
                }
            }
            return results;
        }
        else{
            //System.out.print("High:" + high + "\tLow:" + low);
            SearchParallel left = new SearchParallel(terrain, rowPositions, colPositions, steps, stopped,low, (high+low)/2);
            SearchParallel right = new SearchParallel(terrain, rowPositions, colPositions, steps, stopped,(high+low)/2, high);
            left.fork();
            int[] rightHeight = right.compute();
            int[] leftHeight = left.join();
            int[] result;
            if(rightHeight[0] < leftHeight[0])
                result = rightHeight;
            else
                result = leftHeight;
            return result;
        }

        //return minHeight;
    }

    public int find_valleys(int id) {	
		int height=Integer.MAX_VALUE;
		Direction next = Direction.STAY_HERE;
		while(terrain.visited(rowPositions[id], colPositions[id])==0) { // stop when hit existing path
			height=terrain.get_height(rowPositions[id], colPositions[id]);
			//System.out.println("Height: " + height);
			terrain.mark_visited(rowPositions[id], colPositions[id], id); //mark current position as visited
			steps[id]++;
			next = terrain.next_step(rowPositions[id], colPositions[id]);
			switch(next) {
				case STAY_HERE: return height; //found local valley
				case LEFT: 
					//System.out.println("LEFT");
					rowPositions[id]--;
					break;
				case RIGHT:
					//System.out.println("RIGHT");
					rowPositions[id]++;
					break;
				case UP: 
					//System.out.println("UP");
					colPositions[id]--;
					break;
				case DOWN: 
					//System.out.println("DOWN");
					colPositions[id]++;
					break;
			}
		}
		stopped[id]=true;
		//System.out.println("BEEN VISITED - END OF SEARCH\n\n");
		return height;
	}

    public int getPos_row(int id) {
		return rowPositions[id];
	}

	public int getPos_col(int id) {
		return colPositions[id];
	}
}
