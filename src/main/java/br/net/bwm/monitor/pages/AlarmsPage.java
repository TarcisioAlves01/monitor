package br.net.bwm.monitor.pages;

import br.net.bwm.monitor.model.Alarm;
import br.net.bwm.monitor.utils.ActionsUtil;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

                String date = colls.get(1).getText();
                String alarm = colls.get(2).getText();
                String criticality = colls.get(3).getText();
                String laction = colls.get(4).getText();
                String device = colls.get(5).getText();

                Alarm a = new Alarm(alarm, device, criticality, laction, date);
                alarms.add(a);

            }

        }

        return alarms;
    }

    public List<Alarm> getAll() throws Exception {
        List<Alarm> alarms = new ArrayList<>();

        List<Alarm> firstPage = getAlarm();
        alarms.addAll(firstPage);

        // Find alarm by step page in table
        List<WebElement> elements = driver
                .findElements(By.xpath("//button[@class='p-paginator-page p-paginator-element p-link']"));

        for (WebElement element : elements) {
            try {
                clikByElement(driver, element, 10);
                Thread.sleep(100);
                List<Alarm> a = getAlarm();
                alarms.addAll(a);
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }

        }

        return alarms;
    }

}
