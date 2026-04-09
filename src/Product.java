/**
 * Represents a product listing in the DSS e-commerce platform.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>encapsulation</em>. All fields are private
 * and exposed only through public getters, protecting the internal state from
 * uncontrolled modification. Also demonstrates <em>composition</em> — a Product
 * holds a reference to its {@link Supplier}, establishing a has-a relationship.</p>
 *
 * <p><b>DSA Concept:</b> Product instances are stored in a fixed-size array and sorted
 * by {@link AnalyticsEngine} using merge sort. The sort key is selected dynamically
 * at runtime by {@link AnalyticsEngine#getVal(Object)} based on the active criteria
 * string ("PRICE", "RATING", "SALES", "STOCK").</p>
 *
 * <p><b>Design Note:</b> The {@code supplier} field is a direct object reference (not
 * an ID string), allowing the dashboard to display supplier company names without
 * an additional lookup step.</p>
 */
public class Product {

    /** The display name of this product, shown in the Product Analytics dashboard. */
    private String name;

    /**
     * The retail price of this product in Philippine Peso (PHP).
     * Used as a sort key when criteria is "PRICE".
     */
    private double price;

    /**
     * The average customer rating of this product on a 1.0–5.0 scale.
     * Used as a sort key when criteria is "RATING".
     */
    private double rating;

    /**
     * The total number of units sold for this product.
     * Used as the default sort key ("SALES") and as the bar chart metric.
     */
    private int sales;

    /**
     * The current inventory stock level for this product.
     * Used as a sort key when criteria is "STOCK".
     */
    private int stock;

    /**
     * Reference to the {@link Supplier} responsible for this product.
     * Enables company-level aggregate queries via {@link Main#getSupplierStat}.
     */
    private Supplier supplier;

    /**
     * Constructs a {@code Product} with all required catalog and inventory fields.
     *
     * @param name     the display name of this product
     * @param price    the retail price in PHP
     * @param rating   the average customer rating (1.0 to 5.0)
     * @param sales    the total number of units sold
     * @param stock    the current inventory stock count
     * @param supplier the {@link Supplier} linked to this product
     */
    public Product(String name, double price, double rating, int sales, int stock, Supplier supplier) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.sales = sales;
        this.stock = stock;
        this.supplier = supplier;
    }

    /**
     * Returns the display name of this product.
     * @return product name as a {@code String}
     */
    public String getName() { return name; }

    /**
     * Returns the retail price of this product.
     * @return price in PHP as a {@code double}
     */
    public double getPrice() { return price; }

    /**
     * Returns the average customer rating of this product.
     * @return rating on a 1.0–5.0 scale as a {@code double}
     */
    public double getRating() { return rating; }

    /**
     * Returns the total units sold for this product.
     * @return sales count as an {@code int}
     */
    public int getSales() { return sales; }

    /**
     * Returns the current inventory stock level.
     * @return stock count as an {@code int}
     */
    public int getStock() { return stock; }

    /**
     * Returns the {@link Supplier} linked to this product.
     * @return the associated {@code Supplier} object
     */
    public Supplier getSupplier() { return supplier; }
}
