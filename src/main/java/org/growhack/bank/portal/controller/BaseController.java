package org.growhack.bank.portal.controller;

import org.growhack.bank.portal.model.Response;
import io.javalin.http.Context;
import java.util.Optional;

public class BaseController <T extends Response>{

    protected void handleOptionalResponse(Context ctx, Optional<T> response) {
        response.map(ctx::json)
                .orElse(ctx.status(200));
    }

}
