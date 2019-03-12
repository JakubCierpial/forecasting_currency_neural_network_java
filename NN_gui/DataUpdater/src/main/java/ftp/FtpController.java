package ftp;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class provides methods to checking topicality of data and downloading them
 * @author dev.jakub.cierpial@gmail.com
 */
public class FtpController {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

    private final static String PATH = "pub/waluty/omega/nbp/";

    private FTPClient ftp;
    
    private List<String> historicalDataList = new ArrayList<>();
    
    public FtpController()
    {
        getHistoricalDataList();
    }

    /**
     * Gets list of files which was already downloaded
     */
    private void getHistoricalDataList()
    {

        FileReader alredyDownloaded = null;
        try {
            alredyDownloaded = new FileReader("networks/history/history.txt");

            BufferedReader bufferedReader = new BufferedReader(alredyDownloaded);
            String s = null;
            while((s=bufferedReader.readLine())!=null) {
                historicalDataList.add(s);
            }
            bufferedReader.close();
            alredyDownloaded.close();
        } catch (FileNotFoundException e) {
            Platform.runLater(() ->{new Alert(Alert.AlertType.ERROR,"Nie znaleziono pliku history.txt ").showAndWait();Platform.exit();});

        } catch (IOException e) {
            LOG.error(e.getMessage());   }


    }

    /**
     * Saves list of files  which was already downloaded
     */
    private void saveHistoricalDataList()
    {
        FileWriter alredyDownloaded = null;
        try {
            alredyDownloaded = new FileWriter("networks/history/history.txt");

            PrintWriter printWriter = new PrintWriter(new BufferedWriter(alredyDownloaded));
            for (String s : historicalDataList) {
                printWriter.println(s);
            }
            printWriter.close();
            alredyDownloaded.close();
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());        }

    }

    /**
     * Disconnects with ftp server
     */
    public void disconnect()
    {
        if(ftp.isConnected())
        {
            try {
                ftp.disconnect();
            } catch (IOException e) {
                LOG.error(e.getMessage());         }
        }
    }

    /**
     * Connects to bosss.pl ftp server
     */
    private void connectToServer()
    {
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        try {
            ftp.connect("bossa.pl",21);


        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
        }
        ftp.login("anonymous", "password");
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();


        } catch (IOException e) {
            Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,"Nie można połączyć z bossa.pl, urachamianie programu z danymi lokalnymi.").showAndWait());
            LOG.error(e.getMessage());
        }
    }

    /**
     * Lists all files which was not downloaded
     * @return Names of files
     */
    public List<String> listAllFiles()
    {
        FTPFile[] files = new FTPFile[0];
        ArrayList<String> names = new ArrayList<>();
        try {
            files = ftp.listFiles(PATH);
        } catch (IOException e) {
            LOG.error(e.getMessage());       }
        for (FTPFile file : files) {
            String details = file.getName();
            names.add(details);
        }
        return names;
    }

    /**
     * Downloads file with provided name from server
     * @param file name of file plan to download
     */
    private void dowloadFile(String file)
    {
        File targetFile = new File("networks/history/"+file);
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            this.ftp.retrieveFile(PATH+file, fos);
        } catch (IOException e) {
            LOG.error(e.getMessage());      }
    }

    /**
     * Downloads all currency data which was not downloaded
     * Update list of files which was downloaded
     */
    public void downloadNew()
    {
        connectToServer();
        ArrayList<String> toDownload = new ArrayList<>();
        List<String> ftpFiles = listAllFiles();
        for (String s: ftpFiles) {
            {
                if(!historicalDataList.contains(s))
                {
                    toDownload.add(s);
                }
            }

        }
        for (String s: toDownload) {
            dowloadFile(s);
        }
        historicalDataList.addAll(toDownload);
        saveHistoricalDataList();

    }

}

