package client;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenLibraryScraper
{

    public static class Book {
        public final String title;
        public final String imageUrl;

        public Book(String title, String imageUrl) {
            this.title = title;
            this.imageUrl = imageUrl;
        }
    }

    /**
     * Fetches up to 5 books from the Philosophy category of Books to Scrape.
     *
     * @return List of Book objects containing title and image URL.
     * @throws IOException If there is a problem connecting or parsing the page.
     */
    public static List<Book> fetchBooks() throws IOException
    {
        List<Book> books = new ArrayList<>();

        String url = "https://books.toscrape.com/catalogue/category/books/philosophy_7/index.html";
        Document doc = Jsoup.connect(url).get();

        // Select all book elements
        Elements bookItems = doc.select("article.product_pod");

        URL baseUrl = new URL(url);

        for (Element item : bookItems) {
            // Extract title
            Element titleElement = item.selectFirst("h3 > a");
            String title = titleElement != null ? titleElement.attr("title") : "No Title";

            // Extract and resolve image URL
            Element img = item.selectFirst("div.image_container img");
            String imageUrl = "";
            if (img != null) {
                String src = img.attr("src");
                // Resolve relative URL to absolute
                URL absoluteUrl = new URL(baseUrl, src);
                imageUrl = absoluteUrl.toString();
            }

            books.add(new Book(title, imageUrl));
            if (books.size() >= 5) break;
        }

        return books;
    }
}