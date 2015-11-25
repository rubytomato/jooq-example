# 概要

jOOQはHibernateやMyBatisなどと同じORMフレームワークです。
このアプリケーションはjOOQサイトのjooq-in-7-stepsというチュートリアルの内容をもとに
sqlの発行の仕方を確認するためのexampleになります。


**環境**

* Java 1.8.0_60
* jOOQ (Open Source) 3.6.2
* MySQL 5.6.25
* eclipse 4.4
* maven 3.3.3


## 事前準備


### データベースの作成

データベースにMySQL 5.6.25を使用します。

* データベース名： actor_db
* ユーザー: actor_user / actor_pass

```
create database if not exists actor_db;

create user 'actor_user'@'localhost' identified by 'actor_pass';

grant all on actor_db.* to 'actor_user'@'localhost';
```

### テーブル、初期データおよびjavaコードの生成

下記のコマンドを実行するとテーブルと初期データ作成し、テーブルからjavaコードを生成します。

```
> mvn clean generate-sources -Pgenerate
```

#### sql-maven-plugin

テーブルの作成やデータの投入はmavenのsql-maven-pluginを使用します。

プラグインによって実行されるsqlは`src\main\resources\sql`ディレクトリに格納しています。

* 01-schema.sql
* 02-data.sql


#### jooq-codegen-maven

javaコードの生成はjooq-codegen-mavenを使用します。

このプラグインはjOOQが開発しているコードジェネレータ(jooq-codegen)をmavenから使用できるようにしたものです。
対象データベースのスキーマからjOOQで使用するモデルクラスやDAOクラスなどを生成することができます。


### SQL

jOOQを使用すると下記のようなコードでSQL文を組み立て発行することができます。

#### select

Actorテーブルから全件を取得するselect文

```
Result<Record> result =
    create.select()
          .from(ACTOR)
          .fetch();
```

```
Result<Record> resultJoin1 =
    create.select()
          .from(ACTOR.join(PREFECTURE).on(PREFECTURE.ID.eq(ACTOR.BIRTHPLACE_ID)))
          .fetch();
```

```
Record result4 =
    create.select()
          .from(ACTOR)
          .join(PREFECTURE).on(ACTOR.BIRTHPLACE_ID.eq(PREFECTURE.ID))
          .where(ACTOR.ID.eq(1))
          .and(ACTOR.BLOOD.eq("O"))
          .fetchOne();
```

#### update

```
int numOfUpdate =
    create.update(ACTOR)
          .set(ACTOR.UPDATE_AT, new Timestamp(new Date().getTime()))
          .where(ACTOR.BLOOD.eq("O"))
          .execute();
```

#### delete

```
int numOfDelete =
    create.delete(ACTOR)
          .where(ACTOR.ID.greaterOrEqual(14))
          .execute();
```

