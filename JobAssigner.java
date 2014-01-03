/**
 * Branch and Bound Java implementation by Jamie Macdonald for CMPE365
 * 06256541
 * 
 * Public Domain
 */

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Math;

public class JobAssigner {
    public static int[][] JOB_DATA;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a data file.");
            System.exit(1);
        }
        JOB_DATA = getJobData(args[0]);
        // for (int i = 0; i < JOB_DATA.length; i++) {
        //     for (int j = 0; j < JOB_DATA.length; j++) {
        //         System.out.printf("%d\t", JOB_DATA[i][j]);
        //     }
        //     System.out.printf("\n");
        // }
        System.out.printf("dimension of data: %d\n", JOB_DATA.length);

        MinHeap <Solution> minHeap = new MinHeap<Solution>();

        int upperBound = globalUpperBound();
        System.out.printf("GlobalUpperBound: %d\n", upperBound);
        int lowerBound = globalLowerBound();

        int count = 0; // number of partial solution steps made.

        for (int job = 0; job < JOB_DATA.length; job++) {
            // start a partial solution for each first job choice
            Solution s = new Solution(lowerBound, upperBound);
            s.add(new JobAssignment(0, job));
            minHeap.add(s);
            count++;
        }

        while (true) {
            // find min cost estimate from all Solutions so far.
            // i.e. best first strategy
            // O(logn)
            Solution s = minHeap.remove(0);
            // System.out.printf("lowerBound=%d, globalUpperBound=%d\n", s.getMinCost(), upperBound);

            // check if it is complete solution
            if (s.size() == JOB_DATA.length) {
                for (int i = 0; i < s.size(); i++) {
                    System.out.printf("%d\t%d\t%d\n", s.get(i).getAssignee(), s.get(i).getJob(), s.get(i).getCost());
                }
                int size = 0;
                for (int i = 0; i < s.size(); i++) {
                    size += s.get(i).getCost();
                }
                System.out.printf("Total Cost: %d\n", size);
                System.out.printf("Number of Partial solutions: %d\n", count);
                break;
            }
            // find possible next steps
            // O(n)
            JobAssignment[] possibleNextSteps = s.getPossibleNextSteps();

            // O(nlogn)
            // take a step in every direction
            for (JobAssignment assignment: possibleNextSteps) {
                Solution newSol = s.clone();

                newSol.add(assignment);
                count++;

                if (newSol.getMinCost() <= upperBound) {
                    minHeap.add(newSol);
                    // set upperBound to new (better) upperBound
                    if (newSol.getMaxCost() < upperBound) {
                        upperBound = newSol.getMaxCost();
                    }
                }
            }

            // prune off solutions whose lower bound is higher than upperBound
            // O(number of solutions to be pruned) <- big...
            minHeap.removeAllGreaterThan(upperBound);
        }
    }

    private static int globalUpperBound() {
        /**
         * arbitrarily choose a valid solution - i.e. choose the assignments
         * along the diagonal of JOB_DATA
         * 
         * return cost of that solution.
         */
        int sum = 0;
        for (int i = 0; i < JOB_DATA.length; i++) {
            sum += JOB_DATA[i][i];
        }
        return sum;
    }

    private static int globalUpperBound2() {
        int sum = 0;
        boolean[] assignedJobs = new boolean[JOB_DATA.length];
        for (int job = 0; job < JOB_DATA.length; job++) {
            assignedJobs[job] = false;
        }
        for (int zara = 0; zara < JOB_DATA.length; zara++) {
            int min = Integer.MAX_VALUE;
            for (int job = 0; job < JOB_DATA.length; job++) {
                if (!assignedJobs[job] && JOB_DATA[zara][job] < min) {
                    min = JOB_DATA[zara][job];
                }
            }
            sum += min;
        }
        return sum;
    }

    private static int globalLowerBound() {
        /**
         * return absolute best-case cost - i.e. naively sum min-length 
         * jobs for each zara
         * 
         */
        int sum = 0;
        for (int zara = 0; zara < JOB_DATA.length; zara++) {
            sum += JOB_DATA[zara][getMinJob(zara)];
        }
        return sum;

    }

    public static int getMinJob(int zara) {
        /**
         * perform a linear search for the smallest job available for zara
         * according to boolean[] jobsLeft. jobsLeft[i] == true iff the job
         * is available for zara.
         */
        int minJobLength = Integer.MAX_VALUE;
        int minJob = -1;
        
        boolean[] jobsLeft = new boolean[JOB_DATA.length];
        for (int i = 0; i < JOB_DATA.length; i++) {
            jobsLeft[i] = true;
        }
        for (int job = 0; job < JOB_DATA.length; job++) {
            if (jobsLeft[job] && JOB_DATA[zara][job] < minJobLength) {
                // System.out.println(minJobLength);
                minJobLength = JOB_DATA[zara][job];
                minJob = job;
            }
        }
        return minJob;
    }

    private static int[][] getJobData(String filename) {
        /**
         * retrieve data from file at filename
         * 
         * expect a file formatted as described:
         *
         * The first line gives the value of n, and each subsequent line
         * gives the job costs for a particular agent. Thus each row 
         * after the first contains n tab-separated non-negative integers.
         * 
         * e.g. 
         * 12
         * 9   1   17  6   8   4   12  4   3   9   12  4   
         * 12  8   20  2   20  9   11  3   15  4   7   1   
         * 7   20  20  15  1   19  18  16  4   2   9   18  
         * 12  15  5   11  15  17  17  5   18  5   17  12  
         * 4   12  11  20  1   4   20  17  4   5   11  4   
         * 7   1   15  2   20  19  16  16  8   14  16  10  
         * 2   13  7   13  9   20  5   16  14  12  2   4   
         * 20  5   4   6   11  11  1   1   10  4   13  16  
         * 2   11  9   16  20  9   5   18  1   7   8   4   
         * 14  11  5   9   14  7   2   10  4   13  5   3   
         * 18  7   6   11  14  14  16  6   9   8   11  12  
         * 13  2   8   15  1   15  14  9   9   6   15  10  
         */
        return parseFile(readFile(filename));
    }

    private static int[][] parseFile(String[] data) {
        /**
         * Return an 2d square array of integers of dimension n,
         * where n is the first line of the input data.
         * 
         * Expect String array of same length as the amount of
         * tab-separated numbers in each element, plus the first element
         * which is the number of following lines.
         * 
         * e.g. 
         * 12
         * 9   1   17  6   8   4   12  4   3   9   12  4   
         * 12  8   20  2   20  9   11  3   15  4   7   1   
         * 7   20  20  15  1   19  18  16  4   2   9   18  
         * 12  15  5   11  15  17  17  5   18  5   17  12  
         * 4   12  11  20  1   4   20  17  4   5   11  4   
         * 7   1   15  2   20  19  16  16  8   14  16  10  
         * 2   13  7   13  9   20  5   16  14  12  2   4   
         * 20  5   4   6   11  11  1   1   10  4   13  16  
         * 2   11  9   16  20  9   5   18  1   7   8   4   
         * 14  11  5   9   14  7   2   10  4   13  5   3   
         * 18  7   6   11  14  14  16  6   9   8   11  12  
         * 13  2   8   15  1   15  14  9   9   6   15  10  
         *
         */
        try {
            int n = Integer.parseInt(data[0]); // size on first line
            int[][] intData = new int[n][n];
            String[] tokens = null;
            
            for (int line = 0; line < n; line++) {
                tokens = data[line+1].split("\t");
                for (int num = 0; num < n; num++) {
                    intData[line][num] = Integer.parseInt(tokens[num]);
                }
            }
            return intData.clone();

        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid file format - see docs.");
            System.exit(1);
            return null; // apparently I need this...
        }
    }

    private static String[] readFile(String filename) {
        /**
         * return an array of Strings, one for each line of file given
         * by filename
         */
        List<String> records = new ArrayList<String>();
        try (BufferedReader reader =
                    new BufferedReader(new FileReader(filename))) {
            String line;
            while((line = reader.readLine()) != null) {
                records.add(line);
            }
        } catch (Exception e) {
            System.err.format("Could not read '%s'", filename);
            e.printStackTrace();
            return null;
        }
        String[] retVal = new String[records.size()];
        records.toArray(retVal);
        return retVal;
    }
}
