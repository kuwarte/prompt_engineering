/**
 * Abstract base class for all TUI dashboard panels in the DSS analytics platform.
 *
 * <p><b>OOP Concept:</b> Demonstrates <em>abstraction</em> and <em>polymorphism</em>.
 * Shared rendering utilities ({@link #printFrameHeader}, {@link #buildBar}, {@link #pad})
 * are implemented here and reused by all subclasses. The abstract {@link #render} method
 * defines a contract that each concrete dashboard must fulfill with its own column layout,
 * enabling runtime polymorphism through the {@code Main.activeDashboard} reference.</p>
 *
 * <p><b>DSA Concept:</b> The {@link #buildBar} method performs a linear scan over the
 * engine's data array to find the maximum value — O(n) — used to scale bar widths
 * proportionally on every render call.</p>
 *
 * <p><b>Design Note:</b> {@code TOTAL_WIDTH} is a shared constant that all subclasses
 * use to align their right-side {@code ║} border. Each subclass calibrates its own
 * {@code visualLength} offset to compensate for ANSI escape code characters that
 * are invisible but count toward string length.</p>
 */
public abstract class Dashboard {

    /**
     * The {@link AnalyticsEngine} instance bound to this dashboard.
     * Provides access to sorted data, current criteria, and busy state.
     */
    protected AnalyticsEngine engine;

    /**
     * The display title shown in the header bar (e.g. "PRODUCT ANALYTICS").
     */
    protected String title;

    /**
     * The fixed total width of the dashboard box in characters (excluding the border pipes).
     * All rows must pad to this width to maintain alignment of the right-side {@code ║}.
     */
    protected final int TOTAL_WIDTH = 110;

    /**
     * Constructs a {@code Dashboard} bound to the given engine with the given title.
     *
     * @param engine the {@link AnalyticsEngine} powering this dashboard
     * @param title  the header title string for this dashboard panel
     */
    public Dashboard(AnalyticsEngine engine, String title) {
        this.engine = engine;
        this.title = title;
    }

    /**
     * Renders one frame of the dashboard to the terminal, highlighting elements
     * at positions {@code a1} and {@code a2} based on the sort animation state.
     *
     * <p><b>OOP Note (Polymorphism):</b> Each subclass overrides this method with its
     * own column layout, allowing {@code Main.activeDashboard.render(...)} to dispatch
     * to the correct implementation at runtime without knowing the concrete type.</p>
     *
     * @param a1     index of the first highlighted element (yellow = comparing, green = placed)
     * @param a2     index of the second highlighted element, or {@code -1} if only one is highlighted
     * @param merged {@code true} if the element at {@code a1} has just been placed (green);
     *               {@code false} if both elements are being compared (yellow)
     */
    public abstract void render(int a1, int a2, boolean merged);

    /**
     * Clears the terminal and prints the shared box-drawing header for this dashboard.
     *
     * <p>Uses ANSI escape {@code \033[H\033[2J} to move the cursor to home and clear
     * the screen before each frame, creating the animation effect.</p>
     *
     * <p>The header format is: {@code TITLE | CRITERIA-DIRECTION}
     * (e.g. {@code PRODUCT ANALYTICS | PRICE-DESC}).</p>
     */
    protected void printFrameHeader() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("╔" + "═".repeat(TOTAL_WIDTH) + "╗");
        String head = title + " | " + engine.getCriteria() + (engine.isAsc() ? "-ASC" : "-DESC");
        System.out.printf("║ " + AnalyticsEngine.BOLD + "%-108s" + AnalyticsEngine.RESET + " ║%n", head);
        System.out.println("╠" + "═".repeat(TOTAL_WIDTH) + "╣");
    }

    /**
     * Builds a proportional ASCII bar string scaled relative to the current maximum
     * value in the engine's data array.
     *
     * <p><b>Algorithm:</b> Scans the data array in O(n) to find {@code max}, then
     * calculates {@code filled = (value / max) * barWidth}, clamped to [0, 30].
     * Fills with {@code █} characters and pads the remainder with {@code ░}.</p>
     *
     * @param value the numeric value to represent as a bar
     * @param color the ANSI color code to apply to the filled portion
     * @return a 30-character bar string with ANSI color codes applied
     */
    protected String buildBar(double value, String color) {
        double max = 1;
        for (Object o : engine.getData()) {
            max = Math.max(max, engine.getVal(o));
        }
        int barWidth = 30;
        int filled = (int) ((value / (max == 0 ? 1 : max)) * barWidth);
        filled = Math.max(0, Math.min(barWidth, filled));
        return color + "█".repeat(filled) + AnalyticsEngine.RESET + "░".repeat(barWidth - filled);
    }

    /**
     * Pads or truncates a string to an exact character width for column alignment.
     *
     * <p>If {@code text} is longer than {@code width}, it is truncated and suffixed
     * with {@code "..."} to signal truncation. If shorter, it is right-padded with spaces.</p>
     *
     * @param text  the string to pad or truncate
     * @param width the exact target character width
     * @return a string of exactly {@code width} characters
     */
    protected String pad(String text, int width) {
        if (text.length() > width) return text.substring(0, width - 3) + "...";
        return text + " ".repeat(width - text.length());
    }
}
