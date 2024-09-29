package cangkIR;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

	BorderPane container;
	GridPane formContainer;

	Label title, nameLbl, passwordLbl;
	TextField nameTf;
	PasswordField passwordTf;
	Button loginBtn;
	Hyperlink registerLink;
	private RegisterPage registerPage;
	private UserNavigationBarPage userNavigationBarPage;
	private AdminNavigationBarPage adminNavigationBarPage;

	ArrayList<User> userList = new ArrayList<>();
	Connect connect = Connect.getInstance();

	public static void main(String[] args) {
		launch(args);
	}

	public void init() {
		registerPage = new RegisterPage();
		adminNavigationBarPage = new AdminNavigationBarPage();
		userNavigationBarPage = new UserNavigationBarPage();

		registerLink = new Hyperlink("Don't have an account yet? Register Here!");
		registerLink.setOnAction(e -> redirectToRegisterPage());

		container = new BorderPane();
		formContainer = new GridPane();

		title = new Label("Login");
		title.setFont(Font.font("Arial", 20));
		title.setStyle("-fx-font-weight: bold;");

		nameLbl = new Label("Name");
		passwordLbl = new Label("Password");

		nameTf = new TextField();
		nameTf.setPromptText("");
		nameTf.setPrefColumnCount(40);

		passwordTf = new PasswordField();
		passwordTf.setPromptText("Input your password here");
		passwordTf.setPrefColumnCount(40);

		loginBtn = new Button("Login");
		loginBtn.setOnAction(e -> attemptLogin());

		getData();
	}

    private void redirectToRegisterPage() {
        Scene registerScene = new Scene(registerPage.getContainer(), 1000, 500);
        Stage primaryStage = (Stage) container.getScene().getWindow();
        primaryStage.setScene(registerScene);
    }

	private void attemptLogin() {
		String username = nameTf.getText();
		String password = passwordTf.getText();
		String userRole = getUserRole(username);
		String userPassword = getUserPassword(username);

		System.out.println("Entered username: " + username);
		System.out.println("Entered password: " + password);
		System.out.println("Retrieved user password: " + userPassword);

		if (username.isEmpty() || password.isEmpty() || !password.equals(userPassword)) {
			displayAlert("Error", "Fill out your username and password");
		} else {
			if (userRole != null) {
				if (userRole.equals("Admin")) {
					switchScene(adminNavigationBarPage.getContainer());
				} else if (userRole.equals("User")) {
					switchScene(userNavigationBarPage.getContainer());
				} else {
					displayAlert("Error", "Role is not available");
				}
			} else {
				displayAlert("Error", "User not found");
			}
		}
	}

	private String getUserRole(String username) {
		for (User user : userList) {
			if (user.getUsername().equals(username)) {
				return user.getRole();
			}
		}
		return null; 
	}

	private String getUserPassword(String username) {
		for (User user : userList) {
			if (user.getUsername().equals(username)) {
				return user.getPassword();
			}
		}
		return null;
	}

	private void displayAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Login Error");
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void switchScene(Parent parent) {
		Scene scene = new Scene(parent, 1000, 500);
		Stage primaryStage = (Stage) container.getScene().getWindow();
		primaryStage.setScene(scene);
	}

	public void position() {
		container.setTop(title);
		container.setCenter(formContainer);
		container.setBottom(loginBtn);
		container.setPadding(new Insets(150));

		formContainer.add(nameLbl, 0, 0);
		formContainer.add(nameTf, 1, 0);

		formContainer.add(passwordLbl, 0, 1);
		formContainer.add(passwordTf, 1, 1);

		formContainer.setVgap(10);
		formContainer.setHgap(10);

		formContainer.setAlignment(Pos.CENTER);
		BorderPane.setAlignment(title, Pos.CENTER);
		BorderPane.setAlignment(loginBtn, Pos.CENTER);

		BorderPane.setMargin(loginBtn, new Insets(10, 0, 0, 0));

		VBox vbox = new VBox(10);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(loginBtn, registerLink);

		container.setBottom(vbox);
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

	@Override
	public void start(Stage stage) throws Exception {
		init();
		position();

		HomePage homePage = new HomePage();
		UserNavigationBarPage userNavBar = new UserNavigationBarPage();
		CartPage cartPage = new CartPage();

		BorderPane mainContainer = new BorderPane();
		mainContainer.setTop(userNavBar.getContainer());
		mainContainer.setCenter(homePage.getContainer());
		mainContainer.setBottom(cartPage.getContainer());

		Scene scene = new Scene(container, 1000, 500);

		stage.setScene(scene);
		stage.setTitle("cangkIR");
		stage.show();
	}

}
