import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
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
                    .append("\n\n");
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
    private static final String LOG_FILE_NAME = "logs.sec";

    private static byte[] encryptLogs(ArrayList<LogEntry> logs,String inputPassword) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(inputPassword.getBytes(), ENCRYPTION_ALGORITHM);
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
    private static ArrayList<LogEntry> decryptLogs(byte[] encryptedLogs,String inputPassword) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(inputPassword.getBytes(), ENCRYPTION_ALGORITHM);
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


    public static void open(Logger logger,String inputPassword) {
        ArrayList<LogEntry> loadedLogs = fileOpen(inputPassword);
        logger.addAllLogs(loadedLogs);
    }

    public static void save(ArrayList<LogEntry> logs,String inputPassword) {
        fileSave(logs,inputPassword);
        System.out.println("Logs saved successfully.");
    }

    private static void fileSave(ArrayList<LogEntry> logs,String inputPassword) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOG_FILE_NAME))) {
            byte[] encryptedLogs = encryptLogs(logs,inputPassword);
            oos.writeObject(encryptedLogs);
        } catch (IOException e) {
            System.out.println("Error saving logs to file: " + e.getMessage());
        }
    }

    private static ArrayList<LogEntry> fileOpen(String inputPassword) {
        ArrayList<LogEntry> loadedLogs = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LOG_FILE_NAME))) {
            byte[] encryptedLogs = (byte[]) ois.readObject();
            loadedLogs = decryptLogs(encryptedLogs,inputPassword);
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

        // 使用UTF-8字符编码创建Scanner
        Scanner scanner = new Scanner(System.in, "UTF-8");

        System.out.print("Enter password: (password must be 16 char)\n");
        String inputPassword = scanner.next();

        boolean run = true;

        Manager.open(logger, inputPassword);

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
                    Manager.save(logger.getLogEntries(), inputPassword);
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

