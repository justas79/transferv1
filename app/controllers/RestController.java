package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dto.AccountDto;
import dto.TransferDto;
import helpers.TransferMoneyException;
import models.Account;
import models.Transfer;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.RepositoryService;

import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * Main controller to handle incomming requests.
 * All runtime errors are handled in RestControllerJsonErrorCatcher
 *
 */
@With(RestControllerJsonErrorCatcher.class)
public class RestController extends Controller {

    @Inject
    RepositoryService repositoryService;

    /**
     * Lists all accounts
     */
    public Result listAccounts() {
        return ok(Json.toJson(repositoryService.listAccounts()));
    }

    /**
     * Create or update account. Return error if body is empty or given data is incorrect
     */
    public Result upsertAccount(Http.Request request) {
        JsonNode accountBody = request.body().asJson();

        //validate body
        if (accountBody == null) {
            throw new TransferMoneyException("account details are missing in the request body");
        }

        //validate mandatory fields
        AccountDto accountDto = Json.fromJson(accountBody, AccountDto.class);
        if (accountDto.getName() == null || accountDto.getCurrencyCode() == null) {
            throw new TransferMoneyException("name and currency are mandatory fields");
        }

        //when creating new account and active if omitted, set default to false
        if (accountDto.getId() == null && accountDto.getActive() == null) {
            accountDto.setActive(false);
        }

        return ok(Json.toJson(repositoryService.upsertAccount(accountDto)));
    }


    public Result transferMoney(Http.Request request) {
        JsonNode transferBody = request.body().asJson();

        //validate body contains data
        if (transferBody == null) {
            throw new TransferMoneyException("transfer details are missing in the request body");
        }
        TransferDto transferDto = Json.fromJson(transferBody, TransferDto.class);

        //validate source account is ok
        Account accountFrom = Account.find.byId(transferDto.getSourceAccountId());
        if (accountFrom == null || !accountFrom.isActive()) {
            throw new TransferMoneyException("source account not found or inactive");
        }

        //validate destination account is ok
        Account accountTo = Account.find.byId(transferDto.getTargetAccountId());
        if (accountTo == null || !accountTo.isActive()) {
            throw new TransferMoneyException("destination account not found or inactive");
        }

        if (accountFrom.getId().equals(accountTo.getId())) {
            throw new TransferMoneyException("cannot send money to the same account");
        }

        //validate currencies match
        if (accountFrom.getCurrencyCode() == null ||
                accountTo.getCurrencyCode() == null ||
                !accountFrom.getCurrencyCode().equals(accountTo.getCurrencyCode())) {
            throw new TransferMoneyException("currencies doesn't match on both accounts");
        }

        //validate amount specified
        BigDecimal amount = transferDto.getAmount();
        if (amount == null) {
            throw new TransferMoneyException("amount not specified");
        }

        Transfer transfer = repositoryService.transferMoney(accountFrom, accountTo, amount);
        return ok(transfer.getRequestId().toString());
    }

    public Result transferList() {
        return ok(Json.toJson(repositoryService.listTransfers()));
    }
}