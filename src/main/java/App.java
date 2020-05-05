import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Profile;
import org.apache.poi.util.SystemOutLogger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class App {
    private static final String APPLICATION_NAME = "Gmail Multimail";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    public static NetHttpTransport HTTP_TRANSPORT;
    public static Gmail service;
    public static Window window;
    public static File currentFile;
    public static String currentUser;
    public static Map<String,String> rows;
    public static String subject;

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList("https://mail.google.com/");
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = App.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        File newFile = new java.io.File(TOKENS_DIRECTORY_PATH);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(newFile))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("me");
    }

    public static void init() throws GeneralSecurityException, IOException {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void refresh() throws GeneralSecurityException, IOException {
        Files.delete(Paths.get("./tokens/StoredCredential"));
        init();
        Profile profile = service.users().getProfile("me").execute();
        window.setCurrentUser(profile.getEmailAddress());
    }

    public static void setCurrentFile(File pickedFile) throws IOException {
        currentFile = pickedFile;
        if(currentFile.getAbsolutePath().substring(currentFile.getAbsolutePath().length()-5).equals(".xlsx")){
            rows = ExcelReader.loadMails(currentFile.getAbsolutePath());
            window.refreshMails();
        }else{
            window.currentFile.setText("WRONG FILE FORMAT, PICK AN XLSX");
        }
    }

    public static void setCurrentUser(String s){
        App.currentUser = s;
    }

    public static void setSubject(String subject){
        App.subject = subject;
    }

    public static void main(String... args) throws IOException, GeneralSecurityException, MessagingException {
        // Build a new authorized s client service.
        window = new Window();
        init();
        Profile profile = service.users().getProfile("me").execute();
        App.setCurrentUser(profile.getEmailAddress());
        window.setCurrentUser(profile.getEmailAddress());
        window.setCurrentFile("");
    }
}