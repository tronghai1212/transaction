package org.growhack.bank.portal.model;

import lombok.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
public class TransactionStorage implements Serializable, Externalizable {

    private static final long serialVersionUID = 1L;

    private String latestTransactionId = "empty";
    private List<Transaction> transactionList = new ArrayList<>();
    private HashSet<Integer> transactionDict = new HashSet<>();

    public boolean isExits(String transId){
        return transactionDict.contains(transId.hashCode());
    }

    public void addNewTransaction(Transaction transaction){
        this.transactionList.add(transaction);
        this.latestTransactionId = transaction.getTransId();
        this.transactionDict.add(transaction.getTransId().hashCode());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(latestTransactionId);
        out.writeInt(transactionList.size());
        for (Transaction t : transactionList){
            t.writeExternal(out);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        transactionList = new ArrayList<>();
        latestTransactionId = in.readUTF();
        int num = in.readInt();
        for (int i = 0; i < num; i ++){
            Transaction transaction = new Transaction();
            transaction.readExternal(in);
            addNewTransaction(transaction);
        }

    }
}
