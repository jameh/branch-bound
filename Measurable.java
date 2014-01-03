/**
 * Branch and Bound Java implementation by Jamie Macdonald for CMPE365
 * 06256541
 * 
 * Public Domain
 */

interface Measurable<E> extends Comparable<E> {
    public int getCost();
}