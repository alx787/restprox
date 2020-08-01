#!/bin/sh

#KEYTOOL=/usr/local/java/jdk1.8.0_241/bin/keytool
KEYTOOL=/usr/local/jdk1.8.0_241/bin/keytool

#CNAME=192.168.1.2
CNAME=113.203.122.18

#KEYSTORE=/home/alex/appserver/apache-tomcat-9.0.34/scripts_for_ssl/keystore.jks
KEYSTORE=keystore.jks


$KEYTOOL -v -genkey -dname "CN=$CNAME, OU=Developers, O=IT Systems Inc., L=Kirov, C=RU" \
    -ext SAN=dns:localhost,dns:www.godzilla.com -alias tomcat -storetype jks -keystore $KEYSTORE \
    -validity 3650 -keyalg RSA -keysize 2048 -storepass mystorepass -keypass mystorepass
