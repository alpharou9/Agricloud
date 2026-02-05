package esprit.farouk.controllers;

import esprit.farouk.models.Role;
import esprit.farouk.models.User;
import esprit.farouk.services.RoleService;
import esprit.farouk.services.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import esprit.farouk.utils.ValidationUtils;

import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private Label messageLabel;

    private final UserService userService = new UserService();
    private final RoleService roleService = new RoleService();

    @FXML
    private void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList("Farmer", "Customer"));
        roleCombo.setValue("Farmer");
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMessage("Name, email and password are required.", true);
            return;
        }

        if (!ValidationUtils.isValidName(name)) {
            showMessage("Name must be at least 2 characters and contain only letters.", true);
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showMessage("Please enter a valid email address.", true);
            return;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            showMessage("Phone must be 8-15 digits, optionally starting with +.", true);
            return;
        }

        if (password.length() < 6) {
            showMessage("Password must be at least 6 characters.", true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.", true);
            return;
        }

        try {
            User existing = userService.getByEmail(email);
            if (existing != null) {
                showMessage("An account with this email already exists.", true);
                return;
            }

            String selectedRole = roleCombo.getValue().toLowerCase();
            Role role = roleService.getByName(selectedRole);
            if (role == null) {
                showMessage("Role \"" + selectedRole + "\" not found. Please contact admin.", true);
                return;
            }

            User user = new User(role.getId(), name, email, password, phone);
            userService.add(user);
            showMessage("Account created! You can now login.", false);
        } catch (SQLException e) {
            showMessage("Database error. Please try again.", true);
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
        if (isError) {
            messageLabel.getStyleClass().setAll("error-label");
        } else {
            messageLabel.getStyleClass().setAll("success-label");
        }
    }
}
