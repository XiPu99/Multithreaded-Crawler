package com.xipu;

public class Main {
    public static void main(String[] args) {
        CrawlerDao dao = new JdbcDao();
        new Crawler(dao).run();
    }
}
