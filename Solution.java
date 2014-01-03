/**
 * Branch and Bound Java implementation by Jamie Macdonald for CMPE365
 * 06256541
 * 
 * Public Domain
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

class Solution extends ArrayList<JobAssignment> implements Measurable<Solution> {
    /* A wrapper class around ArrayList<JobAssignment> to keep track
     * of partial potential solutions to the Job Assignment problem.
     */
    private int costSoFar = 0;
    private int maxFutureCost = Integer.MAX_VALUE; // essentially infinity
    private int minFutureCost = 0;

    public Solution(int lowerBound, int upperBound) {
        // initialize empty ArrayList<JobAssignment>
        super();
        this.minFutureCost = lowerBound;
        this.maxFutureCost = upperBound;
    }

    @Override
    public boolean add(JobAssignment assignment) {
        /**
         * add assignment to this Solution, then adjust cost estimates
         * 
         * return true on success, false on failure to add
         * 
         * Override ArrayList<E>.add(E e)
         */
        if (!super.add(assignment)) {
            return false;
        }

        int zara = assignment.getAssignee();
        int job = assignment.getJob();

        // update old min future cost estimate with current assignment
        // this.minFutureCost -= JobAssigner.JOB_DATA[zara][JobAssigner.getMinJob(zara)];
        this.costSoFar += assignment.getCost();
        
        // replace old max estimate with new estimate (does nothing for now)
        // implement greedy algorithm to choose potential solution
        this.maxFutureCost = this.maxFutureCost();
        this.minFutureCost = this.minFutureCost();

        return true;
    }

    @Override
    public JobAssignment remove(int index) {
        JobAssignment retVal = super.remove(index);
        int zara = retVal.getAssignee();
        int job = retVal.getJob();
        // undo add
        
        this.costSoFar -= retVal.getCost();
        this.minFutureCost += JobAssigner.JOB_DATA[zara][JobAssigner.getMinJob(zara)];

        this.maxFutureCost = this.maxFutureCost();
        this.minFutureCost = this.minFutureCost();

        return retVal;
    }

    private boolean[] getJobsLeft() {
        /**
         * generate boolean array of true if job is available (i.e. not in this
         * solution) and false if it is assigned in this solution.
         * 
         * do not store as attribute or else memory explodes.
         */
        boolean[] jobsLeft = new boolean[JobAssigner.JOB_DATA.length];
        Arrays.fill(jobsLeft, true);
        for (int i = 0; i < JobAssigner.JOB_DATA.length; i++ ) {
            for (JobAssignment assignment : this) {
                if (assignment.getJob() == i) {
                    jobsLeft[i] = false;
                }
            }
        }
        return jobsLeft;
    }

    public JobAssignment[] getPossibleNextSteps() {
        /**
         * Linear search for possible next steps.
         * 
         * return list of possible next job assignments
         * O(n)
         */
        if (this.size() == JobAssigner.JOB_DATA.length) {
            return new JobAssignment[0]; // save a little time
        }
        // genius
        int numNextSteps = JobAssigner.JOB_DATA.length - this.size();
        JobAssignment[] nextSteps = new JobAssignment[numNextSteps];
        boolean[] jobsLeft = this.getJobsLeft();
        int i = 0;
        int zara = this.size(); // the next zara
        for (int job = 0; job < jobsLeft.length; job++) {
            if (jobsLeft[job]) {
                // this job is not in this solution
                nextSteps[i] = new JobAssignment(zara, job);
                i++;
            }
        }
        
        return nextSteps;
    }

    public int getMinCost() {
        return costSoFar + this.minFutureCost;
    }

    public int getMaxCost() {
        return costSoFar + this.maxFutureCost;
    }

    private int minFutureCost() {
        boolean[] jobsLeft = this.getJobsLeft();
        int cost = 0;
        for (int zara = this.size(); zara < JobAssigner.JOB_DATA.length; zara++) {
            // min job
            int min = Integer.MAX_VALUE;
            for (int job = 0; job < jobsLeft.length; job++) {
                if (jobsLeft[job] && JobAssigner.JOB_DATA[zara][job] < min) {
                    min = JobAssigner.JOB_DATA[zara][job];
                }
            }
            cost += min;
        }
        return cost;
    }

    private int maxFutureCost() {
        // greedy algorithm eventually
        //List <Integer> localZarasLeft = zarasLeft.clone();
        // take minimum job length over all zaras left
        return maxFutureCost;
    }

    // private int maxFutureCost() {
    //     /**
    //      * greedy algorithm to find a good upper bound on future cost
    //      */
    //     int sum = 0;
    //     int minJob = 0;
    //     boolean[] jobsLeft = this.getJobsLeft();
    //     for (int zara = this.size(); zara < JobAssigner.JOB_DATA.length; zara++) {
    //         int min = Integer.MAX_VALUE;
    //         for (int job = 0; job < JobAssigner.JOB_DATA.length; job++) {
    //             if (jobsLeft[job] && JobAssigner.JOB_DATA[zara][job] < min) {
    //                 minJob = job;
    //                 min = JobAssigner.JOB_DATA[zara][job];
    //             }
    //         }
    //         jobsLeft[minJob] = false;
    //         sum += min;
    //     }
    //     return sum;
    // }

    @Override
    public int compareTo(Solution other) {
        /**
         * implement Measurable (sub-interface of Comparable) so MinHeap
         * knows ordering between Solutions
         */
        // compares by costSoFar + minFutureCost
        // breaks ties with lowest MaxCost
        int otherCost = other.getMinCost();
        int thisCost = this.getMinCost();
        if (thisCost < otherCost) {
            return -1;
        } else if (thisCost > otherCost) {
            return 1;
        } else if (this.getMaxCost() < other.getMaxCost()) {
            return -1;
        } else if (this.getMaxCost() > other.getMaxCost()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getCost() {
        /**
         * implement Measurable so MinHeap knows our lowest estimates
         */
        return getMinCost();
    }

    public void setCostSoFar(int val) {
        this.costSoFar = val;
    }
    public void setMaxFutureCost(int val) {
        this.maxFutureCost = val;
    }
    public void setMinFutureCost(int val) {
        this.minFutureCost = val;
    }

    public Solution clone() {
        Solution s = (Solution) super.clone();
        s.setCostSoFar(this.costSoFar);
        s.setMaxFutureCost(this.maxFutureCost);
        s.setMinFutureCost(this.minFutureCost);
        return s;
    }

    // @Override
    // public boolean equals(Object other) {
    //     Solution j;
    //     if (other instanceof Solution) {
    //         j = (Solution) other;
    //     } else {
    //         return false;
    //     }
    //     int count = 0;
    //     for (JobAssignment assignment : this) {
    //         if (j.contains(assignment)) {
    //             return false;
    //         }
    //         count ++;
    //     }
    //     if (count != j.size()) {
    //         return false;
    //     }
    //     return true;
    // }
}