package cangkIR;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class RegisterPage {

	private BorderPane container;
	private TextField usernameTf;
	private TextField emailTf;
	private PasswordField passwordTf;
	private RadioButton maleRadio;
	private RadioButton femaleRadio;
	private Button registerBtn;
	private Hyperlink loginLink;
	private Main main;

	Connect connect = Connect.getInstance();
	ArrayList<User> userList = new ArrayList<>();

	public RegisterPage() {
		init();
	}

	private void init() {
		container = new BorderPane();
		main = new Main();

		loginLink = new Hyperlink("Already have an account? Click here to login!");
		loginLink.setOnAction(e -> redirectToMainPage());

		Label title = new Label("Register");
		title.setFont(Font.font("Arial", 20));
		title.setStyle("-fx-font-weight: bold;");

		Label usernameLbl = new Label("Username:");
		Label emailLbl = new Label("Email:");
		Label passwordLbl = new Label("Password:");
		Label genderLbl = new Label("Gender");
		genderLbl.setFont(Font.font("Arial", 20));
		genderLbl.setStyle("-fx-font-weight: bold;");

		usernameTf = new TextField();
		usernameTf.setPrefColumnCount(40);
		usernameTf.setPromptText("Input your username here");

		emailTf = new TextField();
		emailTf.setPrefColumnCount(40);
		emailTf.setPromptText("Input your email here");

		passwordTf = new PasswordField();
		passwordTf.setPrefColumnCount(40);
		passwordTf.setPromptText("Input your password here");

		maleRadio = new RadioButton("Male");
		femaleRadio = new RadioButton("Female");
		ToggleGroup genderGroup = new ToggleGroup();
		maleRadio.setToggleGroup(genderGroup);
		femaleRadio.setToggleGroup(genderGroup);

		HBox genderBox = new HBox(10);
		genderBox.getChildren().addAll(maleRadio, femaleRadio);

		registerBtn = new Button("Register");
		registerBtn.setOnAction(e -> attemptRegistration());

		GridPane gridPane = new GridPane();
		gridPane.add(usernameLbl, 0, 0);
		gridPane.add(usernameTf, 0, 1);
		gridPane.add(emailLbl, 0, 2);
		gridPane.add(emailTf, 0, 3);
		gridPane.add(passwordLbl, 0, 4);
		gridPane.add(passwordTf, 0, 5);
		gridPane.add(genderLbl, 0, 6);
		gridPane.add(genderBox, 0, 7);
		gridPane.add(loginLink, 0, 8);

		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setAlignment(Pos.CENTER);

		container.setTop(title);
		container.setCenter(gridPane);
		container.setAlignment(gridPane, Pos.CENTER);

		BorderPane.setAlignment(title, Pos.CENTER);
		container.setBottom(registerBtn);
		container.setAlignment(registerBtn, Pos.CENTER);
		container.setPadding(new Insets(50));

		getData();
	}

	public BorderPane getContainer() {
		return container;
	}

	private void AddSuccessfulAlert(String header, String content) {

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Message");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();

	}

	private void showAlert(String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void getData() {
		String query = "SELECT * FROM msuser";
		connect.rs = connect.execQuery(query);

		userList.clear();

		try {
			while (connect.rs.next()) {
				String id = connect.rs.getString("UserID");
				String username = connect.rs.getString("Username");
				String email = connect.rs.getString("UserEmail");
				String password = connect.rs.getString("UserPassword");
				String gender = connect.rs.getString("UserGender");
				String role = connect.rs.getString("UserRole");

				System.out.println("User gender: " + gender + ", Username: " + username + ", Role: " + role);

				User user = new User(id, username, email, password, gender, role);
				userList.add(user);

			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private String getUserRole(String newUsername) {
	    if (newUsername.toLowerCase().contains("admin")) {
	        return "Admin";
	    } else {
	        return "User";
	    }
	}

	private String generateUserID() {
		int nextIdx;
		nextIdx = userList.size() + 1;
		String userIdx = String.format("%03d", nextIdx);
		return "US" + userIdx;
	}

	private boolean checkUniqueName(String username) {
		for (User user : userList) {
			if (user.getUsername().equals(username)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkUniqueEmail(String newEmail) {
		for (User user : userList) {
			if (user.getEmail().equals(newEmail)) {
				return false;
			}
		}
		return true;
	}

	private String getGender() {
		if (maleRadio.isSelected()) {
			return "Male";
		} else if (femaleRadio.isSelected()) {
			return "Female";
		} else {
			return null;
		}
	}

	private void attemptRegistration() {
		String newUsername = usernameTf.getText();
		String newEmail = emailTf.getText();
		String password = passwordTf.getText();
		boolean isMaleSelected = maleRadio.isSelected();
		boolean isFemaleSelected = femaleRadio.isSelected();
		String newRole = getUserRole(newUsername);
		String userId = generateUserID();
		String gender = getGender();

		if (newUsername.isEmpty() || newEmail.isEmpty() || password.isEmpty()
				|| (!isMaleSelected && !isFemaleSelected)) {
			showAlert("Register Error", "All fields must be filled");
			return;
		} else if (!newEmail.toLowerCase().endsWith("@gmail.com")) {
			showAlert("Register Error", "Make sure your email ends with @gmail.com");
			return;
		} else if (password.length() < 8 || password.length() > 15) {
			showAlert("Register Error", "Make sure your password has a length of 8-15 characters");
			return;
		} else if (!password.matches("^[a-zA-Z0-9]+$")) {
			showAlert("Register Error", "Password must be alphanumeric");
			return;
		} else if (!checkUniqueName(newUsername)) {
			showAlert("Register Error", "Please choose a different username");
			return;
		} else if (!checkUniqueEmail(newEmail)) {
			showAlert("Register Error", "Please choose a different email");
			return;
		}

		User newUser = new User(userId, newUsername, newEmail, password, gender, newRole);
		newUser.generateUserID(); 

		newUser.setPassword(password);

		String query = "INSERT INTO msuser (UserID, Username, UserEmail, UserPassword, UserGender, UserRole) VALUES ('"
				+ userId + "', '" + newUsername + "', '" + newEmail + "', '" + password + "', '" + gender + "', '"
				+ newRole + "')";

		connect.execUpdate(query);
		userList.add(newUser);

		System.out.println("User ID: " + newUser.getUserId());
		System.out.println("Role: " + newUser.getRole());

		AddSuccessfulAlert("Registration Successful", "You have successfully registered!");
		redirectToMainPage();
	}

	private void redirectToMainPage() {
		Main mainPage = new Main();
		Stage primaryStage = new Stage();
		try {
			mainPage.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Stage currentStage = (Stage) container.getScene().getWindow();
		currentStage.close();
	}

}
