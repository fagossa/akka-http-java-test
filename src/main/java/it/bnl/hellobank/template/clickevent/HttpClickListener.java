package it.bnl.hellobank.template.clickevent;

import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

import static akka.http.javadsl.model.StatusCodes.FORBIDDEN;
import static akka.http.javadsl.model.StatusCodes.OK;
import static akka.http.javadsl.model.headers.Connection.create;

public class HttpClickListener extends AllDirectives {

    public Route routes() {
        return getRoute()
                .orElse(postRoute());
    }

    private Route getRoute() {
        return get(() -> pathEndOrSingleSlash(() -> complete(OK, "OK")));
    }

    private Route postRoute() {
        return post(() -> path("click-events", () ->
                        extractDataBytes(bytes ->
                                respondWithHeader(create("close"), () -> complete(FORBIDDEN, "Not allowed!"))
                        )
                )
        );
    }

}
