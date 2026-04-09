# PROMPT

---

Create a Java 21+ CLI application that visualizes the Merge Sort algorithm using an E-commerce Product Analytics Dashboard.

Requirements:

1. DATA MODEL:

- Create a Product class with the following fields:
  name (String), price (double), rating (double), sales (int), stock (int)
- Use encapsulation (private fields + getters)

2. DETERMINISTIC DATASET:

- Do NOT use random values.
- Initialize a fixed array of Product objects with at least 6–10 predefined products.
- The dataset must remain the same every time the program runs to ensure deterministic output.

3. SORTING:

- Implement Merge Sort for Product[].
- Allow sorting based on a selected attribute:
  price, rating, sales, or stock.
- Use a helper function to extract numeric values from Product based on the chosen criteria.

4. VISUALIZATION:

- The title of the program must be "E-Commerce Product Analytics"
- Create a terminal-based (CLI) visualization similar to a bar graph using ASCII characters.
- Each Product should be represented as a horizontal bar.
- The bar length must correspond to the selected attribute value.
- Display product name and value beside each bar.

5. ANIMATION:

- During Merge Sort:
    - Show comparisons between elements
    - Show merging steps
    - Highlight active elements using ANSI colors

- Include a short delay (Thread.sleep) between steps to simulate animation.

6. DASHBOARD STRUCTURE:

- Separate responsibilities into classes:
    - Product (data model)
    - Sorter (Merge Sort logic)
    - Dashboard (visualization/output)
    - Main (program flow)

7. OUTPUT CONSISTENCY:

- Ensure the visualization output is consistent across runs.
- Do not use randomness or time-based variations.

8. USER EXPERIENCE:

- Display a title: "E-Commerce Product Analytics – Merge Sort Visualization"
- Show current sorting criteria (e.g., "Sorting by SALES")
- Show status messages like:
  "Comparing Laptop and Phone"
  "Merging range 0–3"

Goal:
The program must demonstrate how Merge Sort works on real-world objects (Product) and visually show the step-by-step sorting process in a deterministic and reproducible way.
