/**
 * Represents a loyalty consumer (customer) in the DSS e-commerce platform.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>inheritance</em> and <em>encapsulation</em>.
 * Extends {@link User} to inherit {@code id} and {@code name}, and encapsulates the
 * consumer-specific {@code points} field with controlled access via a getter.</p>
 *
 * <p><b>DSA Concept:</b> Instances are stored in the {@code Consumer[]} array in {@link Main}
 * and sorted by {@link AnalyticsEngine} using merge sort on the {@code points} field.
 * The sort produces a ranked loyalty leaderboard in O(n log n) time.</p>
 *
 * <p><b>Design Note:</b> {@code points} is the sole numeric metric for consumers,
 * so the {@link AnalyticsEngine} always dispatches to it regardless of the criteria string
 * when operating on consumer data.</p>
 */
public class Consumer extends User {

    /**
     * The accumulated loyalty points balance for this consumer.
     * Higher values indicate more engaged or higher-spending customers.
     * Used as the sole sort key in the Consumer Loyalty dashboard.
     */
    private int points;

    /**
     * Constructs a {@code Consumer} with identity and loyalty point data.
     *
     * @param id     the unique identifier string for this consumer
     * @param name   the full display name of this consumer
     * @param points the initial loyalty point balance (non-negative integer)
     */
    public Consumer(String id, String name, int points) {
        super(id, name);
        this.points = points;
    }

    /**
     * Returns the current loyalty point balance of this consumer.
     *
     * @return the consumer's loyalty points as an {@code int}
     */
    public int getPoints() {
        return points;
    }
}
