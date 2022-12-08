package org.growhack.bank.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.growhack.bank.portal.model.Transaction;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//import javax.persistence.*;

//@Entity
//@Table(name = "transaction")
@Getter
@Setter
public class TransactitonBankEntity {

    private String id;
    private String senderId;
    private String sendAccount;
    private String bankOfSender;
    private Long amount;
    private String messages;
    private String receivedId;
    private String receivedAccount;
    private String bankOfReceiver;
    private Long moneyBalance;
    private String transactionId;
    private Date createdTime;
    private Date updatedTime;
    private Long user_id;
    private String status;

    public TransactitonBankEntity convertToTransactionBank(Transaction transaction){
        TransactitonBankEntity entity = new TransactitonBankEntity();
        entity.setSenderId(transaction.getSenderId());
        entity.setSendAccount(transaction.getSendAccount());
        entity.setBankOfSender(transaction.getBankOfSender());
        entity.setAmount(Long.parseLong(transaction.getAmount()));
        entity.setMessages(transaction.getMessages());
        entity.setReceivedId(transaction.getReceiverId());
        entity.setReceivedAccount(transaction.getReceiverAccount());
        entity.setBankOfReceiver(transaction.getBankOfReceiver());
        entity.setMoneyBalance(Long.parseLong(transaction.getMoneyBalance()));
        entity.setTransactionId(transaction.getTransId());
        try {
            entity.setCreatedTime((Date) new SimpleDateFormat("dd/MM/yyyy").parse(transaction.getCreatedTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return entity;
    }

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private String id;
//
//    @Column(name = "created_time")
//    private String createdTime;
//
//    @Column(name = "amount")
//    private String amount;
//
//    @Column(name = "messages")
//    private String messages;
//
//    @Column(name = "sender_id")
//    private String senderId;
//
//    @Column(name = "send_account")
//    private String sendAccount;
//
//    @Column(name = "bank_of_sender")
//    private String bankOfSender;
//
//    @Column(name = "received_id")
//    private String receivedId;
//
//    @Column(name = "received_account")
//    private String receivedAccount;
//
//    @Column(name = "bank_of_receiver")
//    private String bankOfReceiver;
//
//    @Column(name = "money_balance")
//    private String moneyBalance;
//
//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_transaction_user_id"))
//    @JsonIgnore
//    private UserEntity user;
}
