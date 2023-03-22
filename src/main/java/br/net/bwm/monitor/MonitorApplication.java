package br.net.bwm.monitor;

import br.net.bwm.monitor.model.Alarm;
import br.net.bwm.monitor.model.Rompimento;
import br.net.bwm.monitor.pages.AlarmsPage;
import br.net.bwm.monitor.pages.LoginPage;
import br.net.bwm.monitor.utils.ManagerDriverUtil;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MonitorApplication {

    private static final String HOST_NAME = "YOUR_HOSTNAME";

    private static WebDriver webDriver;

    private static LoginPage loginPage;
    private static AlarmsPage alarmsPage;

    private static List<Alarm> alarms = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
        run();
    }

    @GetMapping("/")
    public String index() {
        return "Bem-vindo";
    }

    @GetMapping("rompimento")
    public ResponseEntity<List<Rompimento>> getFail() {

        List<String> devices = new ArrayList<>();
        devices.add("SCMD");
        devices.add("SPVL");
        devices.add("LOA");
        devices.add("SCME");

        List<Rompimento> rompimentos = new ArrayList<>();

        for (Alarm alarm : alarms) {

            for (String device : devices) {

                int indexDevice = alarm.getDevice().toUpperCase().indexOf(device);

                String a = alarm.getAlarm().toUpperCase();
                int los = a.indexOf("LOS");
                int down = a.indexOf("DOWN");
                int port8 = a.indexOf("PORTA 8");
                int port9 = a.indexOf("PORTA 9");

                if ((los != -1) || (down != -1) || (port8 != -1) || (port9 != -1)) {

                    if (indexDevice != -1) {

                        Rompimento rompimento = new Rompimento();

                        rompimento.setLocation(alarm.getLaction());
                        rompimento.setDate(alarm.getDate());

                        if (!rompimentos.contains(rompimento)) {
                            rompimentos.add(rompimento);
                        }

                    }

                }

            }

        }

        return ResponseEntity.status(HttpStatus.OK).body(rompimentos);
    }

    @GetMapping("alarm")
    public ResponseEntity<List<Alarm>> getAllAlarms() {
        return ResponseEntity.status(HttpStatus.OK).body(alarms);
    }

    private static void run() {
        while (true) {
            try {
                webDriver = ManagerDriverUtil.browser("chrome");
                webDriver.get(HOST_NAME + "/login");                

                //Faz login na página 
                loginPage = new LoginPage(webDriver);
                loginPage.login();

                while (true) {
                    try {
                        Thread.sleep(200);
                        webDriver.get(HOST_NAME + "/alarms");
                        Thread.sleep(200);

                        //Retorna a lista de alarmes                        
                        alarmsPage = new AlarmsPage(webDriver);

                        alarms = alarmsPage.getAll();

                    } catch (Exception ex) {
                        try {
                            webDriver.navigate().refresh();
                        } catch (Exception e) {
                            webDriver.close();
                            System.out.println("Erro: " + e.getMessage());
                            break;
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
