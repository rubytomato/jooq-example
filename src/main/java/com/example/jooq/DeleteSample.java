package com.example.jooq;

import static com.example.jooq.db.Tables.*;
import static org.jooq.impl.DSL.*;

import java.sql.Connection;
import java.sql.DriverManager;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteSample {
  private static final Logger logger = LoggerFactory.getLogger(DeleteSample.class);

  public static void main(String[] args) {

    String userName = "actor_user";
    String password = "actor_pass";
    String url = "jdbc:mysql://localhost:3306/actor_db";

    try (Connection conn = DriverManager.getConnection(url, userName, password)) {

      Settings settings = new Settings();
      settings.setExecuteLogging(true);
      settings.withRenderFormatted(true);

      DSLContext create = using(conn, SQLDialect.MYSQL, settings);

      /*
       * delete
       */
      int numOfDelete =
          create.delete(ACTOR)
                .where(ACTOR.ID.greaterOrEqual(14))
                .execute();

      logger.info("delete:{}", numOfDelete);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
