#!/bin/sh

KEYTOOL=/usr/local/java/jdk1.8.0_241/bin/keytool
CNAME=192.168.1.122

KEYSTORE=/home/alex/appserver/apache-tomcat-9.0.34/scripts_for_ssl/keystore.jks
KEYSTOREBKS=/home/alex/appserver/apache-tomcat-9.0.34/scripts_for_ssl/keystore.bks

FILECERT=codeprojectsslcert.cer

BKSJAR=/home/alex/appserver/apache-tomcat-9.0.34/scripts_for_ssl/bcprov-jdk15on-146.jar

$KEYTOOL -import -alias tomcat -file $FILECERT -keystore $KEYSTOREBKS -storetype BKS -providerClass org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath $BKSJAR
