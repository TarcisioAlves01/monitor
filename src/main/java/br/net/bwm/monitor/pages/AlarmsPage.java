/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.net.bwm.monitor.pages;

import br.net.bwm.monitor.model.Alarm;
import br.net.bwm.monitor.utils.ActionsUtil;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author tarcisio
 */
public class AlarmsPage extends ActionsUtil {

    private WebDriver driver;

    public AlarmsPage(WebDriver driver) {
        this.driver = driver;
    }

    private List<Alarm> getAlarm() {

        WebElement table = driver.findElement(By.xpath("//tbody[@class='p-datatable-tbody']"));

        List<WebElement> lines = table.findElements(By.cssSelector("tr"));

        List<Alarm> alarms = new ArrayList<>();

        for (WebElement line : lines) {

            List<WebElement> colls = line.findElements(By.cssSelector("td"));

            if (!colls.isEmpty()) {
                String alarm = colls.get(2).getText();
                String device = colls.get(4).getText();
                String laction = colls.get(5).getText();
                String date = colls.get(1).getText();

                Alarm a = new Alarm(alarm, device, laction, date);
                alarms.add(a);

            }

        }

        return alarms;
    }

    public List<Alarm> getAll() throws Exception {
        List<Alarm> alarms = new ArrayList<>();

        List<Alarm> firstPage = getAlarm();
        alarms.addAll(firstPage);

        //Find alarm by step page in table
        List<WebElement> elements = driver.findElements(By.xpath("//button[@class='p-paginator-page p-paginator-element p-link']"));

        for (WebElement element : elements) {
            try {
                clikByElement(driver, element, 10);
                Thread.sleep(200);
                List<Alarm> a = getAlarm();
                alarms.addAll(a);
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }

        }

        return alarms;
    }

}
