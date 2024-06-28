package tests.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.lombok.AddBookToBasketRequestBodyModel;
import models.lombok.DeleteBooksFromBasketRequestBodyModel;
import models.lombok.LoginRequestBodyModel;
import models.lombok.RegistrationRequestBodyModel;
import org.openqa.selenium.Cookie;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.RequestResponseSpecs.*;

public class BookStoreSteps {

    @Step("Зарегистрироваться через API")
    public RegistrationRequestBodyModel apiRegister() {
        RegistrationRequestBodyModel registerData = new RegistrationRequestBodyModel();

        given(registerRequestSpec)
                .body(registerData)
                .when()
                .post("/Account/v1/User")
                .then()
                .spec(responseSpec)
                .extract().response();

        return registerData;
    }

    @Step("Сгенерировать токен через API")
    public void apiGenerateToken(RegistrationRequestBodyModel registerData) {
        LoginRequestBodyModel authData = new LoginRequestBodyModel();
        authData.setUserName(registerData.getUserName());
        authData.setPassword(registerData.getPassword());

        given(loginRequestSpec)
                .body(authData)
                .when()
                .post("/Account/v1/GenerateToken")
                .then()
                .spec(responseSpec)
                .extract().response();
    }

    @Step("Авторизоваться через API")
    public Response apiAuthorization(RegistrationRequestBodyModel registerData) {
        LoginRequestBodyModel authData = new LoginRequestBodyModel();
        authData.setUserName(registerData.getUserName());
        authData.setPassword(registerData.getPassword());

        return step("Авторизоваться через API", () ->
                given(loginRequestSpec)
                        .body(authData)
                        .when()
                        .post("/Account/v1/Login")
                        .then()
                        .spec(responseSpec)
                        .extract().response());
    }

    @Step("Очистить корзину через API")
    public void clearBasketWithApi(Response authResponse) {
        DeleteBooksFromBasketRequestBodyModel userBasket = new DeleteBooksFromBasketRequestBodyModel();
        userBasket.setUserId(authResponse.path("userId"));
        given(authorizedRequestSpec(authResponse.path("token")))
                .body(userBasket)
                .when()
                .delete("/BookStore/v1/Books")
                .then()
                .spec(responseSpec);
    }

    @Step("Добавить книгу в корзину через API")
    public void addBookToBasket(Response authResponse, String isbnNum) {
        AddBookToBasketRequestBodyModel bookData = new AddBookToBasketRequestBodyModel();
        bookData.setUserId(authResponse.path("userId"));
        AddBookToBasketRequestBodyModel.Isbn isbn = new AddBookToBasketRequestBodyModel.Isbn();
        isbn.setIsbn(isbnNum);
        bookData.setCollectionOfIsbns(List.of(isbn));

        given(authorizedRequestSpec(authResponse.path("token")))
                .body(bookData)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .spec(responseSpec);
    }

    @Step("Установить авторизационные cookie в браузере")
    public void setAuthCookie(Response authResponse) {
        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID", authResponse.path("userId")));
        getWebDriver().manage().addCookie(new Cookie("expires", authResponse.path("expires")));
        getWebDriver().manage().addCookie(new Cookie("token", authResponse.path("token")));
    }

    @Step("Перейти в профиль и удалить добавленную книгу из корзины")
    public void deleteBookFromProfile() {
        open("/profile");
        $("#delete-record-undefined").click();
        $("#closeSmallModal-ok").click();
        switchTo().alert().accept();
    }

    @Step("Корзина пуста, отображается текст \"No rows found\"")
    public void checkThatBasketIsEmpty() {
        $(".profile-wrapper").shouldHave(text("No rows found"));
    }


}