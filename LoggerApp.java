import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

class LogEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date timestamp;
    private String content;
    private String title;

    public LogEntry(String title, String content) {
        this.timestamp = new Date();
        this.title = title;
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }
}

class Logger {
    private ArrayList<LogEntry> logEntries;

    public Logger() {
        this.logEntries = new ArrayList<>();
    }

    public void addLogEntry(String title, String content) {
        LogEntry entry = new LogEntry(title, content);
        if (title != null) {
            logEntries.add(entry);
            System.out.println("Added successfully.");
        } else {
            System.out.println("Empty Log.");
        }
    }

    public void deleteLogEntry(String title) {
        logEntries.removeIf(entry -> entry.getTitle().equals(title));
        System.out.println("Deleted successfully.");
    }

    public String getAllLogs() {
        StringBuilder logs = new StringBuilder();
        for (LogEntry entry : logEntries) {
            logs.append("Title: ").append(entry.getTitle())
                    .append(", \nContent: ").append(entry.getContent())
                    .append(", \nTimestamp: ").append(entry.getTimestamp())
                    .append("\n");
        }
        return logs.toString();
    }

    public ArrayList<LogEntry> getLogEntries() {
        return logEntries;
    }

    public void addAllLogs(ArrayList<LogEntry> entries) {
        logEntries.addAll(entries);
    }
}

class Manager {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY = "your_secret_key1"; // Replace with a strong password
    private static final String LOG_FILE_NAME = "logs.sec";

    private static byte[] encryptLogs(ArrayList<LogEntry> logs) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(logs);
            objectOutputStream.close();

            return cipher.doFinal(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Decrypts a byte array into a list of log entries
    @SuppressWarnings("unchecked")
    private static ArrayList<LogEntry> decryptLogs(byte[] encryptedLogs) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(encryptedLogs);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decryptedBytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            return (ArrayList<LogEntry>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean authenticate(Scanner scanner) {
        boolean run = false;
        System.out.print("Enter password: ");
        String inputPassword = scanner.next();
        if (inputPassword.equals(SECRET_KEY)) {
            run = true;
        } else {
            System.out.println("Wrong password. Exiting.");
        }
        return run;
    }

    public static void open(Logger logger) {
        ArrayList<LogEntry> loadedLogs = fileOpen();
        logger.addAllLogs(loadedLogs);
    }

    public static void save(ArrayList<LogEntry> logs) {
        fileSave(logs);
        System.out.println("Logs saved successfully.");
    }

    private static void fileSave(ArrayList<LogEntry> logs) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOG_FILE_NAME))) {
            byte[] encryptedLogs = encryptLogs(logs);
            oos.writeObject(encryptedLogs);
        } catch (IOException e) {
            System.out.println("Error saving logs to file: " + e.getMessage());
        }
    }

    private static ArrayList<LogEntry> fileOpen() {
        ArrayList<LogEntry> loadedLogs = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LOG_FILE_NAME))) {
            byte[] encryptedLogs = (byte[]) ois.readObject();
            loadedLogs = decryptLogs(encryptedLogs);
        } catch (FileNotFoundException e) {
            System.out.println("Log file not found. Creating a new one.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading logs from file: " + e.getMessage());
        }
        return loadedLogs;
    }
}

public class LoggerApp {
    public static void main(String[] args) {
        Logger logger = new Logger();
        Scanner scanner = new Scanner(System.in);

        boolean run = Manager.authenticate(scanner);

        if (run) {
            Manager.open(logger);
        }

        String cmd;

        while (run) {
            System.out.print("> ");
            cmd = scanner.next();

            switch (cmd) {
                case "add":
                    System.out.print("Title: ");
                    String title = scanner.next();
                    System.out.print("Content: ");
                    String content = scanner.next();
                    logger.addLogEntry(title, content);
                    break;

                case "delete":
                    System.out.print("Enter log title to delete: ");
                    String deleteTitle = scanner.next();
                    logger.deleteLogEntry(deleteTitle);
                    break;

                case "show":
                    String logs = logger.getAllLogs();
                    System.out.println(logs);
                    break;

                case "quit":
                    run = false;
                    Manager.save(logger.getLogEntries());
                    break;

                default:
                    System.out.println("Invalid command. Try again.");
                    break;
            }
        }

        scanner.close();
        System.out.println("Exiting LoggerApp. Goodbye!");
    }
}
