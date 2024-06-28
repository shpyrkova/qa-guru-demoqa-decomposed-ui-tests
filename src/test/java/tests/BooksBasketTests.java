package tests;

import io.restassured.response.Response;
import models.lombok.RegistrationRequestBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.steps.BookStoreSteps;


public class BooksBasketTests extends TestBase {

    @Test
    @DisplayName("Удаление книги из корзины с аннотацией Step")
    void deleteBookFromBasketWithStepsAnnotationTest() {
        BookStoreSteps steps = new BookStoreSteps();
        String isbnNum = "9781449325862";

        RegistrationRequestBodyModel registerResponse = steps.apiRegister();
        steps.apiGenerateToken(registerResponse);
        Response authResponse = steps.apiAuthorization(registerResponse);
        steps.clearBasketWithApi(authResponse);
        steps.addBookToBasket(authResponse, isbnNum);
        steps.setAuthCookie(authResponse);
        steps.deleteBookFromProfile();
        steps.checkThatBasketIsEmpty();
    }


}