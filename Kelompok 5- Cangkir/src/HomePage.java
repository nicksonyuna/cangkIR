package cangkIR;

import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class HomePage {

	private MenuBar navBar;
	private Menu menu;
	private MenuItem menuItemA, menuItemB, menuItemC;
	private CartPage cartPage;

	private BorderPane container;
	private Label tableTitleLabel, cupNameLabel, priceLabel;
	private TableView<Cup> cupTableView;
	private Spinner<Integer> quantitySpinner;
	private Button addToCartButton;

	public HomePage() {
		init();
	}

	ArrayList<Cup> cupList = new ArrayList<>();
	Connect connect = Connect.getInstance();

	private void init() {
		container = new BorderPane();

		navBar = new MenuBar();
		menu = new Menu("Menu");

		menuItemA = new MenuItem("Home");
		menuItemB = new MenuItem("Chart");
		menuItemC = new MenuItem("Log Out");

		menuItemB.setOnAction(e -> redirectToCartPage());
		menuItemC.setOnAction(e -> redirectToMainPage());

		menu.getItems().addAll(menuItemA, menuItemB, menuItemC);
		navBar.getMenus().add(menu);
		container.setTop(navBar);

		cartPage = new CartPage();

		tableTitleLabel = new Label("Cup List");
		cupNameLabel = new Label("Cup Name: ");
		cupNameLabel.setFont(Font.font("Arial", 20));
		cupNameLabel.setStyle("-fx-font-weight: bold;");

		priceLabel = new Label("Price: ");
		priceLabel.setFont(Font.font("Arial", 20));
		priceLabel.setStyle("-fx-font-weight: bold;");

		cupTableView = new TableView<>();
		TableColumn<Cup, String> cupNameColumn = new TableColumn<>("Cup Name");
		TableColumn<Cup, Double> priceColumn = new TableColumn<>("Price");

		cupNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		quantitySpinner = new Spinner<>(1, 20, 1);

		addToCartButton = new Button("Add to Cart");
		addToCartButton.setOnAction(e -> addToCartButtonClicked());

		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		gridPane.add(tableTitleLabel, 0, 0, 2, 1);
		gridPane.add(cupTableView, 0, 1, 1, 1);

		VBox cupInfoVBox = new VBox(5);
		cupInfoVBox.getChildren().addAll(cupNameLabel, quantitySpinner, priceLabel, addToCartButton);

		gridPane.add(cupInfoVBox, 1, 1);
		container.setLeft(gridPane);

		refreshTable();

	}

	private void getData() {
		String query = "SELECT * FROM mscup";
		connect.rs = connect.execQuery(query);

		cupList.clear();

		try {
			while (connect.rs.next()) {
				String id = connect.rs.getString("CupID");
				String name = connect.rs.getString("CupName");
				int price = connect.rs.getInt("CupPrice");

				Cup cup = new Cup(id, name, price);
				cupList.add(cup);

			}
		} catch (Exception e) {

		}
	}

	private void refreshTable() {
		getData();

		ObservableList<Cup> cupData = FXCollections.observableArrayList(cupList);
		cupTableView.setItems(cupData);

		cupTableView.getColumns().clear();
		TableColumn<Cup, String> cupNameColumn = new TableColumn<>("Cup Name");
		TableColumn<Cup, Double> priceColumn = new TableColumn<>("Cup Price");

		cupNameColumn.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		cupTableView.getColumns().addAll(cupNameColumn, priceColumn);
	}

	private void redirectToMainPage() {
		try {
			Stage primaryStage = new Stage();
			Main mainPage = new Main();
			mainPage.start(primaryStage);

			Stage currentStage = (Stage) container.getScene().getWindow();
			currentStage.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void redirectToCartPage() {
		Stage primaryStage = new Stage();
		Parent cartPageParent = cartPage.getContainer();
		primaryStage.setScene(new Scene(cartPageParent, 1000, 500));
		primaryStage.show();
		Stage currentStage = (Stage) container.getScene().getWindow();
		currentStage.close();
	}

	private void addToCartButtonClicked() {
		Cup selectedCup = cupTableView.getSelectionModel().getSelectedItem();
		int quantity = quantitySpinner.getValue();

		if (cartPage == null) {
			System.err.println("Error: Cart page is not initialized.");
			return;
		}

		if (selectedCup == null) {
			showAlert("Error", "Please select a cup to be added.");
			return;
		}

		if (cupNameLabel == null || priceLabel == null) {
			System.err.println("Error: Cup name label or price label is not initialized.");
			return;
		}

		if (quantity > 0) {
			double totalPrice = selectedCup.getPrice() * quantity; 

			CartItem cartItem = new CartItem(selectedCup.getCupName(), selectedCup.getPrice(), quantity);
		
			if (cartPage.containsCup(selectedCup.getCupName())) {
				cartPage.updateCartItemQuantity(selectedCup.getCupName(), quantity);
			} else {
				cartPage.addCartItem(cartItem);
			}

			cupNameLabel.setText("Cup Name: " + selectedCup.getCupName());
			priceLabel.setText("Price: $" + totalPrice);
			System.out.println("Quantity: " + quantity);

			showAlertInfo("Message", "Item successfully added to the cart.\nTotal Price: $" + totalPrice);

			refreshTable();
		} else {
			showAlert("Error", "Quantity must be greater than 0");
		}
	}

	private void showAlertInfo(String header, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("message");
		alert.setHeaderText("Chart Info");
		alert.setContentText("Item Successfully Added!");
		alert.showAndWait();

	}

	private void showAlert(String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setContentText("Please select a cup to be added!");
		alert.setTitle("Error");
		alert.setHeaderText("Chart Error");
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void switchScene(Parent parent) {
		Stage stage = (Stage) container.getScene().getWindow();
		stage.setScene(new Scene(parent, 1000, 500));
	}

	public Parent getContainer() {
		return container;
	}
}
