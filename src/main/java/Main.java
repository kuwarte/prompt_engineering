import java.util.Arrays;
import java.util.Scanner;

public class Main {

    // ── Deterministic product catalogue ──────────────────────────────────────
    private static Product[] buildDataset() {
        return new Product[] {
                // name price rating sales stock
                new Product("Laptop Pro 15\"", 1299.99, 4.7, 8_420, 312),
                new Product("Wireless Headphones", 199.95, 4.5, 23_105, 1_540),
                new Product("Mechanical Keyboard", 129.00, 4.8, 15_830, 880),
                new Product("4K Monitor 27\"", 549.00, 4.6, 6_210, 205),
                new Product("USB-C Hub 7-in-1", 49.99, 4.3, 41_670, 3_200),
                new Product("Webcam 1080p", 89.95, 4.1, 19_340, 2_015),
                new Product("Portable SSD 1TB", 109.00, 4.9, 31_580, 1_120),
                new Product("Ergonomic Mouse", 74.50, 4.4, 27_900, 2_450),
                new Product("Laptop Stand", 45.00, 4.2, 38_200, 4_600),
                new Product("Smart LED Desk Lamp", 59.99, 4.6, 22_750, 1_875),
        };
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\033[H\033[2J"); // clear on start

        printWelcome();
        String criteria = chooseCriteria(args);

        // Build dataset and find max value for bar scaling
        Product[] products = buildDataset();
        double maxValue = Arrays.stream(products)
                .mapToDouble(p -> p.getValue(criteria))
                .max()
                .orElse(1.0);

        Dashboard dashboard = new Dashboard(criteria, maxValue);
        Sorter sorter = new Sorter(dashboard, criteria);

        // Show initial (unsorted) state
        System.out.println("\033[H\033[2J");
        dashboard.renderBars(products, -1, -1, -1);
        System.out.println("  Press ENTER to begin Merge Sort animation…");
        waitForEnter();

        // ── Run animated Merge Sort ───────────────────────────────────────────
        sorter.mergeSort(products, 0, products.length - 1);

        // ── Final result ──────────────────────────────────────────────────────
        dashboard.renderFinal(products);
        System.out.println("  Press ENTER to exit.");
        waitForEnter();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static void printWelcome() {
        String cyan = "\033[96m";
        String yellow = "\033[93m";
        String reset = "\033[0m";
        String bold = "\033[1m";

        System.out.println(bold + cyan);
        System.out.println("  ┌────────────────────────────────────────┐");
        System.out.println("  │      E-Commerce Product Analytics      │");
        System.out.println("  └────────────────────────────────────────┘");
        System.out.println(reset);
        System.out.println(yellow + "  Sort the product catalogue by one of these criteria:" + reset);
        System.out.println("    1. PRICE    – product selling price (USD)");
        System.out.println("    2. RATING   – customer rating (0–5 stars)");
        System.out.println("    3. SALES    – total units sold");
        System.out.println("    4. STOCK    – current inventory count");
        System.out.println();
    }

    private static String chooseCriteria(String[] args) {
        // Accept criteria as first CLI argument for non-interactive use
        if (args.length > 0) {
            String c = args[0].toUpperCase();
            if (c.matches("PRICE|RATING|SALES|STOCK")) {
                System.out.println("  Using criteria from argument: " + c);
                sleep(600);
                return c;
            }
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("  Enter choice (1–4) or type the name: ");
        String input = sc.nextLine().trim();

        return switch (input.toUpperCase()) {
            case "1", "PRICE" -> "PRICE";
            case "2", "RATING" -> "RATING";
            case "3", "SALES" -> "SALES";
            case "4", "STOCK" -> "STOCK";
            default -> {
                System.out.println("  Invalid choice. Defaulting to SALES.");
                sleep(800);
                yield "SALES";
            }
        };
    }

    private static void waitForEnter() {
        try {
            System.in.read();
        } catch (Exception ignored) {
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
