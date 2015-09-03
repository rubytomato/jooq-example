package com.example.jooq;

import static com.example.jooq.db.Tables.*;
import static org.jooq.impl.DSL.*;

import java.sql.Connection;
import java.sql.DriverManager;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionSample {
  private static final Logger logger = LoggerFactory.getLogger(TransactionSample.class);

  public static void main(String[] args) {

    String userName = "actor_user";
    String password = "actor_pass";
    String url = "jdbc:mysql://localhost:3306/actor_db";

    try (Connection conn = DriverManager.getConnection(url, userName, password)) {

      Settings settings = new Settings();
      settings.setExecuteLogging(true);
      settings.withRenderFormatted(true);

      DSLContext create = using(conn, SQLDialect.MYSQL, settings);

      try {

        create.transaction(transactional->{
          DSLContext ctx = DSL.using(transactional);

          ctx.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME, ACTOR.BIRTHDAY, ACTOR.BIRTHPLACE_ID)
             .values(20, "森三平太", DateParser.parseSQL("1927-11-15"), Short.valueOf("6"))
             .execute();

          ctx.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME, ACTOR.BIRTHDAY, ACTOR.BIRTHPLACE_ID)
             .values(21, "山谷初男", DateParser.parseSQL("1933-12-19"), Short.valueOf("5"))
             .execute();

          ctx.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME, ACTOR.BIRTHDAY, ACTOR.BIRTHPLACE_ID)
             .values(22, "加藤健一", DateParser.parseSQL("1945-10-31"), Short.valueOf("22"))
             .execute();
        });

      } catch (DataAccessException e) {
        logger.error("{}", e.getLocalizedMessage());
      }

      try {

        create.transaction(transactional->{
          DSLContext ctx = DSL.using(transactional);

          ctx.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME, ACTOR.BIRTHDAY, ACTOR.BIRTHPLACE_ID)
             .values(23, "松本克平", DateParser.parseSQL("1905-04-25"), Short.valueOf("20"))
             .execute();

          ctx.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME, ACTOR.BIRTHDAY, ACTOR.BIRTHPLACE_ID)
             .values(24, "山崎満", DateParser.parseSQL("1933-05-14"), Short.valueOf("1"))
             .execute();

          // 重複データ
          ctx.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME)
             .values(20, "森三平太")
             .execute();
        });

      // DataAccessExceptionは非検査例外
      } catch (DataAccessException e) {
        logger.error("{}", e.getLocalizedMessage());
      }

      Result<Record> result =
          create.select()
                .from(ACTOR)
                .where(ACTOR.ID.greaterOrEqual(20))
                .fetch();

      result.stream().forEach(r->{
        logger.info("select, id:{} name:{} height:{} blood:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.BLOOD), r.getValue(ACTOR.HEIGHT));
      });

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
