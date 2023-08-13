package MonteCarloMini;

import java.util.concurrent.RecursiveTask;
import MonteCarloMini.TerrainArea.Search$InnerClass;

/**
 * Represents a parallel search task that recursively splits the search workload
 * into smaller tasks to take advantage of the ForkJoin framework.
 */

public class SearchParallel extends RecursiveTask<int[]>{

  
    private TerrainArea terrain; // Terrain instance for evaluating heights
    private Search$InnerClass[] searches;  // Array of searches
    private int hi, lo; // Start and end indices for the current search segment
    private int minHeight = Integer.MAX_VALUE;

    private static int SEQUENTIAL_CUTOFF = 10000; // Threshold for splitting the task further

    /**
     * Constructor for initializing the search task.
     */
    public SearchParallel(Search$InnerClass[] searches, int lo, int hi, TerrainArea terrain){
        this.searches = searches;
        this.lo = lo;
        this.hi = hi;
        this.terrain = terrain;
    }


    protected int[] compute(){
        int[] results = new int[3];
        if ((hi - lo) <= SEQUENTIAL_CUTOFF) {
            int localMinimum = Integer.MAX_VALUE;
            int ans = -1;
    
            for (int i = lo; i < hi; i++) {
                
                int currentLocalMinimum = searches[i].find_valleys();
                if (currentLocalMinimum < localMinimum && !searches[i].isStopped()) {
                    localMinimum = currentLocalMinimum;
                    ans = i;
                   
                }
            }
    
            results[0] = localMinimum;
            results[1] = ans;
    
            return results;
        }
        else{
            SearchParallel left = new SearchParallel(searches, lo, (hi+lo)/2, terrain);
            SearchParallel right = new SearchParallel(searches, (hi+lo)/2, hi, terrain);
            left.fork();
            int[] rightHeight = right.compute();
            int[] leftHeight = left.join();
            int[] resultHeight;
            if(rightHeight[0] < leftHeight[0])
                resultHeight = rightHeight;
            else
                resultHeight = leftHeight;
            return resultHeight;
        }

       
    }

}
