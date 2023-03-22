/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.net.bwm.monitor.pages;

import br.net.bwm.monitor.utils.ActionsUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

/**
 *
 * @author tarcisio
 */
public class NotPrivatePage extends ActionsUtil {

    private WebDriver driver;

    private static final By buttonAceptNoCertification = By.xpath("//button[@id='details-button']");
    private static final By linkProceedToPageLogin = By.xpath("//a[@id='proceed-link']");

    public NotPrivatePage(WebDriver driver) {
        this.driver = driver;
    }

    public void connectionIsNotPrivate() {
        click(driver, buttonAceptNoCertification, 10);
        click(driver, linkProceedToPageLogin, 10);
    }

}
