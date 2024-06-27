package tests;

import io.restassured.response.Response;
import models.lombok.AddBookToBasketRequestBodyModel;
import models.lombok.LoginRequestBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static specs.RequestResponseSpecs.*;
import static io.qameta.allure.Allure.step;

public class BooksBasketTests extends TestBase {

    @Test
    @DisplayName("Удаление товара из корзины")
    void deleteBookFromBasketTest() {
        LoginRequestBodyModel authData = new LoginRequestBodyModel();

        Response authResponse = step("Авторизоваться через API", () ->
                given(loginRequestSpec)
                .body(authData)
                .when()
                .post("/Account/v1/Login")
                .then()
                .spec(responseSpec)
                .extract().response());

        AddBookToBasketRequestBodyModel bookData = new AddBookToBasketRequestBodyModel();
        bookData.setUserId(authResponse.path("userId"));
        AddBookToBasketRequestBodyModel.Isbn isbn = new AddBookToBasketRequestBodyModel.Isbn();
        isbn.setIsbn("9781449325862");
        bookData.setCollectionOfIsbns(List.of(isbn));

        step("Добавить товар в корзину через API", () ->
                given(authorizedRequestSpec(authResponse.path("token")))
                .body(bookData)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .spec(responseSpec));

        step("Установить авторизационные cookie в браузере", () -> {
                    open("/favicon.ico");
                    getWebDriver().manage().addCookie(new Cookie("userID", authResponse.path("userId")));
                    getWebDriver().manage().addCookie(new Cookie("expires", authResponse.path("expires")));
                    getWebDriver().manage().addCookie(new Cookie("token", authResponse.path("token")));
                });

        step("Перейти в профиль и удалить добавленный товар из корзины", () -> {
                    open("/profile");
                    $("#delete-record-undefined").click();
                    $("#closeSmallModal-ok").click();
                    switchTo().alert().accept();
                });

        step("Корзина пуста, отображается текст \"No rows found\"", () ->
        $(".profile-wrapper").shouldHave(text("No rows found")));

    }
}