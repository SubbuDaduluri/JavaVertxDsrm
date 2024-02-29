package com.subbu.dsrm;

import com.subbu.dsrm.verticle.MainVerticle;
import io.vertx.reactivex.core.Vertx;

public class Main {
    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();

        vertx.rxDeployVerticle(MainVerticle.class.getName())
            .subscribe(
                verticle -> System.out.println("New verticle started!"),
                throwable -> {
                    System.out.println("Error occurred before deploying a new verticle: " + throwable.getMessage());
                    System.exit(1);
                });
    }

}
