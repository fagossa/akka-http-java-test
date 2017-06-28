package it.bnl.hellobank.execution;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import it.bnl.hellobank.template.clickevent.HttpClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class Executor {

    private static final Logger logger = LoggerFactory.getLogger(Executor.class);

    public static void main(String... args) throws IOException {
        ActorSystem system = ActorSystem.create("bnl-producer-template");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final HttpClickListener listener = new HttpClickListener();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                listener.routes().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("0.0.0.0", 9000), materializer);

        binding
                .exceptionally(failure -> {
                    logger.error("Something very bad happened! ", failure);
                    system.terminate();
                    return null;
                });
        //system.terminate();
    }

}
