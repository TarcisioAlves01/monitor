package br.net.bwm.monitor.pages;

import br.net.bwm.monitor.model.Alarm;
import br.net.bwm.monitor.utils.ActionsUtil;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.nio.file.DirectoryStream;

import java.time.Duration;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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

    public List<Alarm> getAll() throws Exception {
        List<Alarm> alarms = new ArrayList<>();

        // Deleta os relátórios antigos em disco.
        delete();

        By download = By.id("export-excel-button");

        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(download));
            click(driver, download, 10);
        } catch (Exception e) {

            return alarms; // Retorna uma lista vazia em caso de falha
        }

        Path file = Path.of(path.toString(), "Alarms History.xlsx");

        // Aguarde até que o arquivo exista
        if (!waitForFileToDownload(file, 150000)) {
            return alarms; // Retorna uma lista vazia
        }

        // Verifique se o arquivo foi baixado e não está vazio
        if (Files.exists(file) && Files.size(file) > 0) {

            try (Workbook workbook = new XSSFWorkbook(new FileInputStream(file.toString()))) {

                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0 || row.getRowNum() == 1)
                        continue; // Ignora cabeçalho

                    if (!isValidRow(row)) {
                        continue;
                    }

                    String criticality = row.getCell(0).getStringCellValue();
                    String alarm = row.getCell(1).getStringCellValue();
                    String location = row.getCell(4).getStringCellValue();
                    String device = row.getCell(5).getStringCellValue();

                    // Adicione uma verificação se as informações necessárias estão disponíveis
                    Alarm item = new Alarm(alarm, device, criticality, location);
                    alarms.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace(); // Adiciona stack trace para ajudar na depuração
            }
        } else {
            System.out.println("Arquivo não encontrado ou está vazio.");
        }

        return alarms;
    }

    private boolean isValidRow(Row row) {
        // Verifica se as células são válidas antes de chamar getStringCellValue
        return isCellValid(row.getCell(0)) &&
                isCellValid(row.getCell(1)) &&
                isCellValid(row.getCell(4)) &&
                isCellValid(row.getCell(5));
    }

    private boolean isCellValid(Cell cell) {
        return cell != null &&
                cell.getCellType() == CellType.STRING &&
                cell.getStringCellValue() != null &&
                !cell.getStringCellValue().isEmpty();
    }

    private boolean waitForFileToDownload(Path file, int timeout) throws InterruptedException {
        int waitedTime = 0;
        while (waitedTime < timeout) {
            if (Files.exists(file)) {
                return true; // O arquivo foi baixado
            }
            Thread.sleep(1000); // Espera 1 segundo antes de verificar novamente
            waitedTime += 1000;
        }
        return false; // Tempo limite excedido
    }

    private void delete() {

        if (Files.exists(path) && Files.isDirectory(path)) {

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {

                for (Path entry : stream) {

                    if (Files.isRegularFile(entry)) {
                        Files.delete(entry);
                    }
                }

            } catch (IOException e) {

                e.printStackTrace();

            }
        } else {
            System.out.println("Provided path is not a valid directory.");
        }

    }

}
