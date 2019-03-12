package updating;

import application.ModelController;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.io.Files;
import ftp.FtpController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import model.CreateModel;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class provides methods to update data and neural networks
 * @author dev.jakub.cierpial@gmail.com
 */
public class DataUpdater {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

    private List<String[]> aud = new ArrayList<>();
    private List<String[]> cad = new ArrayList<>();
    private List<String[]> chf = new ArrayList<>();
    private List<String[]> eur = new ArrayList<>();
    private List<String[]> gbp = new ArrayList<>();
    private List<String[]> jpy = new ArrayList<>();
    private List<String[]> nok = new ArrayList<>();
    private List<String[]> sek = new ArrayList<>();
    private List<String[]> usd = new ArrayList<>();


    /**
     * Reads all files which was downloaded to update data
     * Updates dates of data
     * Updates all currency data to actual
     */
    public void readAndSave() {
        File file = new File("networks/history/");
        try {

            while (Arrays.asList(file.listFiles()).stream().anyMatch(f -> f.getName().matches(".*\\.zip"))) {
                unzipFiles(Arrays.asList(file.listFiles()).stream().filter(f -> f.getName().matches(".*\\.zip")).findFirst().get());
            }

            for (File f : file.listFiles()) {
                if (Files.getFileExtension(f.getPath()).equals("txt") && f.getName().matches("[0-9]{8}\\.txt")) {
                    try {
                        CSVReader csvReader = new CSVReader(new FileReader(f));
                        List<String[]> lines = csvReader.readAll();
                        addToArray(lines);
                        updateTimes(lines.get(0)[1]);
                        csvReader.close();
                        f.delete();
                    } catch (FileNotFoundException e) {
                        LOG.error(e.getMessage());
                    } catch (IOException e) {
                        LOG.error(e.getMessage());
                    }
                }
            }
            try {
                if (aud.size() != 0) {
                    saveDataToCorrectCurrency();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        } catch (Exception e) {
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Nie znaleziono pliku history.txt ").showAndWait());
        }
    }


    /**
     * Unzip file and extract content to history folder
     * @param f file to unzip
     */
    private void unzipFiles(File f)
    {
        try {
            ZipFile zipFile = new ZipFile(f.getAbsoluteFile());
            zipFile.extractAll("networks/history/");
            f.delete();
        } catch (ZipException e) {
            LOG.error(e.getMessage());       }
    }

    /**
     * Adds currency value to correct list
     * @param lines Lines read from currency quotes, each line is quote for one currency from one day
     */
    private void addToArray(List<String[]> lines) {
        for (String[] line : lines) {

            switch (line[0]) {
                case "AUD": {
                    aud.add(new String[]{line[2]});
                    break;
                }
                case "CAD": {
                    cad.add(new String[]{line[2]});
                    break;
                }
                case "CHF": {
                    chf.add(new String[]{line[2]});
                    break;
                }
                case "EUR": {
                    eur.add(new String[]{line[2]});
                    break;
                }
                case "GBP": {
                    gbp.add(new String[]{line[2]});
                    break;
                }
                case "JPY": {
                    jpy.add(new String[]{line[2]});
                    break;
                }
                case "NOK": {
                    nok.add(new String[]{line[2]});
                    break;
                }
                case "SEK": {
                    sek.add(new String[]{line[2]});
                    break;
                }
                case "USD": {
                    usd.add(new String[]{line[2]});
                    break;
                }
            }

        }
    }

    /**
     * Adds date to historical data
     * @param time date from one day quotation
     * @throws IOException
     */
    private void updateTimes(String time) throws IOException {
        File timeFile = new File("networks/data/time/TimeSet.csv");
        FileWriter fileWriterData = new FileWriter(timeFile, true);
        CSVWriter csvWriterData = new CSVWriter(fileWriterData, ',', CSVWriter.NO_QUOTE_CHARACTER);
        csvWriterData.writeNext(new String[]{time});
        fileWriterData.close();
    }

    /**
     * Saves new currency data to correct files storing historical currency quotes
     * Updates neural networks for each currency
     * @throws IOException
     */
    private void saveDataToCorrectCurrency() throws IOException {

        for (String s : new String[]{"aud", "cad", "chf", "eur", "gbp", "jpy", "nok", "sek", "usd"}) {
            File trainFile = new File("networks/data/" + s + "/DataSetToRead.csv");
            File labelsFile = new File("networks/data/" + s + "/LabelSet.csv");
            FileWriter fileWriterData = new FileWriter(trainFile, true);
            CSVWriter csvWriterData = new CSVWriter(fileWriterData, ',', CSVWriter.NO_QUOTE_CHARACTER);
            FileWriter fileWriterLabels = new FileWriter(labelsFile, true);
            CSVWriter csvWriterLabels = new CSVWriter(fileWriterLabels, ',', CSVWriter.NO_QUOTE_CHARACTER);

            switch (s) {
                case "aud": {
                    csvWriterData.writeAll(aud.subList(0, aud.size() - 1));
                    csvWriterLabels.writeAll(aud.subList(1, aud.size()));
                    ModelController.updateNetwork(aud,"aud");
                    break;
                }
                case "cad": {
                    csvWriterData.writeAll(cad.subList(0, cad.size() - 1));
                    csvWriterLabels.writeAll(cad.subList(1, cad.size()));
                    ModelController.updateNetwork(cad,"cad");
                    break;
                }
                case "chf": {
                    csvWriterData.writeAll(chf.subList(0, chf.size() - 1));
                    csvWriterLabels.writeAll(chf.subList(1, chf.size()));
                    ModelController.updateNetwork(chf,"chf");
                    break;
                }
                case "eur": {
                    csvWriterData.writeAll(eur.subList(0, eur.size() - 1));
                    csvWriterLabels.writeAll(eur.subList(1, eur.size()));
                    ModelController.updateNetwork(eur,"eur");
                    break;
                }
                case "gbp": {
                    csvWriterData.writeAll(gbp.subList(0, gbp.size() - 1));
                    csvWriterLabels.writeAll(gbp.subList(1, gbp.size()));
                    ModelController.updateNetwork(gbp,"gbp");
                    break;
                }
                case "jpy": {
                    csvWriterData.writeAll(jpy.subList(0, jpy.size() - 1));
                    csvWriterLabels.writeAll(jpy.subList(1, jpy.size()));
                    ModelController.updateNetwork(jpy,"jpy");
                    break;
                }
                case "nok": {
                    csvWriterData.writeAll(nok.subList(0, nok.size() - 1));
                    csvWriterLabels.writeAll(nok.subList(1, nok.size()));
                    ModelController.updateNetwork(nok,"nok");
                    break;
                }
                case "sek": {
                    csvWriterData.writeAll(sek.subList(0, sek.size() - 1));
                    csvWriterLabels.writeAll(sek.subList(1, sek.size()));
                    ModelController.updateNetwork(sek,"sek");
                    break;
                }
                case "usd": {
                    csvWriterData.writeAll(usd.subList(0, usd.size() - 1));
                    csvWriterLabels.writeAll(usd.subList(1, usd.size()));
                    ModelController.updateNetwork(usd,"usd");
                    break;
                }
            }

            fileWriterData.close();
            fileWriterLabels.close();

        }


    }
}
