* The system will read in a list of X numbers, and will generate a minimal product-sum number for each.

* A minimal product-sum number is a natural number N, that can be written as the sum and product of the given set of at least two natural numbers.

* Benchmark Test Results:
  Originial Implementation: 87.67ms
  Improved Implementation: 5.49ms
  Performance Improvement: 93.74%

[Link to Benchmark Test]([https://github.com/owenpacc07/CPS353GroupAssignments/blob/main/test/benchmark/ComputeEngineBenchmarkTest.java])

- For a given k, the smallest possible product-sum number must be >= k. So the starting point for the iterator was changed from 4 to k to reduce the search space and speed up the algorithm.

* Example input/output:

  Input "4"
  1 x 1 x 2 x 4 = 1 + 1 + 2 + 4; 4 factors, 4 addends. result is 8

  Output "8"

  IO w/ Delimiters 4:8;

  - Default Delimiter Notation:

  - Separate Input/Output ":"

  - Output ";"


![System Diagram](https://github.com/owenpacc07/CPS353GroupAssignments/blob/main/images/systemDiagram.png?raw=true)

  

