package esprit.farouk.controllers;

import esprit.farouk.models.User;
import esprit.farouk.services.UserService;
import esprit.farouk.utils.EmailUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Random;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private VBox emailStep;
    @FXML private VBox codeStep;

    private final UserService userService = new UserService();
    private String generatedCode;
    private String targetEmail;

    @FXML
    private void handleSendCode() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showMessage("Please enter your email.", true);
            return;
        }

        try {
            User user = userService.getByEmail(email);
            if (user == null) {
                showMessage("No account found with this email.", true);
                return;
            }

            generatedCode = String.format("%06d", new Random().nextInt(999999));
            targetEmail = email;

            EmailUtils.sendResetCode(email, generatedCode);

            emailStep.setVisible(false);
            emailStep.setManaged(false);
            codeStep.setVisible(true);
            codeStep.setManaged(true);
            showMessage("A reset code has been sent to your email.", false);

        } catch (SQLException e) {
            showMessage("Database error. Please try again.", true);
            e.printStackTrace();
        } catch (Exception e) {
            showMessage("Failed to send email. Check SMTP configuration.", true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResetPassword() {
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (code.isEmpty() || newPassword.isEmpty()) {
            showMessage("Please fill in all fields.", true);
            return;
        }

        if (!code.equals(generatedCode)) {
            showMessage("Invalid reset code.", true);
            return;
        }

        if (newPassword.length() < 6) {
            showMessage("Password must be at least 6 characters.", true);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showMessage("Passwords do not match.", true);
            return;
        }

        try {
            User user = userService.getByEmail(targetEmail);
            if (user != null) {
                userService.updatePassword(user.getId(), newPassword);
                showMessage("Password reset successful! You can now login.", false);
                generatedCode = null;

                codeStep.setVisible(false);
                codeStep.setManaged(false);
            }
        } catch (SQLException e) {
            showMessage("Failed to reset password.", true);
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
        messageLabel.getStyleClass().setAll(isError ? "error-label" : "success-label");
    }
}
