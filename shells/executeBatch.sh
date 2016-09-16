#!/bin/sh

#
# Skinny Frameworkで作成したbatch処理の起動Shell
# jarは ./skinny package:standalone で作成したものをapp.jarにrenameします
#

APP_HOME=/usr/local/skinny/batch/

# DB接続情報
export DATABASE_HOST=localhost
export DATABASE_DBNAME=production
export DATABASE_USER=postgres
export DATABASE_PASSWORD=password

export JAVA_OPTS="-Dfile.encoding=UTF-8 \
  -Dskinny.env=production \
  -Djava.io.tmpdir=/var/tmp \
  -Dlogback.configurationFile=/usr/local/skinny/batch/conf/logback-batch.xml \
  -server"

java $JAVA_OPTS -cp $APP_HOME/app.jar batch.BatchMain
