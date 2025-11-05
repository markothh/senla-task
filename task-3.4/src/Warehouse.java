import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Warehouse {
    private List<Book> books = new ArrayList<>();

    private void initializeBooks() {
        books.add(new Book("Преступление и наказание", "Федор Достоевский", "Роман", 450.0, BookStatus.IN_STOCK));
        books.add(new Book("Мастер и Маргарита", "Михаил Булгаков", "Фантастика", 520.0, BookStatus.IN_STOCK));
        books.add(new Book("Война и мир", "Лев Толстой", "Роман-эпопея", 680.0, BookStatus.IN_STOCK));
        books.add(new Book("1984", "Джордж Оруэлл", "Антиутопия", 380.0, BookStatus.IN_STOCK));
        books.add(new Book("Три товарища", "Эрих Мария Ремарк", "Роман", 420.0, BookStatus.IN_STOCK));
        books.add(new Book("Гарри Поттер и философский камень", "Джоан Роулинг", "Фэнтези", 550.0, BookStatus.IN_STOCK));
        books.add(new Book("Маленький принц", "Антуан де Сент-Экзюпери", "Притча", 320.0, BookStatus.IN_STOCK));
        books.add(new Book("Анна Каренина", "Лев Толстой", "Роман", 490.0, BookStatus.IN_STOCK));
        books.add(new Book("Сто лет одиночества", "Габриэль Гарсиа Маркес", "Магический реализм", 460.0, BookStatus.IN_STOCK));
        books.add(new Book("Улисс", "Джеймс Джойс", "Модернизм", 580.0, BookStatus.OUT_OF_STOCK));
        books.add(new Book("Тень горы", "Грегори Дэвид Робертс", "Приключения", 510.0, BookStatus.IN_STOCK));
        books.add(new Book("Шантарам", "Грегори Дэвид Робертс", "Приключения", 620.0, BookStatus.OUT_OF_STOCK));
        books.add(new Book("Песнь льда и пламени", "Джордж Мартин", "Фэнтези", 670.0, BookStatus.IN_STOCK));
        books.add(new Book("Код да Винчи", "Дэн Браун", "Детектив", 390.0, BookStatus.IN_STOCK));
        books.add(new Book("Портрет Дориана Грея", "Оскар Уайльд", "Роман", 350.0, BookStatus.IN_STOCK));
        books.add(new Book("Над пропастью во ржи", "Джером Сэлинджер", "Роман", 330.0, BookStatus.OUT_OF_STOCK));
        books.add(new Book("Атлант расправил плечи", "Айн Рэнд", "Философский роман", 720.0, BookStatus.IN_STOCK));
        books.add(new Book("451 по Фаренгейту", "Рэй Брэдбери", "Антиутопия", 370.0, BookStatus.IN_STOCK));
        books.add(new Book("Повелитель мух", "Уильям Голдинг", "Аллегория", 340.0, BookStatus.IN_STOCK));
        books.add(new Book("О дивный новый мир", "Олдос Хаксли", "Антиутопия", 410.0, BookStatus.OUT_OF_STOCK));
        books.add(new Book("Дюна", "Фрэнк Герберт", "Научная фантастика", 530.0, BookStatus.IN_STOCK));
        books.add(new Book("Фауст", "Иоганн Вольфганг Гёте", "Трагедия", 440.0, BookStatus.IN_STOCK));
        books.add(new Book("Братья Карамазовы", "Федор Достоевский", "Роман", 590.0, BookStatus.IN_STOCK));
        books.add(new Book("Гордость и предубеждение", "Джейн Остин", "Роман", 360.0, BookStatus.IN_STOCK));
        books.add(new Book("Властелин колец: Братство кольца", "Джон Толкин", "Фэнтези", 610.0, BookStatus.IN_STOCK));
        books.add(new Book("Моби Дик", "Герман Мелвилл", "Приключения", 480.0, BookStatus.OUT_OF_STOCK));
        books.add(new Book("Процесс", "Франц Кафка", "Абсурдизм", 400.0, BookStatus.IN_STOCK));
        books.add(new Book("Лолита", "Владимир Набоков", "Роман", 430.0, BookStatus.IN_STOCK));
        books.add(new Book("Старик и море", "Эрнест Хемингуэй", "Повесть", 290.0, BookStatus.IN_STOCK));
        books.add(new Book("Скотный двор", "Джордж Оруэлл", "Сатира", 310.0, BookStatus.OUT_OF_STOCK));
    }

    private Book getBookById(int bookId) {
        Optional<Book> book = books.stream()
                .filter(b -> b.getId() == bookId)
                .findFirst();

        if (book.isEmpty()) {
            throw new IllegalArgumentException("Книга с указанным id не найдена");
        }

        return book.get();
    }

    public Warehouse() {
        initializeBooks();
    }

    public Book getBookByName(String bookName) {
        Optional<Book> book = books.stream()
                .filter(b -> b.getName() == bookName)
                .findFirst();

        if (book.isEmpty()) {
            throw new IllegalArgumentException("Книга с указанным названием не найдена");
        }

        return book.get();
    }

    public List<Book> formIdListFromNames(List<String> names) {
        List<Book> result = new ArrayList<>();
        for (String name : names) {
            result.add(getBookByName(name));
        }

        return result;
    }

    public void addToStock(int bookId) {
        getBookById(bookId).setAvailable();
    }

    public void addToStock(String bookName) {
        getBookByName(bookName).setAvailable();

        System.out.printf("Книга \'%s\' добавлена на склад", bookName);
    }

    public void removeFromStock(int bookId) {
        getBookById(bookId).setUnavailable();
    }

    public void removeFromStock(String bookName) {
        getBookByName(bookName).setUnavailable();

        System.out.printf("Книга \'%s\' списана со склада", bookName);
    }

    public boolean isBookAvailable(int bookId) {
        return getBookById(bookId).isAvailable();
    }

    public boolean isBookAvailable(String bookName) {
        return getBookByName(bookName).isAvailable();
    }
}
