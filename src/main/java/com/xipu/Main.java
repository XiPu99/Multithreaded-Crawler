package com.xipu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://sina.cn/").get();
        System.out.println(doc.title());
        Elements newsHeadlines = doc.select("a");

        for (Element link : newsHeadlines) {
            System.out.println(link.attr("href"));
        }
    }
}
