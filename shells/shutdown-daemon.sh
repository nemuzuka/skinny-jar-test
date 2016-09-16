#!/bin/sh

#
# Daemon停止
#

JAVA_MAIN_CLASS=daemon.DaemonMain
PID_FILE=/var/run/app-daemon.pid

/usr/bin/jsvc -pidfile $PID_FILE -stop $JAVA_MAIN_CLASS
