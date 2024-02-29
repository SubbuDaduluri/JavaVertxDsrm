package com.subbu.dsrm.handler;

import com.subbu.dsrm.model.Book;
import com.subbu.dsrm.repository.BookRepository;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.RequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(VertxExtension.class)
public class BookRepositoryTest {

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

    @InjectMocks
    BookRepository bookRepository;

    @Mock
    MongoClient mongoClient;

    @BeforeEach
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testInsertOne(){
        RequestBody requestBody = Mockito.mock(RequestBody.class);
        JsonObject jsonObject = new JsonObject(JSON_STR);
        Book book = new Book(jsonObject);
        Maybe<String> stringMaybe = Maybe.just("65df2b5cc3d1815917615443");
        when(mongoClient.rxInsert(anyString(),any(JsonObject.class))).thenReturn(stringMaybe);
        Maybe<Book> result = bookRepository.insert(book);
        Assertions.assertEquals(stringMaybe,stringMaybe);
        Assertions.assertNotNull(result);

    }
}
