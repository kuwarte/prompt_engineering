import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for reading and parsing {@code data.json} into the application's data arrays.
 *
 * <p><b>OOP Concept:</b> Demonstrates the <em>Single Responsibility Principle (SRP)</em>.
 * This class has exactly one job: load and parse seed data from disk. It has no knowledge
 * of the TUI, sorting, or business logic — those concerns belong to other classes.</p>
 *
 * <p><b>DSA Concept:</b> Uses sequential string scanning (indexOf/substring) to extract
 * JSON array blocks in O(n) time relative to file size, then applies regex pattern matching
 * to extract individual field values from each JSON object string.</p>
 *
 * <p><b>Parsing Strategy:</b></p>
 * <ol>
 *   <li>Read the entire file into a single {@code String} using {@link BufferedReader}.</li>
 *   <li>Use {@code indexOf} and {@code substring} to extract the raw content of each
 *       top-level JSON array (suppliers, consumers, products).</li>
 *   <li>Split each array block into individual object strings by splitting on {@code "},"}.</li>
 *   <li>For each object string, use {@link Pattern} and {@link Matcher} with named-group
 *       regex patterns to extract each field value by key name.</li>
 *   <li>Construct model objects ({@link Supplier}, {@link Consumer}, {@link Product}) and
 *       populate the static arrays in {@link Main}.</li>
 * </ol>
 *
 * <p><b>Design Note:</b> No external JSON libraries are used — only {@code java.util.regex}
 * and {@code java.io}. If the file is missing or any required field is absent, the program
 * prints a descriptive error and exits via {@code System.exit(1)}.</p>
 */
public class DataLoader {

    /**
     * Regex pattern for extracting a quoted string value by JSON key.
     * Matches patterns like {@code "key": "value"}, capturing {@code value}.
     */
    private static final String STR_PATTERN  = "\"(%s)\"\\s*:\\s*\"([^\"]+)\"";

    /**
     * Regex pattern for extracting a numeric value by JSON key.
     * Matches patterns like {@code "key": 123} or {@code "key": 4.5}, capturing the number.
     */
    private static final String NUM_PATTERN  = "\"(%s)\"\\s*:\\s*([\\d.]+)";

    /**
     * Reads and parses {@code data.json} from the given file path, populating the
     * static arrays ({@link Main#products}, {@link Main#consumers}, {@link Main#suppliers})
     * in {@link Main}.
     *
     * <p><b>Parsing Algorithm:</b></p>
     * <ol>
     *   <li>Read file to a single string via {@link BufferedReader}.</li>
     *   <li>Call {@link #extractBlock(String, String)} to isolate each top-level array.</li>
     *   <li>Call {@link #splitObjects(String)} to tokenize the array into object strings.</li>
     *   <li>Call {@link #getString(String, String)} and {@link #getNumber(String, String)}
     *       to extract field values using regex.</li>
     *   <li>Construct model objects and assign to the corresponding {@link Main} arrays.</li>
     * </ol>
     *
     * @param path the file system path to {@code data.json}
     */
    public static void load(String path) {
        String json;
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            reader.close();
            json = sb.toString();
        } catch (IOException e) {
            System.err.println("[ERROR] Cannot read data.json at path: " + path);
            System.err.println("        " + e.getMessage());
            System.exit(1);
            return;
        }

        // ── Parse suppliers ──────────────────────────────────────────────
        String suppBlock = extractBlock(json, "suppliers");
        List<String> suppObjs = splitObjects(suppBlock);
        if (suppObjs.isEmpty()) {
            System.err.println("[ERROR] 'suppliers' array is missing or empty in data.json");
            System.exit(1);
        }
        Main.suppliers = new Supplier[suppObjs.size()];
        for (int i = 0; i < suppObjs.size(); i++) {
            String obj = suppObjs.get(i);
            String id      = requireString(obj, "id",      "suppliers[" + i + "]");
            String name    = requireString(obj, "name",    "suppliers[" + i + "]");
            String company = requireString(obj, "company", "suppliers[" + i + "]");
            Main.suppliers[i] = new Supplier(id, name, company);
        }

        // ── Parse consumers ──────────────────────────────────────────────
        String consBlock = extractBlock(json, "consumers");
        List<String> consObjs = splitObjects(consBlock);
        if (consObjs.isEmpty()) {
            System.err.println("[ERROR] 'consumers' array is missing or empty in data.json");
            System.exit(1);
        }
        Main.consumers = new Consumer[consObjs.size()];
        for (int i = 0; i < consObjs.size(); i++) {
            String obj = consObjs.get(i);
            String id   = requireString(obj, "id",   "consumers[" + i + "]");
            String name = requireString(obj, "name", "consumers[" + i + "]");
            int points  = (int) requireNumber(obj, "points", "consumers[" + i + "]");
            Main.consumers[i] = new Consumer(id, name, points);
        }

