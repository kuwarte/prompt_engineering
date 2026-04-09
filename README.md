# Prompt Engineering - DSA Activity

A Java 21+ CLI application that demonstrates the **Merge Sort** algorithm step-by-step on a realistic e-commerce product dataset, rendered as an animated ASCII bar chart with ANSI colors directly in your terminal.

---

## Preview

```
╔═════════════════════════════════════════╗
║      E-Commerce Product Analytics       ║
╚═════════════════════════════════════════╝

  ▶  Sorting by: SALES
  ──────────────────────────────────────────────────────────────────────

  CURRENT STATE:

  Laptop Pro 15"       █████████                                      8,420 sold
  Wireless Headphones  █████████████████████████                     23,105 sold   ← yellow: active range
  Mechanical Keyboard  █████████████████                             15,830 sold
  4K Monitor 27"       ███████                                        6,210 sold
  USB-C Hub 7-in-1     █████████████████████████████████████████████ 41,670 sold
  ...

  🔀 Merging range  [0 – 1]  ∪  [2 – 3]

  ⚖  Comparing  Laptop Pro 15"  vs  Wireless Headphones
       8420.00  vs  23105.00
```

Color legend during animation:

| Color     | Meaning                                   |
| --------- | ----------------------------------------- |
| 🟡 Yellow | Elements in the active merge window       |
| 🟢 Green  | Element just placed into its sorted slot  |
| 🔵 Blue   | Elements outside the current merge window |

---

## Requirements

- **Java 21 or higher**
- A terminal that supports **ANSI escape codes** (macOS Terminal, iTerm2, Windows Terminal, most Linux terminals)

> **Windows CMD** does not render ANSI colors by default. Use Windows Terminal or enable virtual terminal processing.

---

## Project Structure

```
prompt_engineering/
├── .git/
├── bin/
├── src/main/java/
│   ├── Dashboard.java   – ANSI-colored ASCII bar chart renderer
│   ├── Main.java        – Entry point: dataset definition, criteria selection, program flow
│   ├── Product.java     – Data model with encapsulated fields and getValue() dispatcher
│   └── Sorter.java      – Recursive Merge Sort with animation hooks
├── .gitignore
├── PROMPT.md
├── README.md
└── run.sh
```

---

## Getting Started

### 1. Compile

```bash
cd prompt_engineering
javac -d bin src/main/java/*.java
```

### 2. Run (interactive mode)

```bash
bash run.sh
```

or directly:

```bash
java -cp bin Main
```

You'll see a menu:

```
  Sort the product catalogue by one of these criteria:
    1. PRICE    – product selling price (USD)
    2. RATING   – customer rating (0–5 stars)
    3. SALES    – total units sold
    4. STOCK    – current inventory count

  Enter choice (1–4) or type the name:
```

### 3. Run (non-interactive / scripted)

Pass the criteria as a command-line argument to skip the prompt:

```bash
java -cp bin Main SALES
java -cp bin Main PRICE
java -cp bin Main RATING
java -cp bin Main STOCK
```

---

## Dataset

Ten fixed products are hardcoded in `Main.java`. The dataset is identical on every run — no randomness is used anywhere in the program.

| #   | Product             | Price     | Rating | Sales  | Stock |
| --- | ------------------- | --------- | ------ | ------ | ----- |
| 1   | Laptop Pro 15"      | $1,299.99 | 4.7    | 8,420  | 312   |
| 2   | Wireless Headphones | $199.95   | 4.5    | 23,105 | 1,540 |
| 3   | Mechanical Keyboard | $129.00   | 4.8    | 15,830 | 880   |
| 4   | 4K Monitor 27"      | $549.00   | 4.6    | 6,210  | 205   |
| 5   | USB-C Hub 7-in-1    | $49.99    | 4.3    | 41,670 | 3,200 |
| 6   | Webcam 1080p        | $89.95    | 4.1    | 19,340 | 2,015 |
| 7   | Portable SSD 1TB    | $109.00   | 4.9    | 31,580 | 1,120 |
| 8   | Ergonomic Mouse     | $74.50    | 4.4    | 27,900 | 2,450 |
| 9   | Laptop Stand        | $45.00    | 4.2    | 38,200 | 4,600 |
| 10  | Smart LED Desk Lamp | $59.99    | 4.6    | 22,750 | 1,875 |

---

## How Merge Sort Is Visualized

Merge Sort is a divide-and-conquer algorithm with O(n log n) time complexity. The animation exposes each internal step:

1. **Divide** — the array is recursively split into halves until single-element subarrays remain (base case).
2. **Merge step banner** — `🔀 Merging range [L – M] ∪ [M+1 – R]` is printed before each merge, with the active range highlighted in yellow.
3. **Comparison** — `⚖ Comparing A vs B` is shown with both numeric values before each element is placed.
4. **Placement** — the winning element turns green as it is written into its sorted position; the bar chart redraws immediately.
5. **Final table** — once the sort completes, a ranked table is printed with all product attributes.

Each animation frame has a short `Thread.sleep` delay (100–260 ms depending on the step type) so the progression is easy to follow.

---

## Class Responsibilities

### `Product`

Pure data model. Fields are all `private final`; exposed via getters. The `getValue(String criteria)` method uses a `switch` expression to return the correct field as a `double`, allowing the sorter to work generically with any criteria.

### `Sorter`

Contains the recursive `mergeSort(Product[], int, int)` and the `merge(Product[], int, int, int)` helper. Calls back into `Dashboard` at each significant step (merge start, comparison, bar update) rather than printing anything itself, keeping I/O and algorithm logic separate.

### `Dashboard`

Owns all terminal output. Tracks a `stepCount` for labeling. `renderBars()` redraws the entire bar chart on every call, coloring rows based on whether they are in the active range, just placed, or inactive. `showComparison()` and `showMergeStep()` print annotated status messages between redraws.

### `Main`

Defines the product catalogue, reads the sorting criteria (CLI arg or interactive prompt), calculates `maxValue` for bar scaling, wires `Dashboard` and `Sorter` together, and controls the before/after display.

---

## Design Notes

- **Bar scaling** — each bar's pixel width is `round((value / maxValue) * 45)`, so bars are proportional within the chosen criteria regardless of the unit (dollars, star ratings, unit counts).
- **Screen clearing** — `\033[H\033[2J` moves the cursor to home and erases the screen before each frame, producing a smooth in-place animation rather than a scrolling log.
- **Determinism** — no `java.util.Random`, no `System.currentTimeMillis()`. Given the same input and criteria, the program produces byte-for-byte identical output on every run.
- **Java 21 features used** — `switch` expressions (criteria dispatch, criteria selection), `var`-free but modern style, text block–free for terminal compatibility.

---

## License

For Educational Purposes Only
