public class Book {
    private static int nextId = 1;

    private int id;
    private String name;
    private String author;
    private String genre;
    private double price;
    private BookStatus status;

    public Book(String name, String author, String genre, double price, BookStatus status) {
        this.id = Book.nextId++;

        this.name = name;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setAvailable() {
        this.status = BookStatus.IN_STOCK;
    }

    public void setUnavailable() {
        this.status = BookStatus.OUT_OF_STOCK;
    }

    boolean isAvailable() {
        return status == BookStatus.IN_STOCK;
    }
}
