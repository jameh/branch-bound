/**
 * Branch and Bound Java implementation by Jamie Macdonald for CMPE365
 * 06256541
 * 
 * Public Domain
 */

class JobAssignment {
    /**
     * Simple class with two attributes representing a job assignment
     */
    private int assignee, job;
    public JobAssignment(int assignee, int job) {
        this.assignee = assignee;
        this.job = job;
    }

    public int getCost() {
        return JobAssigner.JOB_DATA[this.assignee][this.job];
    }

    // public int compareTo(JobAssignment other) {
    //     int thisCost = this.getCost();
    //     int otherCost = other.getCost();
    //     return thisCost < otherCost ? -1 : thisCost > otherCost ? 1 : 0
    // }

    public int getAssignee() {
        return this.assignee;
    }

    public int getJob() {
        return this.job;
    }

    public String toString() {
        return "assignee: " + this.assignee + " job: " + this.job;
    }
}