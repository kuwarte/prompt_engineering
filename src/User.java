/**
 * Abstract base class representing a system user in the DSS e-commerce platform.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>abstraction</em> and <em>inheritance</em>.
 * Common identity fields ({@code id}, {@code name}) are defined here and shared by all
 * concrete subclasses ({@link Supplier}, {@link Consumer}), avoiding code duplication
 * and enforcing a common interface across user types.</p>
 *
 * <p><b>DSA Concept:</b> Not directly applicable; serves as a structural base for
 * polymorphic arrays sorted by {@link AnalyticsEngine}.</p>
 *
 * <p><b>Design Note:</b> Declared abstract to prevent direct instantiation — only
 * meaningful subtypes ({@code Supplier}, {@code Consumer}) should exist in the system.</p>
 */
public abstract class User {

    /**
     * Unique string identifier for this user (e.g. "S0", "C3").
     * Loaded from {@code data.json} and used for internal tracking.
     */
    protected String id;

    /**
     * Full display name of the user, rendered in dashboard table rows.
     */
    protected String name;

    /**
     * Constructs a {@code User} with the given identity fields.
     *
     * @param id   the unique identifier string for this user
     * @param name the display name of this user
     */
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the display name of this user.
     *
     * @return the user's full name as a {@code String}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return the user's ID string
     */
    public String getId() {
        return id;
    }
}
