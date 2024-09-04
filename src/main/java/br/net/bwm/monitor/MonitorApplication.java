package br.net.bwm.monitor;

import br.net.bwm.monitor.model.Alarm;
import br.net.bwm.monitor.model.Rompimento;
import br.net.bwm.monitor.pages.AlarmsPage;
import br.net.bwm.monitor.pages.LoginPage;
import br.net.bwm.monitor.utils.ManagerDriverUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MonitorApplication {

    private static final String HOST_NAME = "https://172.16.227.10";

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
                int payload = a.indexOf("LOS Payload");

                if (indexDevice != -1) {

                    if ((los != -1) || (down != -1) || (port8 != -1) || (port9 != -1) || (payload != -1)) {

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

    @GetMapping("alarms")
    public ResponseEntity<List<Alarm>> getAllAlarms() {
        return ResponseEntity.status(HttpStatus.OK).body(alarms);
    }

    private static void run() {
        while (true) {
            try {
                setupWebDriver();
                performLogin();

                // Monitoramento contínuo dos alarmes
                while (true) {
                    try {
                        fetchAndProcessAlarms();
                        Thread.sleep(5000); // Intervalo entre atualizações
                    } catch (NoSuchSessionException e) {
                        handleException(e);
                        recreateWebDriver(); // Recria o WebDriver e tenta novamente
                        performLogin(); // Refaça o login após recriar o WebDriver
                    } catch (Exception e) {
                        handleException(e);
                        recreateWebDriver(); // Recria o WebDriver em caso de erro
                        performLogin(); // Refaça o login após recriar o WebDriver
                    }
                }
            } catch (Exception e) {
                handleException(e);
                // Não há necessidade de chamar recreateWebDriver() aqui,
                // pois o WebDriver já está sendo recriado na seção interna
            } finally {
                // Garantir que o WebDriver seja encerrado no final do ciclo, se necessário
                if (webDriver != null) {
                    webDriver.quit(); // Fecha a sessão do WebDriver
                }
            }
        }
    }

    private static void setupWebDriver() {
        try {
            if (webDriver != null) {
                webDriver.quit(); // Certifique-se de encerrar uma sessão existente
            }
            webDriver = ManagerDriverUtil.browser();
            if (webDriver == null) {
                throw new RuntimeException("Falha ao inicializar o WebDriver.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar o WebDriver.", e);
        }
    }

    private static void performLogin() {
        try {
            webDriver.get(HOST_NAME + "/login");

            WebDriverWait waitLogin = new WebDriverWait(webDriver, Duration.ofMinutes(1));
            By inputUserName = By.xpath("//input[@id='username']");
            waitLogin.until(ExpectedConditions.visibilityOfElementLocated(inputUserName));

            loginPage = new LoginPage(webDriver);
            loginPage.login();

        } catch (Exception e) {
            handleException(e);
            throw new RuntimeException("Erro durante o login.", e);
        }
    }

    private static void fetchAndProcessAlarms() throws Exception {
        
        webDriver.get(HOST_NAME + "/alarms");

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        By datatable = By.xpath("//tbody[@class='p-datatable-tbody']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(datatable));

        alarmsPage = new AlarmsPage(webDriver);
        alarms = alarmsPage.getAll();
    }

    private static void recreateWebDriver() {
        try {
            if (webDriver != null) {
                webDriver.quit(); // Encerra o WebDriver atual
            }
            setupWebDriver(); // Recria uma nova instância do WebDriver
        } catch (Exception e) {
            handleException(e);
            // Pode ser necessário adicionar um loop de espera ou uma pausa aqui
            // para evitar a tentativa de recriação contínua em caso de falha repetida
        }
    }

    private static void handleException(Exception ex) {
        System.err.println("Ocorreu um erro: " + ex.getMessage());
        ex.printStackTrace();
        // Considerar uma pausa ou estratégia de retry para prevenir loops rápidos
        // contínuos
    }

}
