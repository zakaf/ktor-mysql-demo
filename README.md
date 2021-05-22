# Ktor + Mysql Demo

## Getting Started

### Set up database

1. create mysql database `grafana`
2. create mysql table and add sample data with below scripts

```sql
create table grafana.dashboard
(
    id      bigint auto_increment primary key,
    version int          not null,
    title   varchar(189) not null
);

INSERT INTO grafana.dashboard (id, version, title)
VALUES (1, 12, 'Sample Dashboard');
```

3. create user `grafana` with password `grafana` and grant read privilege on `grafana` database

### Set up server

1. Replace `host` value in application.conf with host address of your choice. ex) localhost

### Run server

1. choose the implementation of your choice

- Exposed
- Jasync-sql

2. Run Application.kt
3. Send http request to test functionality

```
curl --request GET 'http://localhost:8080/dashboards/1'
```

## Performance of Exposed vs Jasync-sql

### Environment
Client
  - [nGrinder](http://naver.github.io/ngrinder/)

Application
  - container made with [jib](https://github.com/GoogleContainerTools/jib) running on 8 core, 8 GB ram Virtual Machine

Database
  - MySQL ver 5.7.29 w/ over 10M rows in the table


### Exposed

| # Database Connection | RPS | Max RPS | # Non 200 Requests | Avg Latency (ms) | Total Test Time (mm:ss) |
|-----------------------|-----|---------|--------------------|------------------|-------------------------|
|10                     |2,499.6|3,009.5|0                   |159.15            |13:22                    |
|30                     |3,321.7|4,130.5|6                   |118.85            |10:04                    |
|50                     |3,278.2|3,998.5|9                   |120               |10:12                    |	

### Jasync-sql

| # Database Connection | RPS | Max RPS | # Non 200 Requests | Avg Latency (ms) | Total Test Time (mm:ss) |
|-----------------------|-----|---------|--------------------|------------------|-------------------------|
|10                     |2,800.7|3,628  |0                   |142.12            |11:56                    |
|30                     |3,801.7|4,654.5|11                  |103.32            |08:48                    |
|50                     |3,999.2|4,792  |111                 |97.27             |08:22                    |
