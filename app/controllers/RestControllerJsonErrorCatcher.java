package controllers;

import helpers.TransferMoneyException;
import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Error catcher uses with RestController.
 * Wraps errors into json response.
 */
public class RestControllerJsonErrorCatcher extends Action.Simple {

    private static final Logger.ALogger logger = Logger.of(RestControllerJsonErrorCatcher.class);

    @Override
    public CompletionStage<Result> call(Http.Request req) {

        try {
            return delegate.call(req);
        } catch (TransferMoneyException me) {
            logger.error("error during processing incoming request", me);
            return CompletableFuture.completedFuture(badRequest(Json.toJson(me.getMessage())));
        } catch (Exception e) {
            logger.error("error during processing incoming request", e);
            return CompletableFuture.completedFuture(internalServerError(Json.toJson("sorry, internal error appeared:" + e.toString())));
        }
    }
}
