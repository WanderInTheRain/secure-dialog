# LoggerApp

LoggerApp is a secure command-line application for managing logs. The application emphasizes data encryption to protect sensitive information using the AES algorithm.

## Overview

LoggerApp enables users to securely create, delete, and view log entries. The application stores log entries in an encrypted format, ensuring that sensitive data remains confidential.

## Features

- **AES Encryption:** Log entries are stored securely using the AES encryption algorithm.
- **Add Log Entries:** Create new log entries with titles and content.
- **Delete Entries:** Remove log entries by providing the title.
- **View Encrypted Logs:** View all log entries in a secure and encrypted format.
- **Save and Load Securely:** Logs are saved and loaded securely using AES encryption.

## Getting Started

1. **Clone the Repository:**
    ```bash
    git clone https://github.com/your-username/secure-dialog.git
    cd secure-dialog
    ```

2. **Run the Precompiled Application:**
    ```bash
    java LoggerApp
    ```

3. **First Time Setup:**
    - The application will check for the existence of the `logs.sec` file.
    - If the file does not exist, it will be created, and you will be prompted to set a password for future use.
    - Subsequent runs will require the password for log access.

4. **Enter Password:**
    - On the first run, set a password for future access.
    - On subsequent runs, enter the previously set password to unlock and access the encrypted logs.

5. **Commands:**
    - Use commands like `add`, `delete`, `show`, and `quit` to interact with the LoggerApp.

6. **Exit:**
    - Enter `quit` to save the logs and exit the application securely.

## Precompiled Binary

- The `LoggerApp.class` file is precompiled and ready to run.

## Dependencies

- Java Development Kit (JDK)

## Contributing

If you would like to contribute to LoggerApp, feel free to open issues or create pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
