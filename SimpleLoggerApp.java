import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

class LogEntry {
    private Date timestamp;
    private String content;

    public LogEntry(String content) {
        this.timestamp = new Date();
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }
}

class Logger {
    private ArrayList<LogEntry> logEntries;

    public Logger() {
        this.logEntries = new ArrayList<>();
    }

    public void addLogEntry(String content) {
        LogEntry entry = new LogEntry(content);
        logEntries.add(entry);
        System.out.println("Log entry added successfully.");
    }

    public void displayLogs() {
        System.out.println("----- Log Entries -----");
        for (LogEntry entry : logEntries) {
            System.out.println(entry.getTimestamp() + ": " + entry.getContent());
        }
        System.out.println("-----------------------");
    }
}

public class SimpleLoggerApp {
    public static void main(String[] args) {
        Logger logger = new Logger();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("1. Add Log Entry");
            System.out.println("2. Display Logs");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter log content: ");
                    String logContent = scanner.nextLine();
                    logger.addLogEntry(logContent);
                    break;
                case 2:
                    logger.displayLogs();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    exit = true;  // 设置退出条件
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }
}
