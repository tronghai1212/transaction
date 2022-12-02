package org.growhack.bank.portal.controller;

import org.growhack.bank.portal.model.ResponseArray;
import org.growhack.bank.portal.model.Transaction;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public class BankController extends BaseController implements CrudHandler {

    @Override
    public void getAll(@NotNull Context context) {
        ResponseArray<Transaction> responseArray = new ResponseArray<Transaction>();
        responseArray.setData(null);
        responseArray.onSuccess();
        handleOptionalResponse(context, Optional.of(responseArray));

    }

    @Override
    public void create(@NotNull Context context) {

    }

    @Override
    public void delete(@NotNull Context context, @NotNull String s) {

    }

    @Override
    public void getOne(@NotNull Context context, @NotNull String s) {

    }

    @Override
    public void update(@NotNull Context context, @NotNull String s) {

    }


}
