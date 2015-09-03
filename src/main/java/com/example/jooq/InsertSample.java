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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.jooq.db.tables.records.ActorRecord;

public class InsertSample {
  private static final Logger logger = LoggerFactory.getLogger(InsertSample.class);

  public static void main(String[] args) {

    String userName = "actor_user";
    String password = "actor_pass";
    String url = "jdbc:mysql://localhost:3306/actor_db";

    try (Connection conn = DriverManager.getConnection(url, userName, password)) {

      Settings settings = new Settings();
      settings.setExecuteLogging(false);
      settings.withRenderFormatted(true);
      settings.withRenderSchema(false);

      DSLContext create = using(conn, SQLDialect.MYSQL, settings);

      /*
       * insert
       */
      int numOfInsert =
          create.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME, ACTOR.HEIGHT, ACTOR.BLOOD, ACTOR.BIRTHDAY, ACTOR.BIRTHPLACE_ID)
                .values(14, "春川ますみ", null, "AB", DateParser.parseSQL("1935-11-10"), Short.valueOf("9"))
                .execute();

      logger.info("insert:{}", numOfInsert);

      /*
       * set
       */
      int numOfSet =
          create.insertInto(ACTOR)
                .set(ACTOR.ID, 15)
                .set(ACTOR.NAME, "村松英子")
                .set(ACTOR.HEIGHT, Short.valueOf("162"))
                .set(ACTOR.BLOOD, "B")
                .set(ACTOR.BIRTHDAY, DateParser.parseSQL("1938-03-31"))
                .set(ACTOR.BIRTHPLACE_ID, Short.valueOf("13"))
                .execute();

      logger.info("set:{}", numOfSet);

      /*
       * returning
       */
      ActorRecord actor =
          create.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME, ACTOR.HEIGHT, ACTOR.BLOOD, ACTOR.BIRTHDAY, ACTOR.BIRTHPLACE_ID)
                .values(16, "野村昭子", Short.valueOf("157"), "A", DateParser.parseSQL("1927-01-02"), Short.valueOf("13"))
                .returning(ACTOR.ID, ACTOR.UPDATE_AT)
                .fetchOne();

      logger.info("returning, id:{} updateAt:{}", actor.getId(), actor.getUpdateAt());

      /**
       * batch
       */
      int[] numOfBatch =
          create.batch(
            create.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME).values(17,"穂積隆信"),
            create.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME).values(18,"信欣三"),
            create.insertInto(ACTOR, ACTOR.ID, ACTOR.NAME).values(19,"浜村純")
          )
          .execute();

      for (int i=0; i<numOfBatch.length; i++) {
        logger.info("status[{}]:{}", i, numOfBatch[i]);
      }

      Result<Record> result =
          create.select()
                .from(ACTOR)
                .fetch();

      result.stream().forEach(r->{
        logger.info("select, id:{} name:{} birthday:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.BIRTHDAY));
      });

    } catch(Exception e) {
      e.printStackTrace();
    }

  }

}
