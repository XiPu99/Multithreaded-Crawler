package com.xipu;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisDao implements CrawlerDao {
    SqlSessionFactory sqlSessionFactory;

    public MybatisDao() {
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            this.sqlSessionFactory =
                    new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNextLinkThenDelete() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            String link = session.selectOne("com.xipu.MyMapper.selectNextLink");
            if (link != null) {
                session.delete("com.xipu.MyMapper.deleteLink", link);
            }
            return link;
        }
    }

    @Override
    public boolean isLinkAlreadyProcessed(String link) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            int count = session.selectOne("com.xipu.MyMapper.countLink", link);
            return count > 0;
        }

    }

    @Override
    public void insertLinksToBeProcessed(List<String> links) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            for (String link : links) {
                Map<String, Object> param = new HashMap<>();
                param.put("tableName", "LINKS_TO_BE_PROCESSED");
                param.put("link", link);
                session.insert("com.xipu.MyMapper.insertLink", param);
            }
        }
    }

    @Override
    public void insertProcessedLinks(String link) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            Map<String, Object> param = new HashMap<>();
            param.put("tableName", "LINK_ALREADY_PROCESSED");
            param.put("link", link);
            session.insert("com.xipu.MyMapper.insertLink", param);
        }
    }

    @Override
    public void insertNewsIntoDatabase(String link, String title, String content) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.xipu.MyMapper.insertNews", new News(link, title, content));
        }
    }
}
