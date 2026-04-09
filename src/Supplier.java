/**
 * Represents a product supplier in the DSS e-commerce platform.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>inheritance</em>. Extends {@link User} to
 * inherit identity fields ({@code id}, {@code name}) and adds a {@code company} field
 * specific to supplier entities. The {@code is-a} relationship holds: a Supplier is a User.</p>
 *
 * <p><b>DSA Concept:</b> Instances are stored in the {@code Supplier[]} array in {@link Main}
 * and sorted by {@link AnalyticsEngine} using merge sort. Supplier statistics are computed
 * via a linear scan over the {@code Product[]} array in {@link Main#getSupplierStat}.</p>
 *
 * <p><b>Design Note:</b> Suppliers are linked to products by reference. Multiple products
 * can share the same supplier, enabling aggregate metric computation per company.</p>
 */
public class Supplier extends User {

    /**
     * The company name this supplier represents (e.g. "NexGen Ph").
     * Used as the grouping key when computing per-supplier aggregates
     * such as minimum product price and total stock.
     */
    private String company;

    /**
     * Constructs a {@code Supplier} with identity and company information.
     *
     * @param id      the unique identifier string for this supplier
     * @param name    the full name of the supplier contact/manager
     * @param company the name of the company this supplier represents
     */
    public Supplier(String id, String name, String company) {
        super(id, name);
        this.company = company;
    }

    /**
     * Returns the company name associated with this supplier.
     *
     * @return the supplier's company name as a {@code String}
     */
    public String getCompany() {
        return company;
    }
}
