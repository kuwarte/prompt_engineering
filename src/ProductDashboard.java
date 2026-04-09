/**
 * Concrete dashboard implementation for the Product Analytics panel.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>runtime polymorphism</em> through method overriding.
 * {@link #render} overrides the abstract method defined in {@link Dashboard}, providing a
 * product-specific column layout (name, price, rating, sales, bar). When
 * {@code Main.activeDashboard.render(...)} is called during animation, the JVM dispatches
 * to this implementation if a {@code ProductDashboard} is active — without the caller
 * knowing the concrete type.</p>
 *
 * <p><b>DSA Concept:</b> Inherits the O(n) bar-scaling scan from {@link Dashboard#buildBar}.
 * The column layout renders data from the array in its current sorted order after each
 * merge step, visually conveying the progress of merge sort in real time.</p>
 *
 * <p><b>Design Note:</b> The {@code visualLength} calibrator (96) compensates for the
 * fixed number of non-ANSI visible characters per row so the right {@code ║} border
 * always lands at column {@code TOTAL_WIDTH + 2}.</p>
 */
public class ProductDashboard extends Dashboard {

    /**
     * Constructs a {@code ProductDashboard} bound to the given engine.
     *
     * @param engine the {@link AnalyticsEngine} managing product data
     */
    public ProductDashboard(AnalyticsEngine engine) {
        super(engine, "PRODUCT ANALYTICS");
    }

    /**
     * Renders the Product Analytics dashboard frame to the terminal.
     *
     * <p><b>Runtime Polymorphism:</b> This override is dispatched to at runtime via
     * the {@link Dashboard} reference in {@code Main.activeDashboard}, demonstrating
     * polymorphic behavior — the caller uses the abstract type, the JVM selects this
     * concrete implementation.</p>
     *
     * <p>Columns rendered: ID | NAME | PRICE | RATING | SALES | PERFORMANCE BAR</p>
     *
     * <p>During sort animation, the row at index {@code a1} or {@code a2} is colored:
     * yellow ({@code merged=false}) for comparison, green ({@code merged=true}) for placement.</p>
     *
     * @param a1     index of the first animated element
     * @param a2     index of the second animated element, or {@code -1}
     * @param merged {@code true} = green (placed), {@code false} = yellow (comparing)
     */
    @Override
    public void render(int a1, int a2, boolean merged) {
        printFrameHeader();
        System.out.printf("║ %-4s | %-20s | %-12s | %-6s | %-8s | %-44s║%n",
                "ID", "NAME", "PRICE (PHP)", "RATE", "SALES", "PERFORMANCE BAR");
        System.out.println("╠" + "═".repeat(TOTAL_WIDTH) + "╣");

        Object[] data = engine.getData();
        for (int i = 0; i < data.length; i++) {
            Product p = (Product) data[i];
            String color = (i == a1 || i == a2)
                    ? (merged ? AnalyticsEngine.GREEN : AnalyticsEngine.YELLOW)
                    : AnalyticsEngine.RESET;

            String id     = pad(String.format("[%02d]", i + 1), 4);
            String name   = pad(p.getName(), 20);
            String price  = pad(String.format("P%,.2f", p.getPrice()), 12);
            String rate   = pad(String.format("%.1f★", p.getRating()), 6);
            String sales  = pad(String.valueOf(p.getSales()), 8);
            String bar    = buildBar(engine.getVal(p), color);

            StringBuilder row = new StringBuilder("║ ");
            row.append(color).append(id).append(AnalyticsEngine.RESET)
               .append(" | ").append(name)
               .append(" | ").append(price)
               .append(" | ").append(rate)
               .append(" | ").append(sales)
               .append(" | ").append(bar);

            // visualLength: number of visible (non-ANSI) characters in this row before padding
            // Adjust this value if the right ║ border drifts left or right
            int visualLength = 96;
            row.append(" ".repeat(Math.max(0, TOTAL_WIDTH - visualLength))).append("║");
            System.out.println(row);
        }

        System.out.println("╚" + "═".repeat(TOTAL_WIDTH) + "╝");
        if (!engine.isBusy()) {
            System.out.println(" [P] Price  [R] Rating  [S] Sales  [T] Stock  |  [A] Ascending  [D] Descending  |  [Q] Back");
            System.out.print(" Action > ");
        }
    }
}
