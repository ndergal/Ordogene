Fitness checking examples :
===========================

fitness1 :
==========

The BIG_GOOD fitness coefficient is more than 10 times better than the
SMALL_BAD fitness coefficient. The best strategy is thus to produce
BIG_GOOD as much as possible and after adding some SMALL_BAD if there
still is some fuel.

3 BIG_GOOD and 3 SMALL_BAD have to be produced.

best supposed fitness : 36

fitness2 :
==========

This experience is a clone of the fitness1 experience. But, this time
The BIG_GOOD fitness coefficient is strictly less than 10 times the
SMALL_BAD fitness coefficient. For this experience, BIG_GOOD are no
more competitive. All the fuel must disappear when producing
SMALL_BAD.

33 SMALL_BAD have to be produced.

best supposed fitness : 33

fitness3 :
==========

In this experience, the best solution is trying to produce
GOOD. However, to produce GOOD, we need first to consume the fuel in
BAD. There is a trap, one can produce MIDDLE from the fuel. MIDDLE is
a dead end entity with nice fitness coefficient but not the best one.

first produced 20 BAD thus produce 20 GOOD with the 20 BAD. Never produce MIDDLE.

best supposed fitness : 160

fitness4 :
==========

This experience is a long chain of low ratted entities which can
enable the production of GOOD after. The same bad trap with MIDDLE
entity is propose as in experience fitness3. The easy and short path
is not the good one.

FUEL --> MIDDLE (6)
FUEL --> BAD1 (5) --> BAD2 (4) --> BAD3 (3) --> BAD4 (2) --> BAD5 (1) --> GOOD (8)

At the end, we should have produced 20 GOOD.

best supposed fitness : 160

fitness5 :
==========

This experience propose 5 final entities which can be produced from
the fuel. The bigger are the entities, the better they are for the
fitness. Let us calculate the rate of fitness coefficient / cost of
producing.

600 FUEL --> 1 VERY_GOOD (780) (rate : 780/600 = 1.3)
70 FUEL --> 1 GOOD (90) (rate : 90/70 = 1.285...)
35 FUEL --> 1 MIDDLE (42) (rate : 42/35 = 1.2)
6 FUEL --> 1 BAD (7) (rate : 7/6 = 1.166...)
1 FUEL --> 1 VERY_BAD (1) (rate : 1/1 = 1)

So, mathematically, the best solution is to produce VERY_GOOD as much
as possible, thus producing GOOD with the remaining fuel, thus
producing MIDDLE, etc...

with 2000 fuel :
3 VERY_GOOD (it remains 200 fuel)
2 GOOD (it remains 60 fuel)
1 MIDDLE (it remains 25 fuel)
4 BAD (it remains 1 fuel)
1 VERY_BAD (no more fuel)

best supposed fitness : 2591 = (3×780)+(2×90)+(1×42)+(4×7)+(1x1)


Tree spanning examples :
========================

Let us define a Fibonacci dummy function as follow :
(1 core operation) : fibo(0) = 1
(1 core operation) : fibo(1) = 1
(1 core operation) : fibo(n) = fibo(n-1) + fibo(n-2)
The function is explosively recursive. Partial results are not stored.

fibonacci_dummy_X_core :
========================

The goal is to compute fibonacci(6) using X core(s).

Some variables or the fitness may be adjusted to bound the example.


Tree searching examples :
=========================

Let us define a cached Fibonacci function as follow :
(1 core operation) : fibo(0) = 1 (thus stored)
(1 core operation) : fibo(1) = 1 (thus stored)
(1 core operation) : fibo(n) = fibo(n-1) + fibo(n-2) (does not consume the operands)
Partial results are stored.

This time, all values should be computed just a single time. The
complexity of production of a high level value of Fibonacci become
linear from exponential.

fibonacci_cached :
==================

The goal is to compute the cached version of fibonacci(10) using a
single core.

Some variables or the fitness may be adjusted to bound the example.


short_path_10 :
===============

To produced the goal : the shortest path need 5 steps

best supposed fitness : 200 - 5 + 25 = 220


short_path_20 :
===============

To produced the goal : the shortest path need 7 steps

best supposed fitness : 200 - 7 + 35 = 228


short_path_50 :
===============

To produced the goal : the shortest path need 10 steps

best supposed fitness : 200 - 10 + 50 = 240


short_path_100 :
===============

To produced the goal : the shortest path need 13 steps

best supposed fitness : 200 - 13 + 65 = 252


short_path_200 :
===============

To produced the goal : the shortest path need 22 steps

best supposed fitness : 200 - 22 + 110 = 288


short_path_500 :
===============

To produced the goal : the shortest path need 33 steps

best supposed fitness : 200 - 33 + 165 = 332



complete examples :
===================

small_strategy_game :
=====================

A complete example with 5 entities. There some cycle in the
dependencies of production and random solution may be very bad.

To be practical and bounded, perhaps some variables should be wisely
set ("slots" : ?, "exec_time" : ?).


river_enigma :
==============

1 boat with 2 possible sits and four people A,B,C and D who want to
cross a river.

Only two solutions seems optimal in time :

AB (2) --> A_back (1) --> CD (10) --> B_back (2) --> AB (2) : 17 time unity
AB (2) --> B_back (2) --> CD (10) --> A_back (1) --> AB (2) : 17 time unity
