export LIB_DIR=compile
export JAR=bank-portal-alternative-shb-1.0-SNAPSHOT.jar
export CLASS_PATH=$LIB_DIR/*

export MAIN_CLASS=com.code12.bank.portal.job.SHBTransScanner

java $JAVA_OPTS -classpath "$JAR;$CLASS_PATH" $MAIN_CLASS;
