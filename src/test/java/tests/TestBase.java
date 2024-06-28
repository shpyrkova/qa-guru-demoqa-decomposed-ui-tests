package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attachments;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    @BeforeAll
    static void setup() {
        Configuration.baseUrl = "https://demoqa.com";
        Configuration.pageLoadStrategy = "eager";
        RestAssured.baseURI = "https://demoqa.com";
        Configuration.browser = System.getProperty("browser", "firefox");
        Configuration.browserSize = System.getProperty("browserSize", "1280x1024");
        Configuration.browserVersion = System.getProperty("browserVersion", "123.0");
        Configuration.timeout = 10000;
        Configuration.remote = "https://"
                + System.getProperty("login")
                + ":"
                + System.getProperty("pass")
                + "@"
                + System.getProperty("host")
                + "/wd/hub";

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                "enableVNC", true,
                "enableVideo", true
        ));

        Configuration.browserCapabilities = capabilities;

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterEach
    void afterEach() {
        Attachments.screenshotAs("Last step screenshot");
        Attachments.pageSource();
        closeWebDriver();
    }
    }