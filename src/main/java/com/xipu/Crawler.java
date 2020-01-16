package com.xipu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Crawler {
    public static void main(String[] args) throws IOException {
        Queue<String> linkPool = new LinkedList<>();
        Set<String> processedLinks = new HashSet<>();
        linkPool.offer("https://sina.cn/");

        String linkToBeProcessed;

        while ((linkToBeProcessed = linkPool.remove()) != null) {
            if (processedLinks.contains(linkToBeProcessed)) {
                continue;
            }
            System.out.println(linkToBeProcessed);
            if (isNewsLink(linkToBeProcessed)) {
                Document doc = Jsoup.connect(linkToBeProcessed).get();
                processNewsLink(doc);
                linkPool.addAll(getAllLinksOnThisPage(doc));
                processedLinks.add(linkToBeProcessed);
            }
        }
    }

    private static void processNewsLink(Document doc) {
        Elements articleTags = doc.select("article");
        if (articleTags.size() > 0) {
            System.out.println(articleTags.get(0).select("h1").text());
            Elements content = articleTags.get(0).select(".art_p");
            for (Element paragraph : content) {
                System.out.println(paragraph.text());
            }
        }
    }

    private static List<String> getAllLinksOnThisPage(Document doc) {
        Elements linkTags = doc.select("a");

        List<String> result = new ArrayList<>();
        linkTags.forEach(element -> result.add(element.attr("href")));
        return result;
    }

    private static boolean isNewsLink(String link) {
        return isValidLink(link) && link.contains("sina.cn") && !link.contains("photo.sina.cn");
    }

    private static boolean isValidLink(String link) {
        link = link.toLowerCase();
        return !link.contains("javascript") && link.length() != 0;
    }
}
