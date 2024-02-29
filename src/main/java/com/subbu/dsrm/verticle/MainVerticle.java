package com.subbu.dsrm.verticle;

import com.subbu.dsrm.handler.BookHandler;
import com.subbu.dsrm.repository.BookRepository;
import com.subbu.dsrm.router.BookRouter;
import com.subbu.dsrm.service.BookService;
import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    private static final String CONF_HOST = "datasource.host";
    private static final String CONF_SERVER_PORT = "server.port=";
    private static final String CONF_DATABASE_NAME = "datasource.dbname";
    private static final String CONF_USER_NAME = "datasource.username";
    private static final String CONF_PASSWORD = "datasource.password";
    private static final String CONF_AUTH_SOURCE = "datasource.authsource";

    @Override
    public void start() {
        final ConfigStoreOptions store = new ConfigStoreOptions().setType("env");
        ConfigStoreOptions file = new ConfigStoreOptions()
            .setType("file")
            .setFormat("properties")
            .setConfig(new JsonObject().put("path", "src/main/resources/conf/conf.properties"));
        final ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(file);
        final ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.rxGetConfig()
            .flatMap(configurations -> {
                final MongoClient client = createMongoClient(vertx, configurations);

                final BookRepository bookRepository = new BookRepository(client);
                final BookService bookService = new BookService(bookRepository);
                final BookHandler bookHandler = new BookHandler(bookService);
                final BookRouter bookRouter = new BookRouter(vertx, bookHandler);

                return createHttpServer(bookRouter.getRouter(), configurations);
            })
            .subscribe(
                server -> System.out.println("HTTP Server listening on port " + server.actualPort()),
                throwable -> {
                    System.out.println("Error occurred before creating a new HTTP server: " + throwable.getMessage());
                    System.exit(1);
                });
    }

    // Private methodss
    private MongoClient createMongoClient(Vertx vertx, JsonObject configurations) {
        final JsonObject config = new JsonObject()
            .put("host", configurations.getString(CONF_HOST))
            .put("username", configurations.getString(CONF_USER_NAME))
            .put("password", configurations.getString(CONF_PASSWORD))
            .put("authSource", configurations.getString(CONF_AUTH_SOURCE))
            .put("db_name", configurations.getString(CONF_DATABASE_NAME))
            .put("useObjectId", true);

        return MongoClient.createShared(vertx, config);
    }

    private Single<HttpServer> createHttpServer(Router router, JsonObject configurations) {
        return vertx
            .createHttpServer()
            .requestHandler(router)
            .rxListen(configurations.getInteger("server.port"));
    }

}
