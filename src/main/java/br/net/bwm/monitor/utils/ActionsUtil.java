/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.net.bwm.monitor.utils;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author tarcisio
 */
public class ActionsUtil {

    public static void click(WebDriver driver, By by, int sleep) {
        WebElement element = (new WebDriverWait(driver, Duration.ofSeconds(sleep)).until(ExpectedConditions.elementToBeClickable(by)));
        element.click();
    }

    public static void clikByElement(WebDriver driver, WebElement element, int sleep) {
        WebElement e = (new WebDriverWait(driver, Duration.ofSeconds(sleep)).until(ExpectedConditions.elementToBeClickable(element)));
        e.click();
    }

    public WebElement sendData(WebDriver driver, By by, String value, int sleep) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(sleep));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
        WebElement element = driver.findElement(by);

        element.sendKeys(value);

        return element;
    }
}
