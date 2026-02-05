package esprit.farouk.controllers;

import esprit.farouk.models.Farm;
import esprit.farouk.models.Field;
import esprit.farouk.models.Role;
import esprit.farouk.models.User;
import esprit.farouk.services.FarmService;
import esprit.farouk.services.FieldService;
import esprit.farouk.services.RoleService;
import esprit.farouk.services.UserService;
import esprit.farouk.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DashboardController {

    @FXML
    private Label userNameLabel;

    @FXML
    private VBox sidebarMenu;

    @FXML
    private StackPane contentArea;

    private final UserService userService = new UserService();
    private final RoleService roleService = new RoleService();
    private final FarmService farmService = new FarmService();
    private final FieldService fieldService = new FieldService();

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        userNameLabel.setText("Welcome, " + user.getName());

        sidebarMenu.getChildren().clear();

        String roleName = "";
        try {
            Role role = roleService.getById(user.getRoleId());
            if (role != null) {
                roleName = role.getName();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if ("admin".equalsIgnoreCase(roleName)) {
            Button usersBtn = new Button("Users");
            usersBtn.getStyleClass().add("sidebar-button");
            usersBtn.setMaxWidth(Double.MAX_VALUE);
            usersBtn.setOnAction(e -> showUsersView());

            Button rolesBtn = new Button("Roles");
            rolesBtn.getStyleClass().add("sidebar-button");
            rolesBtn.setMaxWidth(Double.MAX_VALUE);
            rolesBtn.setOnAction(e -> showRolesView());

            Button farmsBtn = new Button("Farms");
            farmsBtn.getStyleClass().add("sidebar-button");
            farmsBtn.setMaxWidth(Double.MAX_VALUE);
            farmsBtn.setOnAction(e -> showFarmsView());

            Button statsBtn = new Button("Statistics");
            statsBtn.getStyleClass().add("sidebar-button");
            statsBtn.setMaxWidth(Double.MAX_VALUE);
            statsBtn.setOnAction(e -> showStatisticsView());

            sidebarMenu.getChildren().addAll(usersBtn, rolesBtn, farmsBtn, statsBtn);
            showUsersView();
        } else if ("farmer".equalsIgnoreCase(roleName)) {
            Button profileBtn = new Button("Profile");
            profileBtn.getStyleClass().add("sidebar-button");
            profileBtn.setMaxWidth(Double.MAX_VALUE);
            profileBtn.setOnAction(e -> showProfileView());

            Button myFarmsBtn = new Button("My Farms");
            myFarmsBtn.getStyleClass().add("sidebar-button");
            myFarmsBtn.setMaxWidth(Double.MAX_VALUE);
            myFarmsBtn.setOnAction(e -> showMyFarmsView());

            sidebarMenu.getChildren().addAll(profileBtn, myFarmsBtn);
            showProfileView();
        } else {
            // Customer or other roles
            Button profileBtn = new Button("Profile");
            profileBtn.getStyleClass().add("sidebar-button");
            profileBtn.setMaxWidth(Double.MAX_VALUE);
            profileBtn.setOnAction(e -> showProfileView());

            sidebarMenu.getChildren().add(profileBtn);
            showProfileView();
        }
    }

    @FXML
    private void showUsersView() {
        contentArea.getChildren().clear();

        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_LEFT);

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("User Management");
        title.getStyleClass().add("content-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("action-button-add");
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("action-button-edit");
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("action-button-delete");
        Button blockBtn = new Button("Block/Unblock");
        blockBtn.getStyleClass().add("action-button-block");

        header.getChildren().addAll(title, spacer, addBtn, editBtn, deleteBtn, blockBtn);

        // Search & Filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or email...");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ComboBox<String> statusFilter = new ComboBox<>(FXCollections.observableArrayList("All", "Active", "Inactive", "Blocked"));
        statusFilter.setValue("All");
        statusFilter.getStyleClass().add("filter-combo");

        filterBar.getChildren().addAll(searchField, statusFilter);

        // Table
        TableView<User> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<User, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(60);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<User, Long> roleCol = new TableColumn<>("Role ID");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        roleCol.setMaxWidth(80);

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, statusCol, roleCol);

        // Load data with FilteredList + SortedList
        ObservableList<User> masterData = FXCollections.observableArrayList();
        try {
            masterData.addAll(userService.getAll());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
        }

        FilteredList<User> filteredData = new FilteredList<>(masterData, p -> true);

        // Update predicate when search text or status filter changes
        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                filteredData.setPredicate(user -> filterUser(user, newVal, statusFilter.getValue())));
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                filteredData.setPredicate(user -> filterUser(user, searchField.getText(), newVal)));

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        // Reload helper
        Runnable reloadTable = () -> {
            masterData.clear();
            try {
                masterData.addAll(userService.getAll());
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + ex.getMessage());
            }
        };

        // Button actions
        addBtn.setOnAction(e -> {
            showUserFormDialog(null);
            reloadTable.run();
        });

        editBtn.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to edit.");
                return;
            }
            showUserFormDialog(selected);
            reloadTable.run();
        });

        deleteBtn.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to delete.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user \"" + selected.getName() + "\"?");
            confirm.setHeaderText("Confirm Deletion");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    userService.delete(selected.getId());
                    reloadTable.run();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user: " + ex.getMessage());
                }
            }
        });

        blockBtn.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to block/unblock.");
                return;
            }
            if (currentUser != null && selected.getId() == currentUser.getId()) {
                showAlert(Alert.AlertType.WARNING, "Action Denied", "You cannot block your own account.");
                return;
            }
            String newStatus = "blocked".equals(selected.getStatus()) ? "active" : "blocked";
            String action = "blocked".equals(newStatus) ? "block" : "unblock";
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to " + action + " \"" + selected.getName() + "\"?");
            confirm.setHeaderText("Confirm " + action.substring(0, 1).toUpperCase() + action.substring(1));
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    selected.setStatus(newStatus);
                    userService.update(selected);
                    reloadTable.run();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to " + action + " user: " + ex.getMessage());
                }
            }
        });

        container.getChildren().addAll(header, filterBar, table);
        contentArea.getChildren().add(container);
    }

    private boolean filterUser(User user, String searchText, String statusValue) {
        boolean matchesSearch = true;
        if (searchText != null && !searchText.trim().isEmpty()) {
            String lower = searchText.trim().toLowerCase();
            matchesSearch = (user.getName() != null && user.getName().toLowerCase().contains(lower))
                    || (user.getEmail() != null && user.getEmail().toLowerCase().contains(lower));
        }
        boolean matchesStatus = true;
        if (statusValue != null && !"All".equals(statusValue)) {
            matchesStatus = statusValue.toLowerCase().equals(user.getStatus());
        }
        return matchesSearch && matchesStatus;
    }

    @FXML
    private void showRolesView() {
        contentArea.getChildren().clear();

        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_LEFT);

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Role Management");
        title.getStyleClass().add("content-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("action-button-add");
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("action-button-edit");
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("action-button-delete");

        header.getChildren().addAll(title, spacer, addBtn, editBtn, deleteBtn);

        // Table
        TableView<Role> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Role, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(60);

        TableColumn<Role, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Role, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(idCol, nameCol, descCol);

        loadRoles(table);

        // Button actions
        addBtn.setOnAction(e -> {
            showRoleFormDialog(null);
            loadRoles(table);
        });

        editBtn.setOnAction(e -> {
            Role selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a role to edit.");
                return;
            }
            showRoleFormDialog(selected);
            loadRoles(table);
        });

        deleteBtn.setOnAction(e -> {
            Role selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a role to delete.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete role \"" + selected.getName() + "\"?");
            confirm.setHeaderText("Confirm Deletion");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    roleService.delete(selected.getId());
                    loadRoles(table);
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete role: " + ex.getMessage());
                }
            }
        });

        container.getChildren().addAll(header, table);
        contentArea.getChildren().add(container);
    }

    private void showUserFormDialog(User user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Add User" : "Edit User");
        dialog.setHeaderText(user == null ? "Create a new user" : "Edit user: " + user.getName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(user != null ? user.getName() : "");
        nameField.setPromptText("Name");
        TextField emailField = new TextField(user != null ? user.getEmail() : "");
        emailField.setPromptText("Email");
        TextField phoneField = new TextField(user != null ? user.getPhone() : "");
        phoneField.setPromptText("Phone");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("active", "inactive", "blocked"));
        statusCombo.setValue(user != null ? user.getStatus() : "active");

        ComboBox<Role> roleCombo = new ComboBox<>();
        try {
            List<Role> roles = roleService.getAll();
            roleCombo.setItems(FXCollections.observableArrayList(roles));
            roleCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Role item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            roleCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Role item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            if (user != null) {
                for (Role r : roles) {
                    if (r.getId() == user.getRoleId()) {
                        roleCombo.setValue(r);
                        break;
                    }
                }
            } else if (!roles.isEmpty()) {
                roleCombo.setValue(roles.get(0));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load roles: " + e.getMessage());
        }

        int row = 0;
        grid.add(new Label("Name:"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        grid.add(new Label("Phone:"), 0, row);
        grid.add(phoneField, 1, row++);
        if (user == null) {
            grid.add(new Label("Password:"), 0, row);
            grid.add(passwordField, 1, row++);
        }
        grid.add(new Label("Status:"), 0, row);
        grid.add(statusCombo, 1, row++);
        grid.add(new Label("Role:"), 0, row);
        grid.add(roleCombo, 1, row);

        dialog.getDialogPane().setContent(grid);

        // Validation loop: re-show dialog if validation fails
        while (true) {
            Optional<ButtonType> result = dialog.showAndWait();
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                break;
            }

            String nameVal = nameField.getText().trim();
            String emailVal = emailField.getText().trim();
            String phoneVal = phoneField.getText().trim();

            if (!ValidationUtils.isValidName(nameVal)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Name must be at least 2 characters and contain only letters.");
                continue;
            }
            if (!ValidationUtils.isValidEmail(emailVal)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid email address.");
                continue;
            }
            if (!ValidationUtils.isValidPhone(phoneVal)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Phone must be 8-15 digits, optionally starting with +.");
                continue;
            }
            if (user == null && passwordField.getText().length() < 6) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 6 characters.");
                continue;
            }

            try {
                if (user == null) {
                    User newUser = new User();
                    newUser.setName(nameVal);
                    newUser.setEmail(emailVal);
                    newUser.setPhone(phoneVal);
                    newUser.setPassword(passwordField.getText());
                    newUser.setStatus(statusCombo.getValue());
                    newUser.setRoleId(roleCombo.getValue() != null ? roleCombo.getValue().getId() : 1);
                    userService.add(newUser);
                } else {
                    user.setName(nameVal);
                    user.setEmail(emailVal);
                    user.setPhone(phoneVal);
                    user.setStatus(statusCombo.getValue());
                    user.setRoleId(roleCombo.getValue() != null ? roleCombo.getValue().getId() : user.getRoleId());
                    userService.update(user);
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save user: " + e.getMessage());
            }
            break;
        }
    }

    private void showRoleFormDialog(Role role) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(role == null ? "Add Role" : "Edit Role");
        dialog.setHeaderText(role == null ? "Create a new role" : "Edit role: " + role.getName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(role != null ? role.getName() : "");
        nameField.setPromptText("Role name");
        TextField descField = new TextField(role != null ? role.getDescription() : "");
        descField.setPromptText("Description");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (role == null) {
                    Role newRole = new Role();
                    newRole.setName(nameField.getText().trim());
                    newRole.setDescription(descField.getText().trim());
                    roleService.add(newRole);
                } else {
                    role.setName(nameField.getText().trim());
                    role.setDescription(descField.getText().trim());
                    roleService.update(role);
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save role: " + e.getMessage());
            }
        }
    }

    private static final String UPLOADS_DIR = "uploads/profile_pictures/";

    private void showProfileView() {
        contentArea.getChildren().clear();

        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(30));

        Label title = new Label("My Profile");
        title.getStyleClass().add("content-title");

        // Profile picture
        ImageView profileImage = new ImageView();
        profileImage.setFitWidth(120);
        profileImage.setFitHeight(120);
        profileImage.setPreserveRatio(false);
        profileImage.setSmooth(true);
        Circle clip = new Circle(60, 60, 60);
        profileImage.setClip(clip);
        loadProfileImage(profileImage, currentUser.getProfilePicture());

        Button changePicBtn = new Button("Change Picture");
        changePicBtn.getStyleClass().add("profile-pic-button");
        final String[] selectedPicPath = {null};
        changePicBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Profile Picture");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fileChooser.showOpenDialog(contentArea.getScene().getWindow());
            if (file != null) {
                selectedPicPath[0] = file.getAbsolutePath();
                profileImage.setImage(new Image(file.toURI().toString(), 120, 120, false, true));
            }
        });

        Button deletePicBtn = new Button("Remove Picture");
        deletePicBtn.getStyleClass().add("profile-pic-delete-button");
        final boolean[] deletePic = {false};
        deletePicBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove your profile picture?");
            confirm.setHeaderText("Remove Profile Picture");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedPicPath[0] = null;
                deletePic[0] = true;
                profileImage.setImage(null);
                profileImage.setStyle("-fx-background-color: #e0e0e0;");
            }
        });

        HBox picButtons = new HBox(10, changePicBtn, deletePicBtn);
        picButtons.setAlignment(Pos.CENTER);

        VBox picBox = new VBox(10, profileImage, picButtons);
        picBox.setAlignment(Pos.CENTER);

        GridPane form = new GridPane();
        form.getStyleClass().add("profile-form");
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(25));
        form.setMaxWidth(450);

        TextField nameField = new TextField(currentUser.getName());
        nameField.setPromptText("Full Name");
        nameField.getStyleClass().add("text-input");

        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-input");

        TextField phoneField = new TextField(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        phoneField.setPromptText("Phone");
        phoneField.getStyleClass().add("text-input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New password (leave empty to keep current)");
        passwordField.getStyleClass().add("text-input");

        Label feedbackLabel = new Label();
        feedbackLabel.setVisible(false);
        feedbackLabel.setManaged(false);

        int row = 0;
        form.add(new Label("Name:"), 0, row);
        form.add(nameField, 1, row++);
        form.add(new Label("Email:"), 0, row);
        form.add(emailField, 1, row++);
        form.add(new Label("Phone:"), 0, row);
        form.add(phoneField, 1, row++);
        form.add(new Label("Password:"), 0, row);
        form.add(passwordField, 1, row++);

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("profile-save-button");
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                feedbackLabel.setText("Name and email are required.");
                feedbackLabel.getStyleClass().setAll("error-label");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
                return;
            }
            if (!ValidationUtils.isValidName(name)) {
                feedbackLabel.setText("Name must be at least 2 characters and contain only letters.");
                feedbackLabel.getStyleClass().setAll("error-label");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
                return;
            }
            if (!ValidationUtils.isValidEmail(email)) {
                feedbackLabel.setText("Please enter a valid email address.");
                feedbackLabel.getStyleClass().setAll("error-label");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
                return;
            }
            if (!ValidationUtils.isValidPhone(phone)) {
                feedbackLabel.setText("Phone must be 8-15 digits, optionally starting with +.");
                feedbackLabel.getStyleClass().setAll("error-label");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
                return;
            }
            try {
                // Handle profile picture
                if (deletePic[0]) {
                    String oldPic = currentUser.getProfilePicture();
                    if (oldPic != null && !oldPic.isEmpty()) {
                        File oldFile = new File(oldPic);
                        oldFile.delete();
                    }
                    currentUser.setProfilePicture(null);
                    deletePic[0] = false;
                } else if (selectedPicPath[0] != null) {
                    String savedPath = saveProfilePicture(selectedPicPath[0], currentUser.getId());
                    currentUser.setProfilePicture(savedPath);
                }

                currentUser.setName(name);
                currentUser.setEmail(email);
                currentUser.setPhone(phone);
                userService.update(currentUser);

                String newPassword = passwordField.getText();
                if (!newPassword.isEmpty()) {
                    if (newPassword.length() < 6) {
                        feedbackLabel.setText("Password must be at least 6 characters.");
                        feedbackLabel.getStyleClass().setAll("error-label");
                        feedbackLabel.setVisible(true);
                        feedbackLabel.setManaged(true);
                        return;
                    }
                    userService.updatePassword(currentUser.getId(), newPassword);
                }

                userNameLabel.setText("Welcome, " + currentUser.getName());
                feedbackLabel.setText("Profile updated successfully!");
                feedbackLabel.getStyleClass().setAll("success-label");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
                passwordField.clear();
                selectedPicPath[0] = null;
            } catch (SQLException ex) {
                feedbackLabel.setText("Failed to update profile: " + ex.getMessage());
                feedbackLabel.getStyleClass().setAll("error-label");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
            } catch (IOException ex) {
                feedbackLabel.setText("Failed to save profile picture.");
                feedbackLabel.getStyleClass().setAll("error-label");
                feedbackLabel.setVisible(true);
                feedbackLabel.setManaged(true);
            }
        });

        form.add(saveBtn, 1, row);

        container.getChildren().addAll(title, picBox, form, feedbackLabel);
        contentArea.getChildren().add(container);
    }

    private String saveProfilePicture(String sourcePath, long userId) throws IOException {
        Path uploadsPath = Paths.get(UPLOADS_DIR);
        if (!Files.exists(uploadsPath)) {
            Files.createDirectories(uploadsPath);
        }
        String extension = sourcePath.substring(sourcePath.lastIndexOf('.'));
        String fileName = "user_" + userId + extension;
        Path destination = uploadsPath.resolve(fileName);
        Files.copy(Paths.get(sourcePath), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }

    private void loadProfileImage(ImageView imageView, String picturePath) {
        if (picturePath != null && !picturePath.isEmpty()) {
            File file = new File(picturePath);
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString(), 120, 120, false, true));
                return;
            }
        }
        // Gray placeholder when no picture
        imageView.setStyle("-fx-background-color: #e0e0e0;");
    }

    private void showStatisticsView() {
        contentArea.getChildren().clear();

        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(10));

        Label title = new Label("User Statistics");
        title.getStyleClass().add("content-title");

        List<User> users;
        List<Role> roles;
        try {
            users = userService.getAll();
            roles = roleService.getAll();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load data: " + e.getMessage());
            return;
        }

        int total = users.size();
        long active = users.stream().filter(u -> "active".equals(u.getStatus())).count();
        long inactive = users.stream().filter(u -> "inactive".equals(u.getStatus())).count();
        long blocked = users.stream().filter(u -> "blocked".equals(u.getStatus())).count();

        // Stat cards
        HBox cards = new HBox(15);
        cards.setAlignment(Pos.CENTER_LEFT);
        cards.getChildren().addAll(
                createStatCard("Total Users", String.valueOf(total), "stat-card-total"),
                createStatCard("Active", String.valueOf(active), "stat-card-active"),
                createStatCard("Inactive", String.valueOf(inactive), "stat-card-inactive"),
                createStatCard("Blocked", String.valueOf(blocked), "stat-card-blocked")
        );

        // Charts row
        HBox chartsRow = new HBox(20);
        VBox.setVgrow(chartsRow, Priority.ALWAYS);

        // Pie chart - Users per role
        Map<Long, String> roleNames = new LinkedHashMap<>();
        for (Role r : roles) roleNames.put(r.getId(), r.getName());

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<Long, Long> roleCounts = new LinkedHashMap<>();
        for (User u : users) {
            roleCounts.merge(u.getRoleId(), 1L, Long::sum);
        }
        for (Map.Entry<Long, Long> entry : roleCounts.entrySet()) {
            String name = roleNames.getOrDefault(entry.getKey(), "Role " + entry.getKey());
            pieData.add(new PieChart.Data(name + " (" + entry.getValue() + ")", entry.getValue()));
        }
        PieChart pieChart = new PieChart(pieData);
        pieChart.setTitle("Users per Role");
        pieChart.setLabelsVisible(true);
        pieChart.setMaxHeight(350);
        HBox.setHgrow(pieChart, Priority.ALWAYS);

        // Bar chart - Registrations over last 7 days
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Registrations");
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Registrations (Last 7 Days)");
        barChart.setLegendVisible(false);
        barChart.setMaxHeight(350);
        HBox.setHgrow(barChart, Priority.ALWAYS);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd");
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            long count = users.stream()
                    .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().toLocalDate().equals(day))
                    .count();
            series.getData().add(new XYChart.Data<>(day.format(fmt), count));
        }
        barChart.getData().add(series);

        chartsRow.getChildren().addAll(pieChart, barChart);

        container.getChildren().addAll(title, cards, chartsRow);
        contentArea.getChildren().add(container);
    }

    private VBox createStatCard(String label, String value, String styleClass) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().addAll("stat-card", styleClass);
        card.setPadding(new Insets(15, 25, 15, 25));
        card.setMinWidth(130);

        Label valLabel = new Label(value);
        valLabel.getStyleClass().add("stat-card-value");
        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("stat-card-label");

        card.getChildren().addAll(valLabel, nameLabel);
        return card;
    }

    // ==================== Farm Management (Admin) ====================

    private void showFarmsView() {
        contentArea.getChildren().clear();

        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_LEFT);

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Farm Management");
        title.getStyleClass().add("content-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("action-button-add");
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("action-button-edit");
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("action-button-delete");
        Button approveBtn = new Button("Approve");
        approveBtn.getStyleClass().add("action-button-approve");
        Button rejectBtn = new Button("Reject");
        rejectBtn.getStyleClass().add("action-button-reject");
        Button fieldsBtn = new Button("Fields");
        fieldsBtn.getStyleClass().add("action-button-edit");

        header.getChildren().addAll(title, spacer, addBtn, editBtn, deleteBtn, approveBtn, rejectBtn, fieldsBtn);

        // Search & Filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or location...");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ComboBox<String> statusFilter = new ComboBox<>(FXCollections.observableArrayList("All", "Pending", "Approved", "Rejected", "Inactive"));
        statusFilter.setValue("All");
        statusFilter.getStyleClass().add("filter-combo");

        filterBar.getChildren().addAll(searchField, statusFilter);

        // Table
        TableView<Farm> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Farm, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(60);

        TableColumn<Farm, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Farm, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Farm, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("farmType"));

        TableColumn<Farm, Double> areaCol = new TableColumn<>("Area");
        areaCol.setCellValueFactory(new PropertyValueFactory<>("area"));
        areaCol.setMaxWidth(80);

        TableColumn<Farm, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Farm, Long> userCol = new TableColumn<>("User ID");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userCol.setMaxWidth(80);

        table.getColumns().addAll(idCol, nameCol, locationCol, typeCol, areaCol, statusCol, userCol);

        // Load data with FilteredList + SortedList
        ObservableList<Farm> masterData = FXCollections.observableArrayList();
        try {
            masterData.addAll(farmService.getAll());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load farms: " + e.getMessage());
        }

        FilteredList<Farm> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                filteredData.setPredicate(farm -> filterFarm(farm, newVal, statusFilter.getValue())));
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                filteredData.setPredicate(farm -> filterFarm(farm, searchField.getText(), newVal)));

        SortedList<Farm> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        // Reload helper
        Runnable reloadTable = () -> {
            masterData.clear();
            try {
                masterData.addAll(farmService.getAll());
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load farms: " + ex.getMessage());
            }
        };

        // Button actions
        addBtn.setOnAction(e -> {
            showFarmFormDialog(null, true);
            reloadTable.run();
        });

        editBtn.setOnAction(e -> {
            Farm selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a farm to edit.");
                return;
            }
            showFarmFormDialog(selected, true);
            reloadTable.run();
        });

        deleteBtn.setOnAction(e -> {
            Farm selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a farm to delete.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete farm \"" + selected.getName() + "\"?");
            confirm.setHeaderText("Confirm Deletion");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    farmService.delete(selected.getId());
                    reloadTable.run();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete farm: " + ex.getMessage());
                }
            }
        });

        approveBtn.setOnAction(e -> {
            Farm selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a farm to approve.");
                return;
            }
            if ("approved".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Already Approved", "This farm is already approved.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Approve farm \"" + selected.getName() + "\"?");
            confirm.setHeaderText("Confirm Approval");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    selected.setStatus("approved");
                    selected.setApprovedAt(java.time.LocalDateTime.now());
                    selected.setApprovedBy(currentUser.getId());
                    farmService.update(selected);
                    reloadTable.run();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve farm: " + ex.getMessage());
                }
            }
        });

        rejectBtn.setOnAction(e -> {
            Farm selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a farm to reject.");
                return;
            }
            if ("rejected".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Already Rejected", "This farm is already rejected.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Reject farm \"" + selected.getName() + "\"?");
            confirm.setHeaderText("Confirm Rejection");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    selected.setStatus("rejected");
                    selected.setApprovedAt(null);
                    selected.setApprovedBy(null);
                    farmService.update(selected);
                    reloadTable.run();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject farm: " + ex.getMessage());
                }
            }
        });

        fieldsBtn.setOnAction(e -> {
            Farm selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a farm to view its fields.");
                return;
            }
            showFieldsView(selected);
        });

        container.getChildren().addAll(header, filterBar, table);
        contentArea.getChildren().add(container);
    }

    private boolean filterFarm(Farm farm, String searchText, String statusValue) {
        boolean matchesSearch = true;
        if (searchText != null && !searchText.trim().isEmpty()) {
            String lower = searchText.trim().toLowerCase();
            matchesSearch = (farm.getName() != null && farm.getName().toLowerCase().contains(lower))
                    || (farm.getLocation() != null && farm.getLocation().toLowerCase().contains(lower));
        }
        boolean matchesStatus = true;
        if (statusValue != null && !"All".equals(statusValue)) {
            matchesStatus = statusValue.toLowerCase().equals(farm.getStatus());
        }
        return matchesSearch && matchesStatus;
    }

    // ==================== My Farms (Farmer) ====================

    private void showMyFarmsView() {
        contentArea.getChildren().clear();

        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_LEFT);

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("My Farms");
        title.getStyleClass().add("content-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("action-button-add");
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("action-button-edit");
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("action-button-delete");
        Button fieldsBtn = new Button("Fields");
        fieldsBtn.getStyleClass().add("action-button-edit");

        header.getChildren().addAll(title, spacer, addBtn, editBtn, deleteBtn, fieldsBtn);

        // Search & Filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or location...");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ComboBox<String> statusFilter = new ComboBox<>(FXCollections.observableArrayList("All", "Pending", "Approved", "Rejected", "Inactive"));
        statusFilter.setValue("All");
        statusFilter.getStyleClass().add("filter-combo");

        filterBar.getChildren().addAll(searchField, statusFilter);

        // Table
        TableView<Farm> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Farm, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(60);

        TableColumn<Farm, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Farm, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Farm, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("farmType"));

        TableColumn<Farm, Double> areaCol = new TableColumn<>("Area");
        areaCol.setCellValueFactory(new PropertyValueFactory<>("area"));
        areaCol.setMaxWidth(80);

        TableColumn<Farm, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, nameCol, locationCol, typeCol, areaCol, statusCol);

        // Load data - only current user's farms
        ObservableList<Farm> masterData = FXCollections.observableArrayList();
        try {
            masterData.addAll(farmService.getByUserId(currentUser.getId()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load farms: " + e.getMessage());
        }

        FilteredList<Farm> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                filteredData.setPredicate(farm -> filterFarm(farm, newVal, statusFilter.getValue())));
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                filteredData.setPredicate(farm -> filterFarm(farm, searchField.getText(), newVal)));

        SortedList<Farm> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        // Reload helper
        Runnable reloadTable = () -> {
            masterData.clear();
            try {
                masterData.addAll(farmService.getByUserId(currentUser.getId()));
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load farms: " + ex.getMessage());
            }
        };

        // Button actions
        addBtn.setOnAction(e -> {
            showFarmFormDialog(null, false);
            reloadTable.run();
        });

        editBtn.setOnAction(e -> {
            Farm selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a farm to edit.");
                return;
            }
            showFarmFormDialog(selected, false);
            reloadTable.run();
        });

        deleteBtn.setOnAction(e -> {
            Farm selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a farm to delete.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete farm \"" + selected.getName() + "\"?");
            confirm.setHeaderText("Confirm Deletion");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    farmService.delete(selected.getId());
                    reloadTable.run();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete farm: " + ex.getMessage());
                }
            }
        });

        fieldsBtn.setOnAction(e -> {
            Farm selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a farm to view its fields.");
                return;
            }
            showFieldsView(selected);
        });

        container.getChildren().addAll(header, filterBar, table);
        contentArea.getChildren().add(container);
    }

    // ==================== Farm Form Dialog ====================

    private void showFarmFormDialog(Farm farm, boolean isAdmin) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(farm == null ? "Add Farm" : "Edit Farm");
        dialog.setHeaderText(farm == null ? "Create a new farm" : "Edit farm: " + farm.getName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(farm != null ? farm.getName() : "");
        nameField.setPromptText("Farm name");
        TextField locationField = new TextField(farm != null ? farm.getLocation() : "");
        locationField.setPromptText("Location");
        TextField latField = new TextField(farm != null && farm.getLatitude() != null ? farm.getLatitude().toString() : "");
        latField.setPromptText("Latitude (optional)");
        TextField lngField = new TextField(farm != null && farm.getLongitude() != null ? farm.getLongitude().toString() : "");
        lngField.setPromptText("Longitude (optional)");

        Button mapPickerBtn = new Button("Pick on Map");
        mapPickerBtn.getStyleClass().add("action-button-add");
        mapPickerBtn.setOnAction(ev -> showMapPickerDialog(latField, lngField));

        TextField areaField = new TextField(farm != null && farm.getArea() != null ? farm.getArea().toString() : "");
        areaField.setPromptText("Area in hectares (optional)");
        TextField typeField = new TextField(farm != null ? (farm.getFarmType() != null ? farm.getFarmType() : "") : "");
        typeField.setPromptText("Farm type (optional)");
        TextField descField = new TextField(farm != null ? (farm.getDescription() != null ? farm.getDescription() : "") : "");
        descField.setPromptText("Description (optional)");

        int row = 0;
        grid.add(new Label("Name:"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("Location:"), 0, row);
        grid.add(locationField, 1, row++);
        grid.add(new Label("Coordinates:"), 0, row);
        HBox coordBox = new HBox(8, latField, lngField, mapPickerBtn);
        coordBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(coordBox, 1, row++);
        grid.add(new Label("Area:"), 0, row);
        grid.add(areaField, 1, row++);
        grid.add(new Label("Type:"), 0, row);
        grid.add(typeField, 1, row++);
        grid.add(new Label("Description:"), 0, row);
        grid.add(descField, 1, row++);

        // Admin-only fields: status and user selection
        ComboBox<String> statusCombo = null;
        TextField userIdField = null;
        if (isAdmin) {
            statusCombo = new ComboBox<>(FXCollections.observableArrayList("pending", "approved", "rejected", "inactive"));
            statusCombo.setValue(farm != null ? farm.getStatus() : "pending");
            grid.add(new Label("Status:"), 0, row);
            grid.add(statusCombo, 1, row++);

            userIdField = new TextField(farm != null ? String.valueOf(farm.getUserId()) : "");
            userIdField.setPromptText("User ID (owner)");
            grid.add(new Label("User ID:"), 0, row);
            grid.add(userIdField, 1, row);
        }

        dialog.getDialogPane().setContent(grid);

        final ComboBox<String> finalStatusCombo = statusCombo;
        final TextField finalUserIdField = userIdField;

        // Validation loop
        while (true) {
            Optional<ButtonType> result = dialog.showAndWait();
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                break;
            }

            String nameVal = nameField.getText().trim();
            String locationVal = locationField.getText().trim();

            if (nameVal.length() < 2) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Farm name must be at least 2 characters.");
                continue;
            }
            if (locationVal.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Location is required.");
                continue;
            }

            // Parse optional numeric fields
            Double latVal = null;
            Double lngVal = null;
            Double areaVal = null;

            if (!latField.getText().trim().isEmpty()) {
                try {
                    latVal = Double.parseDouble(latField.getText().trim());
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Latitude must be a valid number.");
                    continue;
                }
            }
            if (!lngField.getText().trim().isEmpty()) {
                try {
                    lngVal = Double.parseDouble(lngField.getText().trim());
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Longitude must be a valid number.");
                    continue;
                }
            }
            if (!areaField.getText().trim().isEmpty()) {
                try {
                    areaVal = Double.parseDouble(areaField.getText().trim());
                    if (areaVal < 0) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error", "Area must be a positive number.");
                        continue;
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Area must be a valid number.");
                    continue;
                }
            }

            long ownerUserId = currentUser.getId();
            if (isAdmin && finalUserIdField != null && !finalUserIdField.getText().trim().isEmpty()) {
                try {
                    ownerUserId = Long.parseLong(finalUserIdField.getText().trim());
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "User ID must be a valid number.");
                    continue;
                }
            }

            try {
                if (farm == null) {
                    Farm newFarm = new Farm();
                    newFarm.setUserId(ownerUserId);
                    newFarm.setName(nameVal);
                    newFarm.setLocation(locationVal);
                    newFarm.setLatitude(latVal);
                    newFarm.setLongitude(lngVal);
                    newFarm.setArea(areaVal);
                    newFarm.setFarmType(typeField.getText().trim().isEmpty() ? null : typeField.getText().trim());
                    newFarm.setDescription(descField.getText().trim().isEmpty() ? null : descField.getText().trim());
                    newFarm.setStatus(isAdmin && finalStatusCombo != null ? finalStatusCombo.getValue() : "pending");
                    farmService.add(newFarm);
                } else {
                    farm.setName(nameVal);
                    farm.setLocation(locationVal);
                    farm.setLatitude(latVal);
                    farm.setLongitude(lngVal);
                    farm.setArea(areaVal);
                    farm.setFarmType(typeField.getText().trim().isEmpty() ? null : typeField.getText().trim());
                    farm.setDescription(descField.getText().trim().isEmpty() ? null : descField.getText().trim());
                    if (isAdmin) {
                        if (finalStatusCombo != null) farm.setStatus(finalStatusCombo.getValue());
                        if (finalUserIdField != null && !finalUserIdField.getText().trim().isEmpty()) {
                            farm.setUserId(ownerUserId);
                        }
                    }
                    farmService.update(farm);
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save farm: " + e.getMessage());
            }
            break;
        }
    }

    // ==================== Map Picker Dialog ====================

    private void showMapPickerDialog(TextField latField, TextField lngField) {
        Dialog<ButtonType> mapDialog = new Dialog<>();
        mapDialog.setTitle("Pick Location on Map");
        mapDialog.setHeaderText("Click on the map to select coordinates");
        mapDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        mapDialog.setResizable(true);

        WebView webView = new WebView();
        webView.setPrefSize(700, 500);
        WebEngine engine = webView.getEngine();

        // Initial coordinates: use existing values or default to Tunisia center
        String initLat = latField.getText().trim().isEmpty() ? "36.8" : latField.getText().trim();
        String initLng = lngField.getText().trim().isEmpty() ? "10.18" : lngField.getText().trim();

        String html = "<!DOCTYPE html><html><head>" +
            "<meta charset='utf-8'/>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>" +
            "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>" +
            "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
            "<style>html,body,#map{margin:0;padding:0;width:100%;height:100%;}</style>" +
            "</head><body>" +
            "<div id='map'></div>" +
            "<script>" +
            "var map = L.map('map').setView([" + initLat + "," + initLng + "], 8);" +
            "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{" +
            "  attribution:'OpenStreetMap'," +
            "  maxZoom:19" +
            "}).addTo(map);" +
            "var marker = null;" +
            "var selectedLat = " + initLat + ";" +
            "var selectedLng = " + initLng + ";" +
            // If there were existing coordinates, place initial marker
            (latField.getText().trim().isEmpty() ? "" :
                "marker = L.marker([" + initLat + "," + initLng + "]).addTo(map);") +
            "map.on('click', function(e) {" +
            "  selectedLat = e.latlng.lat;" +
            "  selectedLng = e.latlng.lng;" +
            "  if(marker) map.removeLayer(marker);" +
            "  marker = L.marker([selectedLat, selectedLng]).addTo(map);" +
            "  document.title = selectedLat.toFixed(8) + ',' + selectedLng.toFixed(8);" +
            "});" +
            "</script></body></html>";

        engine.loadContent(html);

        mapDialog.getDialogPane().setContent(webView);
        mapDialog.getDialogPane().setPrefSize(720, 550);

        Optional<ButtonType> result = mapDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String title = (String) engine.executeScript("document.title");
            if (title != null && title.contains(",")) {
                String[] parts = title.split(",");
                latField.setText(parts[0]);
                lngField.setText(parts[1]);
            }
        }
    }

    // ==================== Fields View ====================

    private void showFieldsView(Farm farm) {
        contentArea.getChildren().clear();

        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_LEFT);

        // Header with back button
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("action-button-block");
        backBtn.setOnAction(e -> {
            // Determine if admin or farmer to go back to the correct view
            String roleName = "";
            try {
                Role role = roleService.getById(currentUser.getRoleId());
                if (role != null) roleName = role.getName();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if ("admin".equalsIgnoreCase(roleName)) {
                showFarmsView();
            } else {
                showMyFarmsView();
            }
        });

        Label title = new Label("Fields of \"" + farm.getName() + "\"");
        title.getStyleClass().add("content-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("action-button-add");
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("action-button-edit");
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("action-button-delete");

        header.getChildren().addAll(backBtn, title, spacer, addBtn, editBtn, deleteBtn);

        // Table
        TableView<Field> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Field, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(60);

        TableColumn<Field, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Field, Double> areaCol = new TableColumn<>("Area");
        areaCol.setCellValueFactory(new PropertyValueFactory<>("area"));
        areaCol.setMaxWidth(80);

        TableColumn<Field, String> soilCol = new TableColumn<>("Soil Type");
        soilCol.setCellValueFactory(new PropertyValueFactory<>("soilType"));

        TableColumn<Field, String> cropCol = new TableColumn<>("Crop Type");
        cropCol.setCellValueFactory(new PropertyValueFactory<>("cropType"));

        TableColumn<Field, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, nameCol, areaCol, soilCol, cropCol, statusCol);

        // Load data
        ObservableList<Field> masterData = FXCollections.observableArrayList();
        try {
            masterData.addAll(fieldService.getByFarmId(farm.getId()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load fields: " + e.getMessage());
        }
        table.setItems(masterData);

        // Reload helper
        Runnable reloadTable = () -> {
            masterData.clear();
            try {
                masterData.addAll(fieldService.getByFarmId(farm.getId()));
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load fields: " + ex.getMessage());
            }
        };

        // Button actions
        addBtn.setOnAction(e -> {
            showFieldFormDialog(null, farm.getId());
            reloadTable.run();
        });

        editBtn.setOnAction(e -> {
            Field selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a field to edit.");
                return;
            }
            showFieldFormDialog(selected, farm.getId());
            reloadTable.run();
        });

        deleteBtn.setOnAction(e -> {
            Field selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a field to delete.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete field \"" + selected.getName() + "\"?");
            confirm.setHeaderText("Confirm Deletion");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    fieldService.delete(selected.getId());
                    reloadTable.run();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete field: " + ex.getMessage());
                }
            }
        });

        container.getChildren().addAll(header, table);
        contentArea.getChildren().add(container);
    }

    // ==================== Field Form Dialog ====================

    private void showFieldFormDialog(Field field, long farmId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(field == null ? "Add Field" : "Edit Field");
        dialog.setHeaderText(field == null ? "Create a new field" : "Edit field: " + field.getName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(field != null ? field.getName() : "");
        nameField.setPromptText("Field name");
        TextField areaField = new TextField(field != null ? String.valueOf(field.getArea()) : "");
        areaField.setPromptText("Area (required, positive number)");
        TextField soilField = new TextField(field != null ? (field.getSoilType() != null ? field.getSoilType() : "") : "");
        soilField.setPromptText("Soil type (optional)");
        TextField cropField = new TextField(field != null ? (field.getCropType() != null ? field.getCropType() : "") : "");
        cropField.setPromptText("Crop type (optional)");

        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("active", "inactive", "fallow"));
        statusCombo.setValue(field != null ? field.getStatus() : "active");

        int row = 0;
        grid.add(new Label("Name:"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("Area:"), 0, row);
        grid.add(areaField, 1, row++);
        grid.add(new Label("Soil Type:"), 0, row);
        grid.add(soilField, 1, row++);
        grid.add(new Label("Crop Type:"), 0, row);
        grid.add(cropField, 1, row++);
        grid.add(new Label("Status:"), 0, row);
        grid.add(statusCombo, 1, row);

        dialog.getDialogPane().setContent(grid);

        // Validation loop
        while (true) {
            Optional<ButtonType> result = dialog.showAndWait();
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                break;
            }

            String nameVal = nameField.getText().trim();
            if (nameVal.length() < 2) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Field name must be at least 2 characters.");
                continue;
            }

            double areaVal;
            try {
                areaVal = Double.parseDouble(areaField.getText().trim());
                if (areaVal <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Area must be a positive number.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Area must be a valid positive number.");
                continue;
            }

            try {
                if (field == null) {
                    Field newField = new Field();
                    newField.setFarmId(farmId);
                    newField.setName(nameVal);
                    newField.setArea(areaVal);
                    newField.setSoilType(soilField.getText().trim().isEmpty() ? null : soilField.getText().trim());
                    newField.setCropType(cropField.getText().trim().isEmpty() ? null : cropField.getText().trim());
                    newField.setStatus(statusCombo.getValue());
                    fieldService.add(newField);
                } else {
                    field.setName(nameVal);
                    field.setArea(areaVal);
                    field.setSoilType(soilField.getText().trim().isEmpty() ? null : soilField.getText().trim());
                    field.setCropType(cropField.getText().trim().isEmpty() ? null : cropField.getText().trim());
                    field.setStatus(statusCombo.getValue());
                    fieldService.update(field);
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save field: " + e.getMessage());
            }
            break;
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUsers(TableView<User> table) {
        try {
            table.setItems(FXCollections.observableArrayList(userService.getAll()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void loadRoles(TableView<Role> table) {
        try {
            table.setItems(FXCollections.observableArrayList(roleService.getAll()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load roles: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
