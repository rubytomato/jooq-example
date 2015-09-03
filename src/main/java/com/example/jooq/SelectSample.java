package com.example.jooq;

import static com.example.jooq.db.Tables.*;
import static org.jooq.impl.DSL.*;

import java.sql.Connection;
import java.sql.DriverManager;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.jooq.db.tables.records.ActorRecord;
import com.example.jooq.db.tables.records.PrefectureRecord;

public class SelectSample {
  private static final Logger logger = LoggerFactory.getLogger(SelectSample.class);

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
                .fetch();
      result.stream().forEach(r->{
        logger.info("select, id:{} name:{} height:{} blood:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.BLOOD), r.getValue(ACTOR.HEIGHT));
      });

      /*
       * sql dump
       */
      String sql = create.select().from(ACTOR).getSQL();
      logger.info("sql:{}", sql);

      /*
       * 
       */
      Result<Record4<Integer, String, Short, String>> resultField =
          create.select(ACTOR.ID, ACTOR.NAME, ACTOR.HEIGHT, ACTOR.BLOOD)
                .from(ACTOR)
                .limit(5)
                .fetch();
      resultField.stream().forEach(r->{
        logger.info("id:{} name:{} height:{} blood:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.HEIGHT), r.getValue(ACTOR.BLOOD));
      });

      /*
       * join
       */
      Result<Record> resultJoin1 =
          create.select()
                .from(ACTOR.join(PREFECTURE).on(PREFECTURE.ID.eq(ACTOR.BIRTHPLACE_ID)))
                .fetch();

      
      /*
       * join
       */
      Result<Record> resultJoin2 =
          create.select()
                .from(ACTOR)
                .join(PREFECTURE).on(PREFECTURE.ID.eq(ACTOR.BIRTHPLACE_ID))
                .fetch();

      /*
       * where
       */
      Record result4 =
          create.select()
                .from(ACTOR)
                .join(PREFECTURE).on(ACTOR.BIRTHPLACE_ID.eq(PREFECTURE.ID))
                .where(ACTOR.ID.eq(1))
                .and(ACTOR.BLOOD.eq("O"))
                .fetchOne();
      logger.info("id:{} name:{} height:{} blood:{}", result4.getValue(ACTOR.ID), result4.getValue(ACTOR.NAME), result4.getValue(ACTOR.HEIGHT), result4.getValue(ACTOR.BLOOD));
      logger.info("id:{} name:{}", result4.getValue(PREFECTURE.ID), result4.getValue(PREFECTURE.NAME));

      /*
       * mapping 
       */
      ActorRecord actor = result4.into(ACTOR);
      logger.info("id:{} name:{} height:{} blood:{}", actor.getId(), actor.getName(), actor.getHeight(), actor.getBlood());
      PrefectureRecord pref = result4.into(PREFECTURE);
      logger.info("id:{} name:{}", pref.getId(), pref.getName());

      /*
       * like
       */
      Result<Record> resultLike =
          create.select()
                .from(ACTOR)
                .join(PREFECTURE).on(ACTOR.BIRTHPLACE_ID.eq(PREFECTURE.ID))
                .where(ACTOR.NAME.like("%山%"))
                .fetch();
      resultLike.stream().forEach(r->{
        logger.info("like, id:{} name:{} height:{} blood:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.HEIGHT), r.getValue(ACTOR.BLOOD));
      });

      /*
       * is not null
       */
      Result<Record> resultIsNotNull =
          create.select()
                .from(ACTOR)
                .join(PREFECTURE).on(ACTOR.BIRTHPLACE_ID.eq(PREFECTURE.ID))
                .where(ACTOR.BLOOD.isNotNull())
                .fetch();
      resultIsNotNull.stream().forEach(r->{
        logger.info("is not null, id:{} name:{} height:{} blood:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.HEIGHT), r.getValue(ACTOR.BLOOD));
      });

      /*
       * groupby
       */
      Result<Record2<String, Integer>> resultGroupBy =
          create.select(ACTOR.BLOOD, count())
                .from(ACTOR)
                .groupBy(ACTOR.BLOOD)
                .fetch();
      resultGroupBy.stream().forEach(r->{
        logger.info("groupby, blood:{} count:{}", r.getValue(0), r.getValue(1));
      });

      /*
       * having
       */
      Result<Record2<String, Integer>> resultHaving =
          create.select(ACTOR.BLOOD, count())
                .from(ACTOR)
                .groupBy(ACTOR.BLOOD)
                .having(count().eq(3))
                .fetch();
      resultHaving.stream().forEach(r->{
        logger.info("having, blood:{} count:{}", r.getValue(0), r.getValue(1));
      });

      /*
       * orderby
       */
      Result<Record> resultOrderby =
          create.select()
                .from(ACTOR)
                .orderBy(ACTOR.BIRTHDAY.asc().nullsLast())
                .limit(5)
                .fetch();
      resultOrderby.stream().forEach(r->{
        logger.info("orderby, id:{} name:{} birthday:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.BIRTHDAY));
      });

      /*
       * case
       */
      Result<Record4<Integer, String, Short, String>> resultCase =
          create.select(ACTOR.ID, ACTOR.NAME, ACTOR.HEIGHT, decode().when(ACTOR.BLOOD.isNull(),"unknown").otherwise(ACTOR.BLOOD).as("blood"))
                .from(ACTOR)
                .fetch();
      resultCase.stream().forEach(r->{
        logger.info("case, id:{} name:{} height:{} blood:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.HEIGHT), r.getValue("blood"));
      });

      /*
       * union
       */
      Result<ActorRecord> resultUnion =
          create.selectFrom(ACTOR).where(ACTOR.ID.eq(1))
                .unionAll(
                 selectFrom(ACTOR).where(ACTOR.ID.eq(2)))
                .fetch();
      resultUnion.stream().forEach(r->{
        logger.info("union, id:{} name:{} birthday:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.BIRTHDAY));
      });

      /*
       * nested
       */
      Result<Record> nestedSelect =
          create.select()
                .from(ACTOR)
                .where(ACTOR.BIRTHPLACE_ID.in(
                    create.select(PREFECTURE.ID)
                          .from(PREFECTURE)
                          .where(PREFECTURE.ID.in((short)12,(short)13,(short)14))))
                .fetch();
      nestedSelect.stream().forEach(r->{
        logger.info("select, id:{} name:{} height:{} blood:{} birthplace_id:{}", r.getValue(ACTOR.ID), r.getValue(ACTOR.NAME), r.getValue(ACTOR.HEIGHT), r.getValue(ACTOR.BLOOD), r.getValue(ACTOR.BIRTHPLACE_ID));
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