        // ── Parse products ───────────────────────────────────────────────
        String prodBlock = extractBlock(json, "products");
        List<String> prodObjs = splitObjects(prodBlock);
        if (prodObjs.isEmpty()) {
            System.err.println("[ERROR] 'products' array is missing or empty in data.json");
            System.exit(1);
        }
        Main.products = new Product[prodObjs.size()];
        for (int i = 0; i < prodObjs.size(); i++) {
            String obj  = prodObjs.get(i);
            String name = requireString(obj, "name", "products[" + i + "]");
            double price  = requireNumber(obj, "price",  "products[" + i + "]");
            double rating = requireNumber(obj, "rating", "products[" + i + "]");
            int    sales  = (int) requireNumber(obj, "sales", "products[" + i + "]");
            int    stock  = (int) requireNumber(obj, "stock", "products[" + i + "]");
            int    si     = (int) requireNumber(obj, "supplierIndex", "products[" + i + "]");
            if (si < 0 || si >= Main.suppliers.length) {
                System.err.printf("[ERROR] products[%d].supplierIndex=%d is out of bounds (max %d)%n",
                        i, si, Main.suppliers.length - 1);
                System.exit(1);
            }
            Main.products[i] = new Product(name, price, rating, sales, stock, Main.suppliers[si]);
        }
    }

    /**
     * Extracts the raw content (between {@code [} and {@code ]}) of a named JSON array
     * from the full JSON string using index-based substring operations.
     *
     * @param json      the full JSON file content as a string
     * @param arrayName the key name of the JSON array to extract
     * @return the substring between the array's opening and closing brackets,
     *         or an empty string if the array key is not found
     */
    private static String extractBlock(String json, String arrayName) {
        String key = "\"" + arrayName + "\"";
        int keyIdx = json.indexOf(key);
        if (keyIdx == -1) return "";
        int start = json.indexOf('[', keyIdx);
        if (start == -1) return "";
        int depth = 0, end = start;
        for (int i = start; i < json.length(); i++) {
            if (json.charAt(i) == '[') depth++;
            else if (json.charAt(i) == ']') { depth--; if (depth == 0) { end = i; break; } }
        }
        return json.substring(start + 1, end);
    }

    /**
     * Splits a JSON array body string into individual object strings.
     * Splits on {@code },...{} boundaries while respecting nested braces depth.
     *
     * @param block the raw array content (without surrounding {@code [ ]})
     * @return a {@link List} of individual JSON object strings (without outer braces)
     */
    private static List<String> splitObjects(String block) {
        List<String> result = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < block.length(); i++) {
            char ch = block.charAt(i);
            if (ch == '{') { if (depth++ == 0) start = i; }
            else if (ch == '}') { if (--depth == 0 && start != -1) result.add(block.substring(start, i + 1)); }
        }
        return result;
    }

    /**
     * Extracts a quoted string value for the given JSON key from an object string,
     * using a compiled regex pattern with a named capture group.
     *
     * @param obj the JSON object string to search within
     * @param key the JSON field key name
     * @return the extracted string value, or {@code null} if not found
     */
    private static String getString(String obj, String key) {
        Pattern p = Pattern.compile(String.format(STR_PATTERN, Pattern.quote(key)));
        Matcher m = p.matcher(obj);
        return m.find() ? m.group(2) : null;
    }

    /**
     * Extracts a numeric value for the given JSON key from an object string,
     * using a compiled regex pattern.
     *
     * @param obj the JSON object string to search within
     * @param key the JSON field key name
     * @return the extracted numeric value as a {@code double}, or {@code Double.NaN} if not found
     */
    private static double getNumber(String obj, String key) {
        Pattern p = Pattern.compile(String.format(NUM_PATTERN, Pattern.quote(key)));
        Matcher m = p.matcher(obj);
        return m.find() ? Double.parseDouble(m.group(2)) : Double.NaN;
    }

    /**
     * Extracts a required string field; exits the program with an error if not found.
     *
     * @param obj     the JSON object string
     * @param key     the required field key
     * @param context a descriptive context string for the error message (e.g. "suppliers[2]")
     * @return the extracted string value
     */
    private static String requireString(String obj, String key, String context) {
        String val = getString(obj, key);
        if (val == null) {
            System.err.printf("[ERROR] Missing required field '%s' in %s%n", key, context);
            System.exit(1);
        }
        return val;
    }

    /**
     * Extracts a required numeric field; exits the program with an error if not found.
     *
     * @param obj     the JSON object string
     * @param key     the required field key
     * @param context a descriptive context string for the error message
     * @return the extracted numeric value as a {@code double}
     */
    private static double requireNumber(String obj, String key, String context) {
        double val = getNumber(obj, key);
        if (Double.isNaN(val)) {
            System.err.printf("[ERROR] Missing required numeric field '%s' in %s%n", key, context);
            System.exit(1);
        }
        return val;
    }
}
