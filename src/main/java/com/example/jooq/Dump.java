package com.example.jooq;

import static com.example.jooq.db.tables.Actor.*;

import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.jooq.db.tables.records.ActorRecord;

public class Dump {
  private static final Logger logger = LoggerFactory.getLogger(Dump.class);




  public static void dump(Result<Record> result) {
    result.stream().forEach(r->{
      Integer id = r.getValue(ACTOR.ID);
      String name = r.getValue(ACTOR.NAME);
      Short height = r.getValue(ACTOR.HEIGHT);
      String blood = r.getValue(ACTOR.BLOOD);
      logger.info("id:{} name:{} height:{} blood:{}", id, name, height, blood);
    });
  }

  public static void dumpActorRecord(Result<ActorRecord> result) {
    result.stream().forEach(r->{
      logger.info("id:{} name:{} height:{} blood:{}", r.getId(), r.getName(), r.getHeight(), r.getBlood());
    });
  }

  public static void dumpList(List<String> list) {
    list.stream().forEach(logger::info);
  }

  public static void dumpMap(Map<Integer,String> map) {
    map.forEach((key,value)->{
      logger.info("id:{} name:{}", key, value);
    });
  }

}
