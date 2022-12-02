package org.growhack.bank.portal.model;

import lombok.Data;

@Data
public abstract class Response {

    public int code;
    public String messages;

    public void onFail() {
        setCode(400);
        setMessages("fail");
    }

    public void onSuccess() {
        setCode(200);
        setMessages("success");
    }

}
