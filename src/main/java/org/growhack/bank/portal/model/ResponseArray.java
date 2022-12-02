package org.growhack.bank.portal.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseArray<T extends Optional> extends Response{

    public List<T> data;

}
