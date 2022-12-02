@ECHO OFF
set LIB_DIR=compile
set JAR=bank-portal-alternative-1.0-SNAPSHOT.jar
set CLASS_PATH=$LIB_DIR/*

set MAIN_CLASS=com.code12.bank.portal.job.AllOffBankScanner

%JAVA_HOME%\bin\java -Xms512m -Xmx2048m -classpath "$JAR;$CLASS_PATH" $MAIN_CLASS;
