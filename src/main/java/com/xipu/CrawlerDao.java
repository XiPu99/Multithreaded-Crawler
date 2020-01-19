package com.xipu;

import java.sql.SQLException;
import java.util.List;

public interface CrawlerDao {

    String getNextLinkThenDelete() throws SQLException;

    boolean isLinkAlreadyProcessed(String link) throws SQLException;

    void insertLinksToBeProcessed(List<String> links) throws SQLException;

    void insertProcessedLinks(String link) throws SQLException;

    void insertNewsIntoDatabase(String link, String title, String content) throws SQLException;
}
