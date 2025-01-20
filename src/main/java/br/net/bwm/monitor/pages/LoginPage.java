package br.net.bwm.monitor.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import br.net.bwm.monitor.utils.ActionsUtil;

/**
 *
 * @author tarcisio
 */
public class LoginPage extends ActionsUtil {

    private static final String LOGIN = "YOUR_LOGIN";
    private static final String PASSWORD = "YOUR_PASSWORD";

    private WebDriver driver;

    private static final By inputUserName = By.xpath("//input[@id='username']");
    private static final By inputPassword = By.xpath("//input[@id='password']");
    private static final By buttonAction = By.xpath("//button[@id='login']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login() {
        sendData(driver, inputUserName, LOGIN, 10);
        sendData(driver, inputPassword, PASSWORD, 10);
        click(driver, buttonAction, 10);
    }

}
