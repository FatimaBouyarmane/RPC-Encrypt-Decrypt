import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptionClientGUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(EncryptionClientGUI.class.getName());
    private JTextField serverIPField;
    private JTextField textField;
    private JTextField secretKeyField;
    private JTextArea resultArea;
    private EncryptionService service;

    public EncryptionClientGUI() {
        super("Encryption Client");
        setLayout(new BorderLayout());
        initUI();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set default spacing
        gbc.insets = new Insets(5, 5, 5, 5);

        // Server IP
        JLabel serverIPLabel = new JLabel("Server IP:");
        serverIPField = new JTextField("192.168.202.130", 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(serverIPLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(serverIPField, gbc);

        // Text to process
        JLabel textLabel = new JLabel("Text to process:");
        textField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(textLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(textField, gbc);

        // Secret key
        JLabel secretKeyLabel = new JLabel("Secret Key:");
        secretKeyField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(secretKeyLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(secretKeyField, gbc);

        // Buttons
        JButton encryptButton = new JButton("Encrypt");
        encryptButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon")); // example icon
        encryptButton.setToolTipText("Encrypt the text");
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performOperation("encrypt");
            }
        });

        JButton decryptButton = new JButton("Decrypt");
        decryptButton.setIcon(UIManager.getIcon("FileView.directoryIcon")); // example icon
        decryptButton.setToolTipText("Decrypt the text");
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performOperation("decrypt");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        // Result area
        resultArea = new JTextArea(5, 20);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));

        add(mainPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize RMI connection
        try {
            String serverIP = serverIPField.getText(); // Get server IP from text field
            service = (EncryptionService) Naming.lookup("//" + serverIP + "/EncryptionService");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Client exception", e);
        }
    }

    private void performOperation(String operation) {
        String text = textField.getText();
        String secretKey = secretKeyField.getText();

        try {
            String result = "";
            if (operation.equals("encrypt")) {
                result = service.encrypt(text, secretKey);
            } else if (operation.equals("decrypt")) {
                result = service.decrypt(text, secretKey);
            }

            resultArea.setText(result);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error performing operation", ex);
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EncryptionClientGUI());
    }
}
