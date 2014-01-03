/**
 * Branch and Bound Java implementation by Jamie Macdonald for CMPE365
 * 06256541
 * 
 * Public Domain
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class MinHeap<E extends Measurable<E>> extends ArrayList<E> {
    /* A simple wrapper around a List to make it a binary min-heap. */
    public MinHeap() {
        super();
    }

    @Override
    public boolean add(E e) {
        /**
         * add element e, then percolate up
         * 
         * return true on success, false on failure to add element e.
         *
         * Override ArrayList<E>.add(E e)
         */
        if (!super.add(e)) {
            return false;
        }
        int i = this.size() - 1;
        int parent;
        while (i > 0) {
            parent = this.getParentIndex(i);

            if (this.get(i).compareTo(this.get(parent)) < 0) {
                this.swap(i, parent);
            } else {
                break;
            }
            i = parent;
        }
        return true;
    }

    @Override
    public E remove(int index) {
        /**
         * remove element from index.
         * 
         * return the element.
         *
         * Override ArrayList<E>.remove(E e)
         */
        if (this.size() <= 1 || index == this.size() - 1) {
            return super.remove(index);
        }
        // return value
        E retVal = this.get(index);
        this.set(index, super.remove(this.size() - 1));
        // now trickle last down, reheapifying
        // swap with smallest child
        int smallestChild;
        while (true) {
            smallestChild = this.getSmallestChild(index);
            if (smallestChild == -1) {
                // no children - we are a heap
                break;
            }
            if (this.get(index).compareTo(this.get(smallestChild)) > 0) {
                this.swap(index, smallestChild);
                index = smallestChild;
            } else {
                // locally satisfies heap property - done.
                break;
            }
        }
        return retVal;
    }

    public void removeAllGreaterThan(int upperBound) {
        /**
         * remove all nodes with value strictly greater than upperBound
         * 
         */
        LinkedList<Integer> fifo = new LinkedList<Integer>();
        // start at leaves
        for (int i = this.size()-1; i >= this.size()/2; i--) {
            if (this.get(i).getCost() > upperBound) {
                fifo.addLast(i);
            }
        }
        int prunee, parent;

        while (!fifo.isEmpty()) {

            prunee = fifo.removeFirst();
            parent = this.getParentIndex(prunee);
            if (this.get(parent).getCost() > upperBound) {
                if (fifo.peekLast() != parent) {
                    fifo.addLast(parent);
                }
            }
            this.remove(prunee);
        }
        return;
    }

    private int[] getChildren(int index) {
        int[] children;
        if (this.size() > 2*index + 2) {
            children = new int[2];
            children[0] = 2*index + 1;
            children[1] = 2*index + 2;
        } else if (this.size() > 2*index + 1) {
            children = new int[1];
            children[0] = 2*index + 1;
        } else {
            children = new int[0];
        }
        return children;

    }

    private int getSmallestChild(int index) {
        /**
         * return -1 if no child, otherwise return index of smallest child
         */
        int[] children = getChildren(index);
        if (children.length == 2) {
            // then index has two children, find min

            if (this.get(children[0]).compareTo(this.get(children[1])) < 0) {
                return children[0];
            } else {
                return children[1];
            }
        } else if (children.length == 1) {
            // then index has a single child
            return children[0];
        } else {
            // index has no child
            return -1;
        }
    }

    public int getParentIndex(int i) {
        /**
         * return the parent of input i
         */
        if (i % 2 == 1) {
            return (i - 1) / 2;
        } else {
            return (i - 2) / 2;
        }
    }

    private void swap(int i, int j) {
        /**
         * swap places of items indexed by i and j
         */
        E temp = this.get(i);
        this.set(i, this.get(j));
        this.set(j, temp);
    }

    public String toString() {
        /**
         * return e.g. "12, 13, 1, 20, "
         */
        String s = new String("");
        for (int i = 0; i < this.size(); i++) {
            s += this.get(i).getCost() + ", ";
        }
        return s;
    }

}