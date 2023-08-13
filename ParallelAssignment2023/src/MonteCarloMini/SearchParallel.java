package MonteCarloMini;

import java.util.concurrent.RecursiveTask;

import MonteCarloMini.TerrainArea.SearchInner;

public class SearchParallel extends RecursiveTask<int[]>{

   // private Search searchArray[];
    private TerrainArea terrain;
    private SearchInner[] searches;
    private int high;
    private int low;
    private int minHeight = Integer.MAX_VALUE;

    private static int SEQUENTIAL_CUTOFF = 10000;

//    public SearchParallel(Search[] searchArray, int low, int high){
//         this.searchArray = searchArray;
//         this.high = high;
//         this.low = low;
//    }

    public SearchParallel(SearchInner[] searches, int low, int high, TerrainArea terrain){
        this.searches = searches;
        this.low = low;
        this.high = high;
        this.terrain = terrain;
    }


    protected int[] compute(){
        int[] results = new int[3];
        if ((high - low) <= SEQUENTIAL_CUTOFF) {
            int local_min = Integer.MAX_VALUE;
            int finder = -1;
    
            for (int i = low; i < high; i++) {
                
                int currentLocalMin = searches[i].find_valleys();
                if (currentLocalMin < local_min && !searches[i].isStopped()) {
                    local_min = currentLocalMin;
                    finder = i;
                    //System.out.println(i+ ": " + local_min + "\t" + xMin + "\t" + yMin + "\n");
                }
            }
    
            results[0] = local_min;
            results[1] = finder;
    
            return results;
        }
        else{
            //System.out.print("High:" + high + "\tLow:" + low);
            SearchParallel left = new SearchParallel(searches, low, (high+low)/2, terrain);
            SearchParallel right = new SearchParallel(searches, (high+low)/2, high, terrain);
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

}
