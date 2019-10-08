# log-analyzer

下载七牛access-log日志，并保存到数据库，便于后续分析

项目采用SpringBoot + Mybatis，使用@Scheduled注解定时抓取前一天的访问日志。
使用正则式分析access-log，并拆分到各个字段中
