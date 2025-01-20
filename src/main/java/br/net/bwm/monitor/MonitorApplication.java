package br.net.bwm.monitor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.net.bwm.monitor.model.Alarm;
import br.net.bwm.monitor.pages.AlarmsPage;
import br.net.bwm.monitor.pages.LoginPage;
import br.net.bwm.monitor.utils.ManagerDriverUtil;

@SpringBootApplication
@RestController
public class MonitorApplication {

    private static final String HOST_NAME = "YOUR_HOST";

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
    public ResponseEntity<List<String>> getFail() {

       
        List<String> rompimentos = new ArrayList<>();

        for (Alarm alarm : alarms) {

            int scme = alarm.getDevice().toUpperCase().indexOf("SCME");
            int spvl = alarm.getDevice().toUpperCase().indexOf("SPVL");
            //int scmd = alarm.getDevice().toUpperCase().indexOf("SCMD");

            if (scme != -1 || spvl != -1) {

                String a = alarm.getAlarm().toUpperCase();

                int los = a.indexOf("LOS");
                int down = a.indexOf("DOWN");
                int port = a.indexOf("PORTA");
                int sfp = a.indexOf("SFP");

                if ((los != -1) || (down != -1) || (port != -1) || (sfp != -1)) {

                    if (!rompimentos.contains(alarm.getLaction())) {
                        rompimentos.add(alarm.getLaction());
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
            } finally {

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
            // Acessa a página de login
            webDriver.get(HOST_NAME + "/login");

            // Aguarda até que a URL contenha a parte esperada
            WebDriverWait waitLogin = new WebDriverWait(webDriver, Duration.ofMinutes(2)); // Ajuste conforme
                                                                                           // necessário
            waitLogin.until(ExpectedConditions.urlContains("/login"));

            // Localiza o campo de usuário
            By inputUserName = By.xpath("//input[@id='username']");
            waitLogin.until(ExpectedConditions.visibilityOfElementLocated(inputUserName));

            // Inicializa a página de login e realiza o login
            loginPage = new LoginPage(webDriver);
            loginPage.login();

        } catch (TimeoutException e) {
            System.err.println("Timeout ao tentar acessar o campo de username: " + e.getMessage());
            throw new RuntimeException("Erro durante o login: campo de username não encontrado.", e);
        } catch (Exception e) {
            handleException(e);
            throw new RuntimeException("Erro durante o login.", e);
        }
    }

    private static void fetchAndProcessAlarms() throws Exception {

        webDriver.get(HOST_NAME + "/alarms");

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        By datatable = By.xpath("//table[@class='p-datatable-table']");

        wait.until(ExpectedConditions.visibilityOfElementLocated(datatable));

        alarmsPage = new AlarmsPage(webDriver);

        List<Alarm> list = alarmsPage.getAll();

        if (!list.isEmpty()) {
            alarms = list;
        }

    }

    private static void recreateWebDriver() {
        try {
            if (webDriver != null) {
                webDriver.quit(); // Encerra o WebDriver atual
            }
            setupWebDriver(); // Recria uma nova instância do WebDriver
        } catch (Exception e) {
            handleException(e);
        }
    }

    private static void handleException(Exception ex) {
        System.err.println("Ocorreu um erro: " + ex.getMessage());
        ex.printStackTrace();
        // Considerar uma pausa ou estratégia de retry para prevenir loops rápidos
        // contínuos
    }

}
