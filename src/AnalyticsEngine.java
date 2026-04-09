import java.util.Arrays;

/**
 * Core sorting and data-access engine for the DSS analytics platform.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>encapsulation</em>. All mutable state
 * (data array, criteria, direction, busy flag, speed) is private and managed
 * internally. External callers interact only through {@link #sort}, {@link #getVal},
 * and state query methods.</p>
 *
 * <p><b>DSA Concept:</b> Implements <em>merge sort</em>, a classic divide-and-conquer
 * algorithm with O(n log n) time complexity in all cases. The array is recursively
 * split into halves until base cases of size 1 are reached, then merged back in
 * sorted order. Recursion depth is O(log n). Space complexity is O(n) due to
 * temporary arrays allocated during the merge step.</p>
 *
 * <p><b>Design Note:</b> The {@code volatile} keyword on {@code isBusy} ensures
 * visibility across threads in case the rendering thread checks the flag while
 * the sort runs. The {@code activeDashboard} reference in {@link Main} is updated
 * before sort is called, so every merge step triggers a live re-render.</p>
 */
public class AnalyticsEngine {

    /** ANSI reset code — clears all color and formatting. */
    public static final String RESET  = "\u001B[0m";

    /** ANSI bold formatting code. */
    public static final String BOLD   = "\033[1m";

    /** ANSI yellow foreground — used to highlight elements being compared. */
    public static final String YELLOW = "\u001B[33m";

    /** ANSI green foreground — used to highlight elements that have been placed. */
    public static final String GREEN  = "\u001B[32m";

    /**
     * The data array being sorted. Holds {@link Product}, {@link Consumer},
     * or {@link Supplier} objects depending on which dashboard is active.
     */
    private Object[] data;

    /**
     * The active sort criteria string. Valid values depend on data type:
     * {@code "PRICE"}, {@code "RATING"}, {@code "SALES"}, {@code "STOCK"} for products;
     * {@code "POINTS"} for consumers; {@code "PRICE"} or {@code "STOCK"} for suppliers.
     */
    private String criteria = "DEFAULT";

    /**
     * Sort direction flag. {@code true} = ascending, {@code false} = descending.
     */
    private boolean ascending = true;

    /**
     * Volatile busy flag. Set to {@code true} during an active sort to suppress
     * the keybinding hint bar in the dashboard render. Volatile ensures cross-thread
     * visibility without requiring synchronization.
     */
    private volatile boolean isBusy = false;

    /**
     * Animation delay in milliseconds between each render step during sorting.
     * Lower values produce faster animations.
     */
    private long speed = 50;

    /**
     * Constructs an {@code AnalyticsEngine} bound to the given data array.
     *
     * @param data the object array to be sorted; elements must be
     *             {@link Product}, {@link Consumer}, or {@link Supplier} instances
     */
    public AnalyticsEngine(Object[] data) {
        this.data = data;
    }

    /**
     * Initiates a merge sort on the internal data array using the given criteria and direction.
     * Sets {@code isBusy} to {@code true} before sorting and {@code false} after.
     *
     * @param criteria the field name to sort by (e.g. {@code "PRICE"}, {@code "SALES"})
     * @param asc      {@code true} for ascending order, {@code false} for descending
     */
    public void sort(String criteria, boolean asc) {
        this.criteria = criteria;
        this.ascending = asc;
        this.isBusy = true;
        mergeSort(data, 0, data.length - 1);
        this.isBusy = false;
    }

    /**
     * Recursively divides the array into halves and sorts each half before merging.
     *
     * <p><b>Divide and Conquer Breakdown:</b></p>
     * <ul>
     *   <li><b>Divide:</b> The array segment [l, r] is split at midpoint {@code m = l + (r-l)/2}.
     *       Using {@code (r-l)/2} instead of {@code (l+r)/2} avoids integer overflow for large indices.</li>
     *   <li><b>Conquer:</b> {@code mergeSort} is called recursively on [l, m] and [m+1, r].
     *       This continues until segments of size 1 are reached (base case: {@code l >= r}).</li>
     *   <li><b>Combine:</b> {@link #merge} is called to merge the two sorted halves in O(n) time.</li>
     * </ul>
     *
     * <p><b>Recursion Depth:</b> O(log n) — each call halves the problem size.</p>
     * <p><b>Time Complexity:</b> O(n log n) in all cases (best, average, worst).</p>
     * <p><b>Space Complexity:</b> O(n) — temporary arrays are allocated in {@link #merge}.</p>
     *
     * @param arr the array being sorted
     * @param l   the left boundary index (inclusive)
     * @param r   the right boundary index (inclusive)
     */
    private void mergeSort(Object[] arr, int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }

