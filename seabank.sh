export LIB_DIR=compile
export JAR=bank-portal-alternative-seabank-1.0-SNAPSHOT.jar
export CLASS_PATH=$LIB_DIR/*

export MAIN_CLASS=com.code12.bank.portal.job.SeabankTransScanner

java $JAVA_OPTS -classpath "$JAR;$CLASS_PATH" $MAIN_CLASS;
