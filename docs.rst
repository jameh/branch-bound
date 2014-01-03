********************************
Docs for B+B Java Implementation
********************************
Usage
=====

.. code-block:: bash

    javac JobAssigner.java

    java JobAssigner 'B+B Data 20131116 1902.txt'

Overview
========

The main class is ``JobAssigner``. This holds the main loop, the ``JOB_DATA``, handles the file I/O, and calculates global upper and lower bounds for our solutions.

The ``Solution`` class is a wrapper around an ``ArrayList<JobAssignment>``, and has some utility functions to make B+B easier to write from the main class.

A ``JobAssignment`` is just a two-tuple of integers: ``job`` and ``assignee``. These represent part of a ``Solution``.

``MinHeap<E extends Measurable<E>>``  is a wrapper around ``ArrayList<E>``. It's a generic class, whose generic type is required to implement interface ``Measurable``. ``Solution`` implements ``Measurable``, so we use ``MinHeap`` to store our solutions, scored by their ``costSoFar + minFutureCost``. This helps the main iteration pull out the solutions "best-first".

Performance
===========

This program works well on the first (dimension 12) input file, ``B+B Data 20131116 1902.txt``, but does not finish in reasonable time on the second (dimension 60) input file, ``B+B Data 20131116 2125.txt``.

I found that on the first file, implementing a greedy algorithm for calculating upper bounds lowered the total number of iterations needed to find the solution. 

Using the dimension 60 file as input, the program will run for more than 1h50m on one of my 2.6 GHz cores, and slowly work its way up to using maximum allocated Heap space. Performing a Heap Dump, most of this memory seems to be simply from the MinHeap that keeps growing with every main loop iteration.

.. note::
    In the main loop, every loop, we remove all nodes from the MinHeap whose lowerBound is greater than our globalUpperBound. If this step is skipped, since it is technically unecessary, the data structure grows much faster, and we run into the memory limits of Java very quickly (~15 minutes).