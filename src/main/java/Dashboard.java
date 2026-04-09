public class Dashboard {

    // ── ANSI escape codes ────────────────────────────────────────────────────
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String DIM = "\033[2m";

    // Foreground colors
    private static final String FG_WHITE = "\033[97m";
    private static final String FG_CYAN = "\033[96m";
    private static final String FG_YELLOW = "\033[93m";
    private static final String FG_GREEN = "\033[92m";
    private static final String FG_RED = "\033[91m";
    private static final String FG_MAGENTA = "\033[95m";
    private static final String FG_BLUE = "\033[94m";
    private static final String FG_GRAY = "\033[90m";

    // Background colors
    private static final String BG_DARK = "\033[40m";

    // Bar characters
    private static final String BAR_CHAR = "█";
    private static final String BAR_HALF = "▓";

    private final String criteria;
    private final double maxValue;
    private final int BAR_WIDTH = 45;

    private int stepCount = 0;

    public Dashboard(String criteria, double maxValue) {
        this.criteria = criteria.toUpperCase();
        this.maxValue = maxValue;
    }

    // ── Title banner ─────────────────────────────────────────────────────────
    public void printTitle() {
        clearScreen();
        String border = "═".repeat(72);
        System.out.println(BOLD + FG_CYAN + "╔" + border + "╗" + RESET);
        System.out.println(BOLD + FG_CYAN + "║" + RESET
                + centerPad("E-Commerce Product Analytics", 72)
                + BOLD + FG_CYAN + "║" + RESET);
        System.out.println(BOLD + FG_CYAN + "╚" + border + "╝" + RESET);
        System.out.println();
        System.out.println(BOLD + FG_YELLOW + "  ▶  Sorting by: " + FG_GREEN + criteria + RESET);
        System.out.println(FG_GRAY + "  " + "─".repeat(70) + RESET);
        System.out.println();
    }

    // ── Show the full bar chart ───────────────────────────────────────────────
    public void renderBars(Product[] arr, int activeLeft, int activeRight, int justPlaced) {
        printTitle();
        System.out.println(BOLD + FG_WHITE + "  CURRENT STATE:" + RESET);
        System.out.println();

        for (int idx = 0; idx < arr.length; idx++) {
            Product p = arr[idx];
            double val = p.getValue(criteria);
            int bars = (int) Math.round((val / maxValue) * BAR_WIDTH);
            bars = Math.max(1, bars);

            boolean inRange = (idx >= activeLeft && idx <= activeRight);
            boolean placed = (idx == justPlaced);

            String nameColor, barColor, labelColor;

            if (placed) {
                nameColor = BOLD + FG_GREEN;
                barColor = FG_GREEN;
                labelColor = BOLD + FG_GREEN;
            } else if (inRange) {
                nameColor = BOLD + FG_YELLOW;
                barColor = FG_YELLOW;
                labelColor = FG_YELLOW;
            } else {
                nameColor = FG_WHITE;
                barColor = FG_BLUE;
                labelColor = FG_GRAY;
            }

            String bar = BAR_CHAR.repeat(Math.max(0, bars - 1))
                    + (bars > 0 ? BAR_HALF : "");

            String valueStr = formatValue(val);
            System.out.printf("  %s%-20s%s %s%-" + BAR_WIDTH + "s%s %s%s%s%n",
                    nameColor, truncate(p.getName(), 20), RESET,
                    barColor, bar, RESET,
                    labelColor, valueStr, RESET);
        }

        System.out.println();
        System.out.println(FG_GRAY + "  " + "─".repeat(70) + RESET);
        System.out.println(DIM + FG_GRAY + "  Step #" + (++stepCount) + RESET);
        System.out.println();
    }

    // ── Show comparison message ───────────────────────────────────────────────
    public void showComparison(Product[] arr, Product a, Product b, int left, int right) {
        renderBars(arr, left, right, -1);
        System.out.printf("  %s⚖  Comparing  %s%s%s  vs  %s%s%s%n",
                FG_MAGENTA + BOLD,
                FG_YELLOW, truncate(a.getName(), 20), RESET + FG_MAGENTA + BOLD,
                FG_RED, truncate(b.getName(), 20), RESET);
        System.out.printf("       %s%.2f%s  vs  %s%.2f%s%n%n",
                FG_YELLOW, a.getValue(criteria), RESET,
                FG_RED, b.getValue(criteria), RESET);
        sleep(140);
    }

    // ── Show merge-step banner ────────────────────────────────────────────────
    public void showMergeStep(Product[] arr, int left, int mid, int right) {
        renderBars(arr, left, right, -1);
        System.out.printf("  %s🔀 Merging range  [%d – %d]  ∪  [%d – %d]%s%n%n",
                BOLD + FG_CYAN, left, mid, mid + 1, right, RESET);
        sleep(260);
    }

    // ── Final sorted display ──────────────────────────────────────────────────
    public void renderFinal(Product[] arr) {
        renderBars(arr, 0, arr.length - 1, -1);
        String msg = "✔  Sort complete!  Sorted " + arr.length + " products by " + criteria + ".";
        System.out.println(BOLD + FG_GREEN + "  " + msg + RESET);
        System.out.println();

        System.out.println(BOLD + FG_WHITE + "  FINAL SORTED TABLE:" + RESET);
        System.out.println(FG_GRAY + "  " + "─".repeat(70) + RESET);
        for (int i = 0; i < arr.length; i++) {
            String rank = String.format("%2d.", i + 1);
            System.out.println("  " + FG_CYAN + rank + RESET + "  " + arr[i]);
        }
        System.out.println(FG_GRAY + "  " + "─".repeat(70) + RESET);
        System.out.println();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String formatValue(double val) {
        return switch (criteria) {
            case "PRICE" -> String.format("$%,8.2f", val);
            case "RATING" -> String.format("  ★ %.1f ", val);
            case "SALES" -> String.format("%,8d sold", (int) val);
            case "STOCK" -> String.format("%,8d units", (int) val);
            default -> String.format("%10.2f", val);
        };
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private String centerPad(String s, int width) {
        int pad = (width - s.length()) / 2;
        int rPad = width - s.length() - pad;
        return " ".repeat(Math.max(0, pad)) + s + " ".repeat(Math.max(0, rPad));
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
