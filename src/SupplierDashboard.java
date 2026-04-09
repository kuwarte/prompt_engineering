/**
 * Concrete dashboard implementation for the Supplier Metrics panel.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>runtime polymorphism</em> through method overriding.
 * {@link #render} overrides the abstract contract in {@link Dashboard} with a supplier-specific
 * layout showing company names, owner names, and computed metric values. The
 * {@code Main.activeDashboard} reference dispatches to this implementation at runtime.</p>
 *
 * <p><b>DSA Concept:</b> Supplier sort values are computed on demand by
 * {@link Main#getSupplierStat}, which performs a linear scan over the full product array —
 * O(p) per supplier, O(n × p) total across a sort pass. The bar chart also performs
 * an O(n) max scan via {@link Dashboard#buildBar}.</p>
 *
 * <p><b>Design Note:</b> Supplier metrics are derived (not stored), computed from linked
 * product data at render time. The {@code visualLength} calibrator (107) is tuned for
 * this dashboard's five-column layout.</p>
 */
public class SupplierDashboard extends Dashboard {

    /**
     * Constructs a {@code SupplierDashboard} bound to the given engine.
     *
     * @param engine the {@link AnalyticsEngine} managing supplier data
     */
    public SupplierDashboard(AnalyticsEngine engine) {
        super(engine, "SUPPLIER METRICS");
    }

    /**
     * Renders the Supplier Metrics dashboard frame to the terminal.
     *
     * <p><b>Runtime Polymorphism:</b> Dispatched to at runtime when the active dashboard
     * is a {@code SupplierDashboard}, demonstrating polymorphic behavior through the
     * abstract {@link Dashboard} reference in {@code Main.activeDashboard}.</p>
     *
     * <p>Columns rendered: ID | COMPANY | OWNER | METRIC VALUE | STATUS BAR</p>
     *
     * <p>The metric value column reflects the currently active criteria:
     * minimum product price (PRICE) or total product stock (STOCK).</p>
     *
     * @param a1     index of the first animated element
     * @param a2     index of the second animated element, or {@code -1}
     * @param merged {@code true} = green (placed), {@code false} = yellow (comparing)
     */
    @Override
    public void render(int a1, int a2, boolean merged) {
        printFrameHeader();
        System.out.printf("║ %-4s | %-25s | %-20s | %-15s | %-32s ║%n",
                "ID", "COMPANY", "OWNER", "METRIC VALUE", "STATUS BAR");
        System.out.println("╠" + "═".repeat(TOTAL_WIDTH) + "╣");

        Object[] data = engine.getData();
        for (int i = 0; i < data.length; i++) {
            Supplier s = (Supplier) data[i];
            String color = (i == a1 || i == a2)
                    ? (merged ? AnalyticsEngine.GREEN : AnalyticsEngine.YELLOW)
                    : AnalyticsEngine.RESET;

            double val    = engine.getVal(s);
            String id     = pad(String.format("[%02d]", i + 1), 4);
            String comp   = pad(s.getCompany(), 25);
            String owner  = pad(s.getName(), 20);
            String metric = pad(String.format("%.2f", val), 15);
            String bar    = buildBar(val, color);

            StringBuilder row = new StringBuilder("║ ");
            row.append(color).append(id).append(AnalyticsEngine.RESET)
               .append(" | ").append(comp)
               .append(" | ").append(owner)
               .append(" | ").append(metric)
               .append(" | ").append(bar);

            // visualLength: calibrated for this dashboard's five-column layout
            int visualLength = 107;
            row.append(" ".repeat(Math.max(0, TOTAL_WIDTH - visualLength))).append("║");
            System.out.println(row);
        }

        System.out.println("╚" + "═".repeat(TOTAL_WIDTH) + "╝");
        if (!engine.isBusy()) {
            System.out.println(" [P] Min Price  [T] Total Stock  |  [A] Ascending  [D] Descending  |  [Q] Back");
            System.out.print(" Action > ");
        }
    }
}
