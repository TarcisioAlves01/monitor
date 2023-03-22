package br.net.bwm.monitor;

import br.net.bwm.monitor.model.Alarm;
import br.net.bwm.monitor.pages.AlarmsPage;
import br.net.bwm.monitor.pages.LoginPage;
import br.net.bwm.monitor.pages.NotPrivatePage;
import br.net.bwm.monitor.utils.ManagerDriverUtil;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MonitorApplication {

    private static final String HOST_NAME = "YOUR_HOST";

    private static WebDriver webDriver;

    private static NotPrivatePage pageNotPrivate;
    private static LoginPage loginPage;
    private static AlarmsPage alarmsPage;

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
        run();
    }

    private static void run() {
        while (true) {
            try {
                webDriver = ManagerDriverUtil.browser("chrome");
                webDriver.get(HOST_NAME + "/login");

                //Acessa a página que não possui certificado válido SSL
                pageNotPrivate = new NotPrivatePage(webDriver);
                pageNotPrivate.connectionIsNotPrivate();

                //Faz login na página 
                loginPage = new LoginPage(webDriver);
                loginPage.login();

                while (true) {

                    while (true) {
                        try {
                            Thread.sleep(150);
                            webDriver.get(HOST_NAME + "/alarms");
                            Thread.sleep(150);

                            //Retorna a lista de alarmes                        
                            alarmsPage = new AlarmsPage(webDriver);
                            List<Alarm> alarms = alarmsPage.getAll();

                            System.out.println(alarms.size() + " alarmes registrados");

                        } catch (Exception e) {
                            break;
                        } finally {
                            webDriver.navigate().refresh();
                        }

                    }
                }

            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            } finally {
                webDriver.quit();
            }

        }
    }

}
