package org.growhack.bank.portal.service;

import org.growhack.bank.portal.controller.BankController;
import io.javalin.Javalin;

public class HttpService {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(8009);
        app.routes(() -> {

            BankController bankController = new BankController();
            app.get("/banks/vib/getTransaction", ctx -> {
                bankController.getAll(ctx);
            });

        });

        app.get("/", ctx -> ctx.result("Hello World"));

    }

}