    /**
     * Merges two adjacent sorted sub-arrays [l..m] and [m+1..r] into a single sorted segment.
     *
     * <p><b>Algorithm:</b></p>
     * <ol>
     *   <li>Copy both halves into temporary arrays {@code L} and {@code R}.</li>
     *   <li>Use two pointers {@code i} (left half) and {@code j} (right half) to compare elements.</li>
     *   <li>Before each comparison, call {@code render(l+i, m+1+j, false)} to highlight both
     *       candidates in <b>yellow</b> on the active dashboard.</li>
     *   <li>Place the smaller (or larger, depending on direction) element into {@code arr[k]},
     *       then call {@code render(k-1, -1, true)} to highlight the placed element in <b>green</b>.</li>
     *   <li>Call {@link #sleep()} after each render to pace the animation.</li>
     *   <li>Drain remaining elements from whichever half still has entries.</li>
     * </ol>
     *
     * <p><b>Time Complexity:</b> O(n) per merge call, where n = r - l + 1.</p>
     *
     * @param arr the full array being sorted
     * @param l   left start index of the first sub-array
     * @param m   end index of the first sub-array (and mid-point)
     * @param r   end index of the second sub-array
     */
    private void merge(Object[] arr, int l, int m, int r) {
        Object[] L = Arrays.copyOfRange(arr, l, m + 1);
        Object[] R = Arrays.copyOfRange(arr, m + 1, r + 1);
        int i = 0, j = 0, k = l;

        while (i < L.length && j < R.length) {
            Main.activeDashboard.render(l + i, m + 1 + j, false);
            if (compare(L[i], R[j])) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
            sleep();
            Main.activeDashboard.render(k - 1, -1, true);
        }
        while (i < L.length) {
            arr[k++] = L[i++];
            Main.activeDashboard.render(k - 1, -1, true);
            sleep();
        }
        while (j < R.length) {
            arr[k++] = R[j++];
            Main.activeDashboard.render(k - 1, -1, true);
            sleep();
        }
    }

    /**
     * Compares two objects using the active sort criteria and direction.
     * Delegates value extraction to {@link #getVal(Object)}.
     *
     * @param a the first object to compare
     * @param b the second object to compare
     * @return {@code true} if {@code a} should come before {@code b} in sorted order
     */
    private boolean compare(Object a, Object b) {
        double v1 = getVal(a);
        double v2 = getVal(b);
        return ascending ? v1 <= v2 : v1 >= v2;
    }

    /**
     * Extracts the numeric sort value from an object based on the active criteria string.
     *
     * <p>Dispatches by runtime type using {@code instanceof} pattern matching (Java 16+):</p>
     * <ul>
     *   <li>{@link Product}: returns price, rating, stock, or sales depending on {@code criteria}</li>
     *   <li>{@link Consumer}: always returns loyalty points</li>
     *   <li>{@link Supplier}: delegates to {@link Main#getSupplierStat} for aggregate computation</li>
     * </ul>
     *
     * @param obj the object whose sort value is to be extracted
     * @return the numeric value used for comparison as a {@code double}
     */
    public double getVal(Object obj) {
        if (obj instanceof Product p) {
            return switch (criteria) {
                case "PRICE"  -> p.getPrice();
                case "RATING" -> p.getRating();
                case "STOCK"  -> p.getStock();
                default       -> p.getSales();
            };
        } else if (obj instanceof Consumer c) {
            return c.getPoints();
        } else if (obj instanceof Supplier s) {
            return Main.getSupplierStat(s, criteria);
        }
        return 0;
    }

    /**
     * Pauses execution for the configured animation speed duration.
     * Swallows {@link InterruptedException} to avoid propagating it
     * through the sort call stack.
     */
    private void sleep() {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns whether a sort operation is currently in progress.
     * @return {@code true} if sorting is active, {@code false} otherwise
     */
    public boolean isBusy() { return isBusy; }

    /**
     * Returns the current data array held by this engine.
     * @return the object array being sorted
     */
    public Object[] getData() { return data; }

    /**
     * Returns the active sort criteria string.
     * @return the criteria label (e.g. {@code "PRICE"}, {@code "SALES"})
     */
    public String getCriteria() { return criteria; }

    /**
     * Returns the active sort direction.
     * @return {@code true} if ascending, {@code false} if descending
     */
    public boolean isAsc() { return ascending; }
}
