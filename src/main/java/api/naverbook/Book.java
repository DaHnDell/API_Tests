package api.naverbook;

public class Book {

  private String id;
  private String title;
  private String author;
  private String publisher;
  private String isbn;
  private String description;
  private int price;
  private int stock;
  private boolean selected;
  private boolean fromSearch;

  public Book(String id, String title, String author, String publisher,
      String isbn, String description, int price, int stock,
      boolean selected, boolean fromSearch) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.publisher = publisher;
    this.isbn = isbn;
    this.description = description;
    this.price = price;
    this.stock = stock;
    this.selected = selected;
    this.fromSearch = fromSearch;
  }

  @Override
  public String toString() {
    return String.format(
        "[%s] - %s | 저자: %s | 출판사: %s | ISBN: %s | 가격: %d원",
        id, title, author, publisher, isbn, price);
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public String getPublisher() {
    return publisher;
  }

  public String getIsbn() {
    return isbn;
  }

  public String getDescription() {
    return description;
  }

  public int getPrice() {
    return price;
  }

  public int getStock() {
    return stock;
  }

  public boolean isSelected() {
    return selected;
  }

  public boolean isFromSearch() {
    return fromSearch;
  }

}
