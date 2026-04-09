public class Product {
    private final String name;
    private final double price;
    private final double rating;
    private final int sales;
    private final int stock;

    public Product(String name, double price, double rating, int sales, int stock) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.sales = sales;
        this.stock = stock;
    }

    public String getName()   { return name; }
    public double getPrice()  { return price; }
    public double getRating() { return rating; }
    public int getSales()     { return sales; }
    public int getStock()     { return stock; }

    public double getValue(String criteria) {
        return switch (criteria.toUpperCase()) {
            case "PRICE"  -> price;
            case "RATING" -> rating;
            case "SALES"  -> sales;
            case "STOCK"  -> stock;
            default -> throw new IllegalArgumentException("Unknown criteria: " + criteria);
        };
    }

    @Override
    public String toString() {
        return String.format("%-20s | Price: $%7.2f | Rating: %.1f | Sales: %5d | Stock: %4d",
                name, price, rating, sales, stock);
    }
}
