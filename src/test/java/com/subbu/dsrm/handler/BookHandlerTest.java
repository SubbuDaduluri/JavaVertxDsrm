package com.subbu.dsrm.handler;

import com.subbu.dsrm.model.Book;
import com.subbu.dsrm.repository.BookRepository;
import com.subbu.dsrm.service.BookService;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RequestBody;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(VertxExtension.class)
public class BookHandlerTest {


    private static final String JSON_STR = "{\n" +
        "    \"author\": \"Leo Tolstoy\",\n" +
        "    \"country\": \"Russia\",\n" +
        "    \"imageLink\": \"images/war-and-peace.jpg\",\n" +
        "    \"language\": \"Russian\",\n" +
        "    \"link\": \"https://en.wikipedia.org/wiki/War_and_Peace\",\n" +
        "    \"pages\": 1296,\n" +
        "    \"title\": \"War and Peace\",\n" +
        "    \"year\": 1867\n" +
        "}";

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookHandler bookHandler;

    @Mock
    RoutingContext routingContext;

    @Mock
    BookRepository bookRepository;

    @Mock
    HttpServerResponse httpServerResponse;

    @BeforeEach
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testInsertOne(){
        RequestBody requestBody = Mockito.mock(RequestBody.class);
        JsonObject jsonObject = new JsonObject(JSON_STR);
        Mockito.when(requestBody.asJsonObject()).thenReturn(jsonObject);
        Book book = new Book(jsonObject);
        Maybe<Book> stringMaybe = Maybe.just(book);
        Mockito.when(routingContext.body()).thenReturn(requestBody);
        Mockito.when(bookService.insert(any(Book.class))).thenReturn(stringMaybe);
        Mockito.when(routingContext.response()).thenReturn(httpServerResponse);
        Mockito.when(httpServerResponse.putHeader(anyString(),anyString())).thenReturn(httpServerResponse);
        Mockito.when(httpServerResponse.setStatusCode(anyInt())).thenReturn(httpServerResponse);
        bookHandler.insertOne(routingContext);
        Mockito.verify(bookService, times(1)).insert(any(Book.class));;

    }
}
