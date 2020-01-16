package com.xipu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Crawler {
    public static void main(String[] args) throws IOException {

        Queue<String> linkPool = new LinkedList<>();
        Set<String> processedLinks = new HashSet<>();
        linkPool.offer("https://sina.cn/");

        String linkToBeProcessed;

        while ( (linkToBeProcessed = linkPool.remove()) != null ) {
            if (processedLinks.contains(linkToBeProcessed)) {
                continue;
            }

            linkPool.addAll(get(linkToBeProcessed));
            processedLinks.add(linkToBeProcessed);
        }

    }

    private static List<String> get(String link) throws IOException {
        System.out.println(link);
        Document doc = Jsoup.connect(link).get();
        Elements linkTags = doc.select("a");

        List<String> result = new ArrayList<>();
        linkTags.forEach(element -> result.add(element.attr("href")));
        return result;
    }
}
