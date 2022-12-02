#!bin/bash
export LIB_DIR=compile
export JAR=bank-portal-alternative-1.0.jar
export CLASS_PATH=$LIB_DIR/*


export MAIN_CLASS=org.growhack.bank.portal.job.AgribankTransScanner
java $JAVA_OPTS -classpath "$JAR:$CLASS_PATH" $MAIN_CLASS;
