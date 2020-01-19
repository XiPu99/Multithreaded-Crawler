package com.xipu;

import java.io.File;
import java.sql.*;
import java.util.List;

public class JdbcDao implements CrawlerDao {

    private Connection connection;

    JdbcDao() {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        String jdbcUrl = "jdbc:h2:file:" + projectDir.getAbsolutePath() + "/news";
        try {
            connection = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNextLinkToProcessFromDatabase() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT LINK FROM LINKS_TO_BE_PROCESSED LIMIT 1");
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        return null;
    }

    @Override
    public String getNextLinkThenDelete() throws SQLException {
        String nextLink = getNextLinkToProcessFromDatabase();
        if (nextLink != null) {
            updateDatabase("DELETE FROM LINKS_TO_BE_PROCESSED WHERE LINK = '" + nextLink + "'");
        }
        return nextLink;
    }

    @Override
    public boolean isLinkAlreadyProcessed(String link) throws SQLException {
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT LINK FROM LINK_ALREADY_PROCESSED WHERE LINK = ?")) {
            statement.setString(1, link);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    @Override
    public void insertLinksToBeProcessed(List<String> links) throws SQLException {
        for (String link : links) {
            updateDatabase(String.format("INSERT INTO LINKS_TO_BE_PROCESSED (LINK) VALUES('%s')", link));
        }
    }

    @Override
    public void insertProcessedLinks(String link) throws SQLException {
        updateDatabase(String.format("INSERT INTO LINK_ALREADY_PROCESSED (LINK) VALUES('%s')", link));
    }

    @Override
    public void insertNewsIntoDatabase(String link, String title, String content) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO NEWS (LINK, TITLE, CONTENT, CREATED_AT) VALUES(?, ?, ?, NOW())")) {
            preparedStatement.setString(1, link);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, content);
            preparedStatement.executeUpdate();
        }
    }

    private void updateDatabase(String sql) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }
}
