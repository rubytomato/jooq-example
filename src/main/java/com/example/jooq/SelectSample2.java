package com.example.jooq;

import static com.example.jooq.db.Tables.*;
import static org.jooq.impl.DSL.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.jooq.db.tables.records.ActorRecord;

public class SelectSample2 {
  private static final Logger logger = LoggerFactory.getLogger(SelectSample2.class);

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
       * list
       */
      List<String> nameList =
          create.select()
                .from(ACTOR)
                .limit(5)
                .fetch(ACTOR.NAME);
      nameList.stream().forEach(logger::info);

      /*
       * map
       */
      Map<Integer, String> idMap =
          create.select()
                .from(ACTOR)
                .limit(5)
                .fetchMap(ACTOR.ID, ACTOR.NAME);
      idMap.forEach((key,value)->{
        logger.info("id:{} name:{}", key, value);
      });

      /*
       * map
       */
      Map<Integer, ActorRecord> recordMap =
          create.selectFrom(ACTOR)
                .limit(5)
                .fetch()
                .intoMap(ACTOR.ID);
      recordMap.forEach((key,value)->{
        logger.info("id:{} name:{} height:{} blood:{}", key, value.getName(), value.getHeight(), value.getBlood());
      });

      /*
       * cursor 
       */
      Cursor<ActorRecord> cursor = null;
      try {
        cursor = create.selectFrom(ACTOR).limit(5).fetchLazy();
        while (cursor.hasNext()) {
          ActorRecord r = cursor.fetchOne();
          logger.info("cursor, id:{} name:{} birthday:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.BIRTHDAY));
        }
      } finally {
        if (cursor != null) {
          cursor.close();
        }
      }

      /*
       * export json
       */
      
      String json =
          create.select()
                .from(ACTOR)
                .limit(3)
                .fetch()
                .formatJSON();
      logger.info("json:{}", json);

    } catch(Exception e) {
      e.printStackTrace();
    }

  }
  
}
