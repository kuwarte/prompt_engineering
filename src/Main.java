import java.util.Scanner;

/**
 * Entry point and controller for the DSS E-Commerce Analytics platform.
 *
 * <p><b>OOP Concept:</b> Acts as the <em>Controller</em> in an MVC-style architecture.
 * Owns all data arrays, engine instances, and the {@link #activeDashboard} reference.
 * Delegates rendering to {@link Dashboard} subclasses and sorting to {@link AnalyticsEngine},
 * keeping concerns cleanly separated.</p>
 *
 * <p><b>DSA Concept:</b> {@link #getSupplierStat} performs a linear scan O(n) over the
 * {@link #products} array to aggregate per-supplier metrics (min price, total stock).
 * This is called inside {@link AnalyticsEngine#getVal} during each merge comparison,
 * so it executes O(n log n × p) times across a full sort — acceptable for small datasets.</p>
 *
 * <p><b>Design Note:</b> {@link #activeDashboard} is {@code static} so that
 * {@link AnalyticsEngine#merge} can call {@code render()} without holding a reference
 * to the dashboard directly, keeping the engine decoupled from the view layer.</p>
 */
public class Main {

    /**
     * The global array of {@link Product} objects loaded from {@code data.json}.
     * Shared across all engines and used by {@link #getSupplierStat} for aggregate queries.
     */
    public static Product[] products;

    /**
     * The global array of {@link Consumer} objects loaded from {@code data.json}.
     */
    public static Consumer[] consumers;

    /**
     * The global array of {@link Supplier} objects loaded from {@code data.json}.
     */
    public static Supplier[] suppliers;

    /**
     * Static reference to the currently active {@link Dashboard}.
     * Updated before each {@link AnalyticsEngine#sort} call so that merge step
     * re-renders go to the correct dashboard. {@code static} allows
     * {@link AnalyticsEngine} to call it without a direct dependency on {@code Main}.
     */
    public static Dashboard activeDashboard;

    /**
     * Application entry point. Loads data, initializes engines and dashboards,
     * and drives the main menu loop.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        DataLoader.load("data.json");

        Scanner sc = new Scanner(System.in);
        AnalyticsEngine pEng = new AnalyticsEngine(products);
        AnalyticsEngine cEng = new AnalyticsEngine(consumers);
        AnalyticsEngine sEng = new AnalyticsEngine(suppliers);

        while (true) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║           DSS  E-COMMERCE  ANALYTICS           ║");
            System.out.println("╠════════════════════════════════════════════════╣");
            System.out.println("║   [1]  Product Analytics Dashboard             ║");
            System.out.println("║   [2]  Consumer Loyalty Analytics              ║");
            System.out.println("║   [3]  Supplier Performance Dashboard          ║");
            System.out.println("║   [Q]  Exit System                             ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.print(" Select Option: ");

            String choice = sc.nextLine().toUpperCase().trim();
            switch (choice) {
                case "1" -> handleLoop(sc, new ProductDashboard(pEng),  pEng, "SALES");
                case "2" -> handleLoop(sc, new ConsumerDashboard(cEng), cEng, "POINTS");
                case "3" -> handleLoop(sc, new SupplierDashboard(sEng), sEng, "STOCK");
                case "Q" -> { System.out.println(" Goodbye."); return; }
            }
        }
    }

    /**
     * Drives the interactive input loop for a single dashboard session.
     *
     * <p>Sets {@link #activeDashboard} so that merge-step renders target the correct
     * panel. Renders the initial frame, reads a key, maps it to a criteria or direction
     * change, then triggers a sort. Loop exits when the user presses {@code Q}.</p>
     *
     * @param sc      the shared {@link Scanner} for reading user input
     * @param db      the {@link Dashboard} to display and animate
     * @param eng     the {@link AnalyticsEngine} managing the dashboard's data
     * @param defCrit the default sort criteria string to use on first entry
     */
    private static void handleLoop(Scanner sc, Dashboard db, AnalyticsEngine eng, String defCrit) {
        activeDashboard = db;
        String crit = defCrit;
        boolean asc = false;

        while (true) {
            db.render(-1, -1, false);
            String in = sc.nextLine().toUpperCase().trim();

            switch (in) {
                case "Q"      -> { return; }
                case "A"      -> asc = true;
                case "D"      -> asc = false;
                case "P"      -> crit = "PRICE";
                case "R"      -> crit = "RATING";
                case "S"      -> crit = "SALES";
                case "T"      -> crit = "STOCK";
                default       -> { continue; }
            }
            eng.sort(crit, asc);
        }
    }

    /**
     * Computes an aggregate metric for a given supplier across all linked products.
     *
     * <p><b>Algorithm:</b> Performs a single linear scan O(p) over the {@link #products}
     * array, matching products whose supplier company name equals {@code s.getCompany()}.
     * Depending on the criteria, it tracks the minimum price or accumulates total stock.</p>
     *
     * <p>A sentinel value of {@code 99999} is used for min-price initialization; if no
     * products are found for this supplier, returns {@code 0} gracefully.</p>
     *
     * @param s     the {@link Supplier} to compute metrics for
     * @param crit  the metric to compute: {@code "PRICE"} for minimum product price,
     *              any other value for total stock
     * @return the computed aggregate metric as a {@code double}
     */
    public static double getSupplierStat(Supplier s, String crit) {
        double result = crit.equals("PRICE") ? 99999 : 0;
        for (Product p : products) {
            if (p.getSupplier().getCompany().equals(s.getCompany())) {
                if (crit.equals("PRICE")) result = Math.min(result, p.getPrice());
                else result += p.getStock();
            }
        }
        return (result == 99999) ? 0 : result;
    }
}
