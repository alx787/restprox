#!/bin/sh

KEYTOOL=/usr/local/java/jdk1.8.0_241/bin/keytool
CNAME=192.168.1.122
KEYSTORE=/home/alex/appserver/apache-tomcat-9.0.34/scripts_for_ssl/keystore.jks

$KEYTOOL -list -keystore $KEYSTORE
