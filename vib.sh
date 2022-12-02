export LIB_DIR=compile
export JAR=bank-portal-alternative-vib-1.0-SNAPSHOT.jar
export CLASS_PATH=$LIB_DIR/*

export MAIN_CLASS=org.growhack.bank.portal.job.VIBTransScanner

java $JAVA_OPTS -classpath "$JAR;$CLASS_PATH" $MAIN_CLASS;
