#!/bin/sh

#
# Daemon起動
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
  -Dlogback.configurationFile=/usr/local/skinny/batch/conf/logback-daemon.xml"

JAVA_HOME=/usr/lib/jvm/jre

JVM=server
JAVA_USER=root
JAVA_MAIN_CLASS=daemon.DaemonMain
PID_FILE=/var/run/app-daemon.pid

JAVA_STDOUT=/var/log/apps/daemon-stdout.log
JAVA_STDERR=/var/log/apps/daemon-stderr.log

JSVC_ARGS="-jvm $JVM -user $JAVA_USER "
JSVC_ARGS="$JSVC_ARGS -outfile $JAVA_STDOUT -errfile $JAVA_STDERR"

/usr/bin/jsvc $JSVC_ARGS $JAVA_OPTS -cp $APP_HOME/app.jar \
  -home $JAVA_HOME -pidfile $PID_FILE \
  $JAVA_MAIN_CLASS
