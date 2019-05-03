package it;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.AccountDto;
import dto.TransferDto;
import models.Account;
import org.hamcrest.Matchers;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import services.RepositoryService;
import services.RepositoryServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNull.notNullValue;
import static play.test.Helpers.*;

public class IntegrationTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testUpsertCreation() throws IOException {
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).bodyJson(Json.toJson(mockAccount1())).uri("/api/1.0/accounts/upsert");
        AccountDto accountDto = new ObjectMapper().readValue(contentAsString(route(app, request)), AccountDto.class);
        assertThat(accountDto.getName(), is("Test1"));
    }


    @Test
    public void testUpsertAltering() throws IOException {
        //create new
        JsonNode jsonMock1 = Json.toJson(mockAccount1());
        new Http.RequestBuilder().method(POST).bodyJson(jsonMock1).uri("/api/1.0/accounts/upsert");

        //update newly created
        AccountDto accountDto = mockAccount2();
        accountDto.setId(1L);
        JsonNode jsonMock2 = Json.toJson(mockAccount2());
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).bodyJson(jsonMock2).uri("/api/1.0/accounts/upsert");
        AccountDto resultAccountDto = new ObjectMapper().readValue(contentAsString(route(app, request)), AccountDto.class);

        assertThat(resultAccountDto.getName(), is("Test2"));
    }

    @Test
    public void testListContainsCreatedItems() throws IOException {
        RepositoryService repositoryService = app.injector().instanceOf(RepositoryServiceImpl.class);
        repositoryService.upsertAccount(mockAccount1());
        repositoryService.upsertAccount(mockAccount1());
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri("/api/1.0/accounts/list");
        Result result = route(app, request);
        final String body = contentAsString(result);
        List<AccountDto> accountDtos = new ObjectMapper().readValue(body, new TypeReference<List<AccountDto>>(){});
        assertThat(accountDtos, is(notNullValue()));
        assertThat(accountDtos, hasSize(2));
    }

    @Test
    public void testTransfers() throws IOException {
        //prepare 4 accounts
        RepositoryService repositoryService = app.injector().instanceOf(RepositoryServiceImpl.class);
        Account accountDto1 = repositoryService.upsertAccount(mockAccount1());
        Account accountDto2 = repositoryService.upsertAccount(mockAccount2());
        Account accountDto3 = repositoryService.upsertAccount(mockAccount1());
        Account accountDto4 = repositoryService.upsertAccount(mockAccount2());

        //transfer from 1st to 2nd
        Account account1 = Account.find.byId(accountDto1.getId());
        Account account2 = Account.find.byId(accountDto2.getId());
        repositoryService.transferMoney(account1, account2, new BigDecimal(20));

        //transfer from 3rd to 4th
        Account account3 = Account.find.byId(accountDto3.getId());
        Account account4 = Account.find.byId(accountDto4.getId());
        repositoryService.transferMoney(account3, account4, new BigDecimal(30));

        //verify balances
        assertThat(account1.getBalance(), comparesEqualTo(new BigDecimal(80)));
        assertThat(account2.getBalance(), comparesEqualTo(new BigDecimal(70)));
        assertThat(account3.getBalance(), comparesEqualTo(new BigDecimal(70)));
        assertThat(account4.getBalance(), comparesEqualTo(new BigDecimal(80)));

        //get transfer list
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri("/api/1.0/accounts/transferList");
        List<TransferDto> transferDtos = new ObjectMapper().readValue(contentAsString(route(app, request)),
                new TypeReference<List<TransferDto>>(){});

        //verify transactions
        assertThat(
                transferDtos,
                hasItem(allOf(
                        Matchers.<TransferDto>hasProperty("sourceAccountId", is(accountDto1.getId())),
                        Matchers.<TransferDto>hasProperty("targetAccountId", is(account2.getId())),
                        Matchers.<TransferDto>hasProperty("amount", comparesEqualTo(new BigDecimal(20)))
                ))
        );

        assertThat(
                transferDtos,
                hasItem(allOf(
                        Matchers.<TransferDto>hasProperty("sourceAccountId", is(accountDto3.getId())),
                        Matchers.<TransferDto>hasProperty("targetAccountId", is(account4.getId())),
                        Matchers.<TransferDto>hasProperty("amount", comparesEqualTo((new BigDecimal(30)))
                ))
        ));
    }

    private AccountDto mockAccount1() {
        AccountDto accountDto = new AccountDto();
        accountDto.setActive(true);
        accountDto.setBalance(new BigDecimal(100));
        accountDto.setName("Test1");
        accountDto.setCurrencyCode(Currency.getInstance("EUR"));
        return accountDto;
    }

    private AccountDto mockAccount2() {
        AccountDto accountDto = new AccountDto();
        accountDto.setActive(true);
        accountDto.setBalance(new BigDecimal(50));
        accountDto.setName("Test2");
        accountDto.setCurrencyCode(Currency.getInstance("EUR"));
        return accountDto;
    }

}
