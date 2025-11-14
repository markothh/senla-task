package Model.Service;

import Model.Entity.Book;
import Model.Enum.BookStatus;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class Warehouse {
    private List<Book> books = new ArrayList<>();

    private void initializeBooks() {
        books.add(new Book("Преступление и наказание", "История бывшего студента, совершившего убийство из-за своей теории", "Федор Достоевский", "Роман", 450.0, BookStatus.IN_STOCK, 1866, LocalDate.of(2025, 10, 25)));
        books.add(new Book("Мастер и Маргарита", "Мистическая история о визите дьявола в Москву 1930-х годов", "Михаил Булгаков", "Фантастика", 520.0, BookStatus.IN_STOCK, 1967, LocalDate.of(2025, 10, 20)));
        books.add(new Book("1984", "Антиутопия о тоталитарном обществе под постоянным контролем", "Джордж Оруэлл", "Антиутопия", 380.0, BookStatus.IN_STOCK, 1949, LocalDate.of(2025, 10, 15)));
        books.add(new Book("Гарри Поттер и философский камень", "Первая книга о юном волшебнике и его обучении в Хогвартсе", "Джоан Роулинг", "Фэнтези", 550.0, BookStatus.IN_STOCK, 1997, LocalDate.of(2025, 10, 22)));
        books.add(new Book("Тень горы", "Продолжение истории Линдсея Форда о его жизни в Индии", "Грегори Дэвид Робертс", "Приключения", 510.0, BookStatus.IN_STOCK, 2015, LocalDate.of(2025, 10, 18)));
        books.add(new Book("Война и мир", "Эпопея о судьбах русских аристократов во время наполеоновских войн", "Лев Толстой", "Роман-эпопея", 680.0, BookStatus.IN_STOCK, 1869, LocalDate.of(2024, 3, 10)));
        books.add(new Book("Три товарища", "История дружбы трех ветеранов Первой мировой войны в послевоенной Германии", "Эрих Мария Ремарк", "Роман", 420.0, BookStatus.IN_STOCK, 1936, LocalDate.of(2024, 4, 5)));
        books.add(new Book("Маленький принц", "Философская притча о встрече летчика с мальчиком с другой планеты", "Антуан де Сент-Экзюпери", "Притча", 320.0, BookStatus.IN_STOCK, 1943, LocalDate.of(2024, 2, 28)));
        books.add(new Book("Анна Каренина", "Трагическая история любви замужней женщины к офицеру Вронскому", "Лев Толстой", "Роман", 490.0, BookStatus.IN_STOCK, 1877, LocalDate.of(2024, 3, 18)));
        books.add(new Book("Сто лет одиночества", "Сага о семье Буэндиа в вымышленном городе Макондо", "Габриэль Гарсиа Маркес", "Магический реализм", 460.0, BookStatus.IN_STOCK, 1967, LocalDate.of(2024, 4, 22)));
        books.add(new Book("Портрет Дориана Грея", "История молодого человека, чей портрет стареет вместо него", "Оскар Уайльд", "Роман", 350.0, BookStatus.IN_STOCK, 1890, LocalDate.of(2023, 8, 15)));
        books.add(new Book("Атлант расправил плечи", "Философский роман о борьбе талантливых людей с обществом", "Айн Рэнд", "Философский роман", 720.0, BookStatus.IN_STOCK, 1957, LocalDate.of(2023, 6, 12)));
        books.add(new Book("451 по Фаренгейту", "Антиутопия о обществе, где книги находятся под запретом", "Рэй Брэдбери", "Антиутопия", 370.0, BookStatus.IN_STOCK, 1953, LocalDate.of(2023, 9, 5)));
        books.add(new Book("Повелитель мух", "Аллегория о группе детей, оказавшихся на необитаемом острове", "Уильям Голдинг", "Аллегория", 340.0, BookStatus.IN_STOCK, 1954, LocalDate.of(2023, 7, 20)));
        books.add(new Book("Дюна", "Эпическая сага о борьбе за контроль над планетой Арракис", "Фрэнк Герберт", "Научная фантастика", 530.0, BookStatus.IN_STOCK, 1965, LocalDate.of(2023, 11, 8)));
        books.add(new Book("Улисс", "Модернистский роман, описывающий один день из жизни Леопольда Блума", "Джеймс Джойс", "Модернизм", 580.0, BookStatus.OUT_OF_STOCK, 1922, null));
        books.add(new Book("Шантарам", "Автобиографический роман о беглом заключенном в Бомбее", "Грегори Дэвид Робертс", "Приключения", 620.0, BookStatus.OUT_OF_STOCK, 2003, null));
        books.add(new Book("Над пропастью во ржи", "История подростка Холдена Колфилда и его бунта против общества", "Джером Сэлинджер", "Роман", 330.0, BookStatus.OUT_OF_STOCK, 1951, null));
        books.add(new Book("О дивный новый мир", "Антиутопия о технологически развитом обществе будущего", "Олдос Хаксли", "Антиутопия", 410.0, BookStatus.OUT_OF_STOCK, 1932, null));
        books.add(new Book("Моби Дик", "Эпическая история охоты на белого кита капитаном Ахавом", "Герман Мелвилл", "Приключения", 480.0, BookStatus.OUT_OF_STOCK, 1851, null));
        books.add(new Book("Скотный двор", "Сатирическая притча о животных, свергнувших своих хозяев", "Джордж Оруэлл", "Сатира", 310.0, BookStatus.OUT_OF_STOCK, 1945, null));
        books.add(new Book("Фауст", "Трагедия о ученом, заключившем сделку с дьяволом", "Иоганн Вольфганг Гёте", "Трагедия", 440.0, BookStatus.IN_STOCK, 1808, LocalDate.of(2024, 12, 3)));
        books.add(new Book("Братья Карамазовы", "Философский роман о вере, сомнениях и отцеубийстве", "Федор Достоевский", "Роман", 590.0, BookStatus.IN_STOCK, 1880, LocalDate.of(2024, 8, 19)));
        books.add(new Book("Гордость и предубеждение", "История любви Элизабет Беннет и мистера Дарси", "Джейн Остин", "Роман", 360.0, BookStatus.IN_STOCK, 1813, LocalDate.of(2025, 1, 12)));
        books.add(new Book("Властелин колец: Братство кольца", "Первая часть эпопеи о путешествии хоббита Фродо", "Джон Толкин", "Фэнтези", 610.0, BookStatus.IN_STOCK, 1954, LocalDate.of(2025, 3, 22)));
        books.add(new Book("Процесс", "Абсурдистская история о человеке, арестованном без предъявления обвинения", "Франц Кафка", "Абсурдизм", 400.0, BookStatus.IN_STOCK, 1925, LocalDate.of(2024, 10, 8)));
        books.add(new Book("Лолита", "Скандальный роман о одержимости взрослого мужчины девочкой-подростком", "Владимир Набоков", "Роман", 430.0, BookStatus.IN_STOCK, 1955, LocalDate.of(2025, 5, 30)));
        books.add(new Book("Старик и море", "Повесть о старом рыбаке и его борьбе с гигантской рыбой", "Эрнест Хемингуэй", "Повесть", 290.0, BookStatus.IN_STOCK, 1952, LocalDate.of(2025, 7, 14)));
        books.add(new Book("Песнь льда и пламени", "Эпическая фэнтези-сага о борьбе за Железный трон", "Джордж Мартин", "Фэнтези", 670.0, BookStatus.IN_STOCK, 1996, LocalDate.of(2025, 9, 5)));
        books.add(new Book("Код да Винчи", "Детектив о тайных обществах и религиозных заговорах", "Дэн Браун", "Детектив", 390.0, BookStatus.IN_STOCK, 2003, LocalDate.of(2025, 8, 28)));
    }

    private Book getBookById(int bookId) {
        Optional<Book> book = books.stream()
                .filter(b -> b.getId() == bookId)
                .findFirst();

        if (book.isEmpty()) {
            String errMessage = "Книга с указанным id не найдена";
            Logger.getGlobal().severe(errMessage);
            throw new IllegalArgumentException(errMessage);
        }

        return book.get();
    }

    public Warehouse() {
        initializeBooks();
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Book> getSortedBooks(String sortBy, boolean isReversed) {
        HashMap<String, Comparator<Book>> comparators = new HashMap<>() {{
            put("bookName", Comparator.comparing(Book::getName));
            put("price", Comparator.comparing(Book::getPrice));
            put("publishDate", Comparator.comparing(Book::getPublishYear));
            put("stockAvailability", Comparator.comparing(Book::getStockDate, Comparator.nullsLast(LocalDate::compareTo)));
        }};

        Comparator<Book> comparator = comparators.get(sortBy);
        if (comparator == null) {
            String errMessage = "Невозможна сортировка по указанному полю. " +
                    "Возможные значения параметра сортировки: bookName, price, publishDate, stockAvailability";
            Logger.getGlobal().severe(errMessage);
            throw new IllegalArgumentException(errMessage);
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return books.stream()
                .sorted(comparator)
                .toList();
    }

    public Book getBookByName(String bookName) {
        Optional<Book> book = books.stream()
                .filter(b -> b.getName().equals(bookName))
                .findFirst();

        if (book.isEmpty()) {
            String errMessage = "Книга с указанным названием не найдена";
            Logger.getGlobal().severe(errMessage);
            throw new IllegalArgumentException(errMessage);
        }

        return book.get();
    }

    public String getDescriptionByBookName(String bookName) {
        return getBookByName(bookName).getDescription();
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
