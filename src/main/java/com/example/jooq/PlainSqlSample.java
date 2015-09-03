package com.example.jooq;

import static com.example.jooq.db.Tables.*;
import static org.jooq.impl.DSL.*;

import java.sql.Connection;
import java.sql.DriverManager;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainSqlSample {
  private static final Logger logger = LoggerFactory.getLogger(PlainSqlSample.class);

  public static void main(String[] args) {

    String userName = "actor_user";
    String password = "actor_pass";
    String url = "jdbc:mysql://localhost:3306/actor_db";

    try (Connection conn = DriverManager.getConnection(url, userName, password)) {

      Settings settings = new Settings();
      settings.setExecuteLogging(false);
      settings.withRenderFormatted(true);

      DSLContext create = using(conn, SQLDialect.MYSQL, settings);

      /*
       * select
       */
      Result<Record> result =
          create.select()
                .from(ACTOR)
                .where("ACTOR.BLOOD = ?", "O")
                .fetch();
      result.stream().forEach(r->{
        logger.info("select, id:{} name:{} height:{} blood:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.HEIGHT), r.getValue(ACTOR.BLOOD));
      });

      /*
       * result query
       */
      ResultQuery<Record> resultQuery = 
          create.resultQuery("SELECT * FROM actor WHERE actor.blood = 'B' LIMIT 5");
      Result<Record> resQuery = resultQuery.fetch();
      resQuery.stream().forEach(r->{
        logger.info("result query, id:{} name:{} height:{} blood:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.HEIGHT), r.getValue(ACTOR.BLOOD));
      });

      /*
       * query
       */
      Query query = create.query("UPDATE actor SET update_at = now() WHERE actor.BLOOD = 'B'");
      int numOfQuery = query.execute();
      logger.info("query:{}", numOfQuery);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
