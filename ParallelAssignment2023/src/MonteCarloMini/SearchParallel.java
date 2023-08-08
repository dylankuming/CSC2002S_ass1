package MonteCarloMini;

import java.util.concurrent.RecursiveTask;

public class SearchParallel extends RecursiveTask{

    private Search searchArray[];
    private int low;
    private int high;
    private int minHeight = Integer.MAX_VALUE;
    private int xMin;
    private int yMin;

    private static int SEQUENTIAL_CUTOFF = 1000;

   public SearchParallel(Search[] searchArray, int low, int high){
        this.searchArray = searchArray;
        this.high = high;
        this.low = low;
   }
    

    protected Integer[] compute(){
        Integer[] results = new Integer[3];
        if((high-low) <= SEQUENTIAL_CUTOFF){
            //System.out.print("High:" + high + "\tLow:" + low);
            for(int i = low; i< high; i++){
                int local_min = searchArray[i].find_valleys();
                if (local_min <= minHeight){
                    minHeight = local_min;
                    xMin = searchArray[i].getPos_col();
                    yMin = searchArray[i].getPos_row();
                    results = new Integer[]{minHeight, xMin, yMin};
                }
            }
            return results;
        }
        else{
            //System.out.print("High:" + high + "\tLow:" + low);
            SearchParallel left = new SearchParallel(searchArray, low, (high+low)/2);
            SearchParallel right = new SearchParallel(searchArray, (high+low)/2, high);
            left.fork();
            Integer[] rightHeight = right.compute();
            Integer[] leftHeight = (Integer[])left.join();
            Integer[] result;
            if(rightHeight[0] < leftHeight[0])
                result = rightHeight;
            else
                result = leftHeight;
            return result;

        }

        //return minHeight;
    }
}
