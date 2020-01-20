# Multithreaded Crawler

### Project info

 This project uses Jsoup library to scrape news page information from [this website](https://sina.cn) and
 store it into a H2 database via two ways -- JDBC and MyBatis. The class `MockDataGenerator` 
 can be used to generate mock data to increase the size of the database. This project also uses
 CircleCi for style checking and Flyway for database migration.