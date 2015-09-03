package com.example.jooq;

import static com.example.jooq.db.Tables.*;
import static org.jooq.impl.DSL.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.Date;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateSample {
  private static final Logger logger = LoggerFactory.getLogger(UpdateSample.class);

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
       * update
       */
      int numOfUpdate =
          create.update(ACTOR)
                .set(ACTOR.UPDATE_AT, new Timestamp(new Date().getTime()))
                .where(ACTOR.BLOOD.eq("O"))
                .execute();

      logger.info("update:{}", numOfUpdate);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
