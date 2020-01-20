package com.xipu;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Random;

public class MockDataGenerator {
    private static final int TARGET_NEWS_SIZE = 100_0000;

    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory;
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            List<News> currentNews = session.selectList("com.xipu.MockMapper.selectNews");

            int count = TARGET_NEWS_SIZE - currentNews.size();

            Random random = new Random();

            try {
                while (count-- > 0) {
                    int randIndex = random.nextInt(currentNews.size());
                    News newsToBeInserted = currentNews.get(randIndex).copy();
                    Instant oldInstant = newsToBeInserted.getCreatedAt();
                    Instant newInstant = oldInstant.minusSeconds(random.nextInt(3600 * 24 * 365));
                    newsToBeInserted.setCreatedAt(newInstant);
                    session.insert("com.xipu.MockMapper.insertNews", newsToBeInserted);
                    System.out.println("Left: " + count);
                    if (count % 2000 == 0) {
                        session.flushStatements();
                    }
                }
                session.commit();
            } catch (Exception e) {
                session.rollback();
                throw new RuntimeException(e);
            }
        }
    }
}
