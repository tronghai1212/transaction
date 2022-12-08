package org.growhack.bank.portal.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
//import org.growhack.bank.portal.entity.TransactionBankRepository;
//import org.growhack.bank.portal.entity.TransactitonBankEntity;
import org.growhack.bank.portal.entity.TransactitonBankEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@Data
public class Transaction extends Optional implements Serializable, Externalizable {

//    @Autowired
//    TransactionBankRepository repository;

    public static Logger LOG = LoggerFactory.getLogger(Transaction.class);

    private static final long serialVersionUID = 1L;

    @SerializedName("transId")
    private String transId;
    @SerializedName("transTime")
    private String createdTime;
    @SerializedName("amount")
    private String amount;
    @SerializedName("comment")
    private String messages;
    @SerializedName("fromBankNo")
    private String senderId;
    @SerializedName("fromBankName")
    private String sendAccount;
    @SerializedName("fromBank")
    private String bankOfSender;
    @SerializedName("toBankNo")
    private String receiverId;
    @SerializedName("toBankAccount")
    private String receiverAccount;
    @SerializedName("toBank")
    private String bankOfReceiver;
    @SerializedName("balance")
    private String moneyBalance;


    public Transaction() {
    }

    //{"date":"30/11/2021","mess":"TIMA CT CHO HOANG THI KIM ANH THEO TT","money":"- 7,295,363 ₫","trans_id":"3716940792","acc":"","name":"VIB","bank":"VIB"}

    public Transaction(JsonObject jsonObject) {
        try {
            if (jsonObject.get("date") != null) {
                createdTime = jsonObject.get("date").getAsString();
            }
            if (jsonObject.get("money") != null) {
                amount = jsonObject.get("money").getAsString();
            }
            if (jsonObject.get("mess") != null) {
                messages = jsonObject.get("mess").getAsString();
            }
            if (jsonObject.get("acc") != null) {
                senderId = jsonObject.get("acc").getAsString();
            }
            if (jsonObject.get("name") != null) {
                sendAccount = jsonObject.get("name").getAsString();
            }
            if (jsonObject.get("bank") != null) {
                bankOfSender = jsonObject.get("bank").getAsString();
            }
            if (jsonObject.get("trans_id") != null) {
                transId = jsonObject.get("trans_id").getAsString();
            }
            if (jsonObject.get("balance") != null) {
                moneyBalance = jsonObject.get("balance").getAsString();
            }
        } catch (Exception e) {
            LOG.warn("Parser json error : " + jsonObject);
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "createdTime='" + createdTime + '\'' +
                ", amount='" + amount + '\'' +
                ", messages='" + messages + '\'' +
                ", senderId='" + senderId + '\'' +
                ", sendAccount='" + sendAccount + '\'' +
                ", bankOfSender='" + bankOfSender + '\'' +
                ", transId='" + transId + '\'' +
                ", balance='" + moneyBalance + '\'' +
                '}';
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        if (createdTime == null)
            createdTime = "";
        if (amount == null)
            amount = "";
        if (messages == null)
            messages = "";
        if (senderId == null)
            senderId = "";
        if (sendAccount == null)
            sendAccount = "";
        if (bankOfSender == null)
            bankOfSender = "";
        if (transId == null)
            transId = "";
        out.writeUTF(bankOfSender);
        out.writeUTF(sendAccount);
        out.writeUTF(senderId);
        out.writeUTF(messages);
        out.writeUTF(amount);
        out.writeUTF(createdTime);
        out.writeUTF(transId);

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        createdTime = in.readUTF();
        amount = in.readUTF();
        messages = in.readUTF();
        senderId = in.readUTF();
        sendAccount = in.readUTF();
        bankOfSender = in.readUTF();
        transId = in.readUTF();

    }



}
