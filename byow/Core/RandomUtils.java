package byow.Core;

import java.util.Random;

/**
 * A utility class providing a set of static methods to generate pseudo-random numbers.
 * It supports various distributions including Bernoulli, Uniform, Gaussian, and others.
 * Methods are also provided for shuffling arrays and generating random integers within specified ranges.
 */
public class RandomUtils {

    /**
     * Returns a random real number uniformly in [0, 1).
     *
     * @param random a Random object to generate the random number
     * @return a random real number uniformly in [0, 1)
     */
    public static double uniform(Random random) {
        return random.nextDouble();
    }

    /**
     * Returns a random integer uniformly in [0, n).
     *
     * @param random a Random object to generate the random number
     * @param n the upper bound for the random number
     * @return a random integer uniformly between 0 (inclusive) and {@code n} (exclusive)
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public static int uniform(Random random, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("argument must be positive: " + n);
        }
        return random.nextInt(n);
    }

    /**
     * Returns a random long integer uniformly in [0, n).
     *
     * @param random a Random object to generate the random number
     * @param n the upper bound for the random number
     * @return a random long integer uniformly between 0 (inclusive) and {@code n} (exclusive)
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public static long uniform(Random random, long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("argument must be positive: " + n);
        }

        // Adjusts to avoid biases for large n values
        long r = random.nextLong();
        long m = n - 1;

        // Handle edge cases and power-of-two optimizations
        if ((n & m) == 0L) {
            return r & m;
        }

        // Reject over-represented candidates (handling bias)
        long u = r >>> 1;
        while (u + m - (r = u % n) < 0L) {
            u = random.nextLong() >>> 1;
        }
        return r;
    }

    /**
     * Returns a random integer uniformly in [a, b).
     *
     * @param random a Random object to generate the random number
     * @param a the lower bound of the range
     * @param b the upper bound of the range
     * @return a random integer uniformly in [a, b)
     * @throws IllegalArgumentException if {@code b <= a} or if the range is too large
     */
    public static int uniform(Random random, int a, int b) {
        if ((b <= a) || ((long) b - a >= Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("invalid range: [" + a + ", " + b + ")");
        }
        return a + uniform(random, b - a);
    }

    /**
     * Returns a random real number uniformly in [a, b).
     *
     * @param random a Random object to generate the random number
     * @param a the lower bound of the range
     * @param b the upper bound of the range
     * @return a random real number uniformly in [a, b)
     * @throws IllegalArgumentException if {@code a >= b}
     */
    public static double uniform(Random random, double a, double b) {
        if (!(a < b)) {
            throw new IllegalArgumentException("invalid range: [" + a + ", " + b + ")");
        }
        return a + uniform(random) * (b - a);
    }

    /**
     * Returns a random boolean from a Bernoulli distribution with success
     * probability p.
     *
     * @param random a Random object to generate the random number
     * @param p the probability of returning true
     * @return {@code true} with probability {@code p}, {@code false} otherwise
     * @throws IllegalArgumentException if {@code p} is not between 0.0 and 1.0
     */
    public static boolean bernoulli(Random random, double p) {
        if (!(p >= 0.0 && p <= 1.0)) {
            throw new IllegalArgumentException("probability p must be between 0.0 and 1.0: " + p);
        }
        return uniform(random) < p;
    }

    /**
     * Returns a random boolean from a Bernoulli distribution with success
     * probability 1/2.
     *
     * @param random a Random object to generate the random number
     * @return {@code true} with probability 1/2, {@code false} otherwise
     */
    public static boolean bernoulli(Random random) {
        return bernoulli(random, 0.5);
    }

    /**
     * Returns a random integer from a Poisson distribution with mean lambda.
     *
     * @param random a Random object to generate the random number
     * @param lambda the mean of the Poisson distribution
     * @return a random integer from a Poisson distribution with mean {@code lambda}
     * @throws IllegalArgumentException if {@code lambda <= 0}
     */
    public static int poisson(Random random, double lambda) {
        if (!(lambda > 0.0)) {
            throw new IllegalArgumentException("lambda must be positive: " + lambda);
        }
        int k = 0;
        double p = 1.0;
        double expLambda = Math.exp(-lambda);
        do {
            k++;
            p *= uniform(random);
        } while (p >= expLambda);
        return k - 1;
    }

    /**
     * Rearranges the elements of the specified array in uniformly random order.
     *
     * @param random a Random object to generate the random numbers
     * @param a the array to shuffle
     * @throws IllegalArgumentException if {@code a} is null
     */
    public static void shuffle(Random random, Object[] a) {
        validateNotNull(a);
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int r = i + uniform(random, n - i); // between i and n-1
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    // Additional helper methods
    private static void validateNotNull(Object x) {
        if (x == null) {
            throw new IllegalArgumentException("argument is null");
        }
    }
}
