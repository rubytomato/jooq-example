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

public class JooqIn7Steps {
  private static final Logger logger = LoggerFactory.getLogger(JooqIn7Steps.class);

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
       * select
       */
      Result<Record> result =
          create.select()
                .from(ACTOR)
                .limit(5)
                .fetch();

      for (Record r : result) {
        Integer id = r.getValue(ACTOR.ID);
        String name = r.getValue(ACTOR.NAME);
        Short height = r.getValue(ACTOR.HEIGHT);
        String blood = r.getValue(ACTOR.BLOOD);
        logger.info("id:{} name:{} height:{} blood:{}", id, name, height, blood);
      }

      /*
       * join
       */
      Result<Record> resultJoin =
          create.select()
                .from(ACTOR.join(PREFECTURE).on(PREFECTURE.ID.equal(ACTOR.BIRTHPLACE_ID)))
                .limit(5)
                .fetch();

      for (Record r : resultJoin) {
        Integer id = r.getValue(ACTOR.ID);
        String name = r.getValue(ACTOR.NAME);
        Short height = r.getValue(ACTOR.HEIGHT);
        String blood = r.getValue(ACTOR.BLOOD);
        String prefname = r.getValue(PREFECTURE.NAME);
        logger.info("id:{} name:{} height:{} blood:{} pref:{}", id, name, height, blood, prefname);
      }

      /*
       * select from
       */
      Result<ActorRecord> actorResult =
          create.selectFrom(ACTOR)
                .limit(5)
                .fetch();
      for (ActorRecord r : actorResult) {
        logger.info("id:{} name:{} height:{} blood:{}", r.getId(), r.getName(), r.getHeight(), r.getBlood());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
