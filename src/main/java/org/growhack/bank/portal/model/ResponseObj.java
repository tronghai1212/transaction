package org.growhack.bank.portal.model;

import lombok.Data;

@Data
public class ResponseObj<T extends  Optional> extends Response {

    public T data;

}
