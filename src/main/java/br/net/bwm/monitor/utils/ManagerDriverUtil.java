/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.net.bwm.monitor.utils;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 *
 * @author tarcisio
 */
public class ManagerDriverUtil extends ActionsUtil {

    // private static final String URL_DRIVER = "http://localhost:4444/wd/hub";
    private static final String URL_DRIVER = "http://selenium:4444/wd/hub";

    private static WebDriver driver;

    public static WebDriver browser() throws Exception {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.setAcceptInsecureCerts(true);
        options.addArguments("download.default_directory=" + path.toString());

        driver = new RemoteWebDriver(new URL(URL_DRIVER), options);

        driver.manage().window().maximize();

        return driver;
    }

    public static void closeBrowser() {
        if (driver != null) {
            driver.quit();
            System.out.println("Driver fechado com sucesso.");
        }
    }

}