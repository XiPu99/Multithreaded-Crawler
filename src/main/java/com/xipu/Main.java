package com.xipu;

public class Main {
    public static void main(String[] args) {
        CrawlerDao dao = new MybatisDao();
        new Crawler(dao).run();
    }
}
