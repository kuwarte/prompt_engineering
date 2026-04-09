/**
 * Concrete dashboard implementation for the Consumer Loyalty panel.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>runtime polymorphism</em> through method overriding.
 * {@link #render} overrides the abstract contract in {@link Dashboard} with a consumer-specific
 * layout focused on loyalty point rankings. The {@code Main.activeDashboard} reference dispatches
 * to this implementation at runtime when the Consumer Loyalty dashboard is active.</p>
 *
 * <p><b>DSA Concept:</b> Inherits the O(n) bar-scaling scan from {@link Dashboard#buildBar}.
 * Consumers are sorted by {@code points} via merge sort in {@link AnalyticsEngine}, producing
 * a real-time animated loyalty leaderboard.</p>
 *
 * <p><b>Design Note:</b> The {@code visualLength} calibrator (107) is tuned for this dashboard's
 * wider name column (43 chars). Consumer loyalty has only one sort key (points), so the keybinding
 * bar only shows direction controls.</p>
 */
public class ConsumerDashboard extends Dashboard {

    /**
     * Constructs a {@code ConsumerDashboard} bound to the given engine.
     *
     * @param engine the {@link AnalyticsEngine} managing consumer data
     */
    public ConsumerDashboard(AnalyticsEngine engine) {
        super(engine, "CONSUMER LOYALTY");
    }

    /**
     * Renders the Consumer Loyalty dashboard frame to the terminal.
     *
     * <p><b>Runtime Polymorphism:</b> This override is dispatched to at runtime when
     * the active dashboard is a {@code ConsumerDashboard}, demonstrating dynamic method
     * dispatch through the abstract {@link Dashboard} reference.</p>
     *
     * <p>Columns rendered: ID | CUSTOMER NAME | LOYALTY POINTS | VISUAL STATUS (bar)</p>
     *
     * <p>Each row color during animation: yellow for comparison, green for placement.
     * The hint bar is suppressed while {@code isBusy} is {@code true}.</p>
     *
     * @param a1     index of the first animated element
     * @param a2     index of the second animated element, or {@code -1}
     * @param merged {@code true} = green (placed), {@code false} = yellow (comparing)
     */
    @Override
    public void render(int a1, int a2, boolean merged) {
        printFrameHeader();
        System.out.printf("║ %-4s | %-43s | %-20s | %-32s ║%n",
                "ID", "CUSTOMER NAME", "LOYALTY POINTS", "VISUAL STATUS");
        System.out.println("╠" + "═".repeat(TOTAL_WIDTH) + "╣");

        Object[] data = engine.getData();
        for (int i = 0; i < data.length; i++) {
            Consumer c = (Consumer) data[i];
            String color = (i == a1 || i == a2)
                    ? (merged ? AnalyticsEngine.GREEN : AnalyticsEngine.YELLOW)
                    : AnalyticsEngine.RESET;

            String id     = pad(String.format("[%02d]", i + 1), 4);
            String name   = pad(c.getName(), 43);
            String points = pad(String.format("%,d pts", c.getPoints()), 20);
            String bar    = buildBar(c.getPoints(), color);

            StringBuilder row = new StringBuilder("║ ");
            row.append(color).append(id).append(AnalyticsEngine.RESET)
               .append(" | ").append(name)
               .append(" | ").append(points)
               .append(" | ").append(bar);

            // visualLength: tuned for the wider name column in this dashboard
            int visualLength = 107;
            row.append(" ".repeat(Math.max(0, TOTAL_WIDTH - visualLength))).append("║");
            System.out.println(row);
        }

        System.out.println("╚" + "═".repeat(TOTAL_WIDTH) + "╝");
        if (!engine.isBusy()) {
            System.out.println(" [A] Ascending  [D] Descending  |  [Q] Back");
            System.out.print(" Action > ");
        }
    }
}
