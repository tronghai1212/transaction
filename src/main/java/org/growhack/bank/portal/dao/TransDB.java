package org.growhack.bank.portal.dao;

import org.growhack.bank.portal.model.Transaction;
import org.growhack.bank.portal.model.TransactionStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TransDB {

    public static Logger LOG = LoggerFactory.getLogger(TransDB.class);

    private String dbName;
    private TransactionStorage transactionStorage;
    private final String dir = "localdb";
    private String url = System.getProperty("user.dir");

    public TransDB(String dbName){
        this.dbName = dbName +".db";
    }

    public void load(){
        try {

            TransactionStorage transactionStorage = (TransactionStorage) load(getPathOfFileDB());

            if(transactionStorage != null) {
                this.transactionStorage = transactionStorage;
            }
            else
                this.transactionStorage = new TransactionStorage();

        }catch (Exception e){
            LOG.warn("Load mini db error ", e);
            transactionStorage = new TransactionStorage();
        }

    }

    public String getPathOfFileDB(){
        Path root = Paths.get(".").normalize().toAbsolutePath();
        Path dbFile = root.resolve(dir).resolve(dbName);
        return dbFile.toString();

    }

    public boolean isCollected(String transId){
        return transactionStorage.isExits(transId);
    }

    public List<Transaction> getListTransaction(){
        return transactionStorage.getTransactionList();
    }

    public void updateTransaction(Transaction transaction){
        transactionStorage.addNewTransaction(transaction);
    }

    public void save() throws IOException {
        FileOutputStream fout = new FileOutputStream(getPathOfFileDB());
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(transactionStorage);
        oos.close();
    }

    public static Object load(String fileName) throws Exception{
        File file = new File(fileName);
        if (file.exists()){
            LOG.info("File " + fileName + " exits");
        } else {
            LOG.info("File " + fileName + " not exits");
        }

        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object result = null;
        try {
            result = ois.readObject();
        } catch (OptionalDataException e) {
            if (!e.eof) throw e;
        } finally {
            ois.close();
        }
        return result;
    }

    public void readData(){
        try {
            this.load();
            LOG.info("The data is : " + this.transactionStorage );
        }catch (Exception e){
            LOG.warn("Can't load data from db file", e);
        }
    }

    public static void main(String[] args) {
        TransDB transDB = new TransDB("VIB");
        transDB.readData();
    }

}
