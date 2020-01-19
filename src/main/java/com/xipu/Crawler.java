package com.xipu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Crawler {

    private CrawlerDao crawlerDao;

    public Crawler(CrawlerDao dao) {
        this.crawlerDao = dao;
    }

    public void run() {
        String linkToBeProcessed;

        try {
            while ((linkToBeProcessed = crawlerDao.getNextLinkThenDelete()) != null) {
                if (crawlerDao.isLinkAlreadyProcessed(linkToBeProcessed)) {
                    continue;
                }
                System.out.println(linkToBeProcessed);
                if (isNewsLink(linkToBeProcessed)) {
                    linkToBeProcessed = fixLinkIfContainsEscapeCharacter(linkToBeProcessed);
                    Document doc = Jsoup.connect(linkToBeProcessed).get();
                    processNewsLink(doc, linkToBeProcessed);
                    crawlerDao.insertLinksToBeProcessed(getAllLinksOnThisPage(doc));
                    crawlerDao.insertProcessedLinks(linkToBeProcessed);
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processNewsLink(Document doc, String link) throws SQLException {
        Elements articleTags = doc.select("article");
        if (articleTags.size() > 0) {
            String title = articleTags.get(0).select("h1").text();
            Elements paragraphs = articleTags.get(0).select(".art_p");
            String content = paragraphs.stream().map(Element::text).collect(Collectors.joining("\n"));
            crawlerDao.insertNewsIntoDatabase(link, title, content);
        }
    }

    private List<String> getAllLinksOnThisPage(Document doc) {
        Elements linkTags = doc.select("a");
        List<String> result = new ArrayList<>();
        linkTags.forEach(element -> result.add(element.attr("href")));
        return result;
    }

    private static boolean isNewsLink(String link) {
        return isValidLink(link) && (isIndexPage(link) || link.contains("news.sina.cn")) && !isLoginPage(link) && !isSharingPage(link);
    }

    private static boolean isIndexPage(String link) {
        return link.equals("https://sina.cn");
    }

    private static boolean isSharingPage(String link) {
        return link.contains("share.sina.cn");
    }

    private static boolean isLoginPage(String link) {
        return link.contains("passport.sina.cn");
    }

    private static boolean isValidLink(String link) {
        link = link.toLowerCase();
        return !link.contains("javascript") && link.length() != 0;
    }

    private static String fixLinkIfContainsEscapeCharacter(String link) {
        if (link.contains("\\")) {
            link = link.replaceAll("\\\\", "");
        }
        return link;
    }
}
