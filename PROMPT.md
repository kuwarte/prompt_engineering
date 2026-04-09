# PROMPT

---


Create a Java console application for a DSS (Decision Support System) e-commerce analytics dashboard. The system should have 3 dashboards: Product Analytics, Consumer Loyalty, and Supplier Metrics. 

Use ANSI escape codes throughout for colors, bold text, and screen clearing.

TUI Design Requirements: Use a full bordered ASCII box layout using `╔ ═ ╗ ║ ╠ ╚ ╝` characters for all panels. 
Every dashboard must have a header bar showing the dashboard title and current sort criteria with direction (e.g. `PRODUCT ANALYTICS | PRICE-DESC`). 
Column headers must be separated from data rows by a divider line. 
All columns must be strictly padded and aligned using `String.format` with fixed widths so nothing ever shifts or breaks alignment. 
Long text must be truncated with `...` to preserve layout. The rightmost border `║` must always land on the same column - calibrate each row carefully with a visual length offset.

Keybindings: All input must be single-key, no Enter required where possible. Each dashboard must show a keybinding hint bar at the bottom only when the system is idle (not sorting). 

* Product dashboard: `[P]` Price, `[R]` Rating, `[S]` Sales, `[T]` Stock, `[A]` Ascending, `[D]` Descending, `[Q]` Back. 
* Consumer dashboard: `[A]` Ascending, `[D]` Descending, `[Q]` Back. 
* Supplier dashboard: `[P]` Min Price, `[T]` Total Stock, `[A]` Ascending, `[D]` Descending, `[Q]` Back. Main menu uses `[1]`, `[2]`, `[3]`, `[Q]`.

Sorting Animation: Use merge sort. During sorting, re-render the full dashboard on every comparison and placement step with a short `Thread.sleep` delay so the animation is visible. 
Highlight the two elements being compared in yellow and the placed element in green using ANSI codes. Hide the keybinding bar while sorting is in progress. The `isBusy` flag on the engine must gate this.

Bar Chart Column: Every dashboard must include a proportional bar chart column rendered with `█` (filled) and `░` (empty) characters. 
Bar width is fixed at 30 characters. 
Each bar scales relative to the current maximum value in the dataset. 
The bar color must match the highlight color of that row during animation, and reset to default otherwise.

Class Structure (strictly modular):
* `User` (abstract) with `id` and `name` => subclasses `Supplier` (adds `company`) and `Consumer` (adds `points`)
* `Product` with `name`, `price`, `rating`, `sales`, `stock`, and a linked `Supplier`
* `AnalyticsEngine` - holds the data array, sort criteria, direction flag, busy flag, and animation speed. Contains `mergeSort`, `merge`, `compare`, `getVal`, and `sleep` methods. `getVal` must dispatch by criteria string.
* `Dashboard` (abstract) - holds engine reference, title, and total width constant. Contains `printFrameHeader`, `buildBar`, and `pad` helper methods. Abstract `render(int a1, int a2, boolean merged)` method.
* `ProductDashboard`, `ConsumerDashboard`, `SupplierDashboard` - each implements `render` with their own column layout
* `Main` - seeds data, owns the three engine instances and the static `activeDashboard` reference, runs the main menu loop, and has a `handleLoop` method for per-dashboard input handling. Also has a static `getSupplierStat` method for computing per-supplier aggregates across the product list.

Data: Seed in JSON format 20 products, 20 consumers, and 20 suppliers with randomized values. Suppliers rotate across 10 company names. Products link to suppliers via `suppliers[i % 5]`. 

Documentation: Use JavaDocs 
