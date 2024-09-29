package cangkIR;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CupManagementPage implements EventHandler<ActionEvent> {

	private MenuBar navBar;
	private Menu menu;
	private MenuItem menuItemA, menuItemB, menuItemC;

	private ObservableList<CartItem> cartItems;
	private VBox cartItemsVBox;
	private BorderPane container;

	private Label cupManagementLabel, cupNameLabel, cupPriceLabel;
	private TableView<Cup> cupTableView;
	private Button addCupButton, updatePriceButton, removeCupButton;

	private TextField cupNameField, cupPriceField;

	Connect connect = Connect.getInstance();

	ArrayList<Cup> cupList = new ArrayList<>();

	public CupManagementPage() {
		init();
	}

	private void init() {
		cartItems = FXCollections.observableArrayList();
		cartItemsVBox = new VBox();
		container = new BorderPane();
		navBar = new MenuBar();
		menu = new Menu("Menu");

		menuItemA = new MenuItem("Home");
		menuItemB = new MenuItem("Chart");
		menuItemC = new MenuItem("Log Out");

		menuItemA.setOnAction(e -> redirectToHomePage());
		menuItemC.setOnAction(e -> redirectToMainPage());

		menu.getItems().addAll(menuItemA, menuItemB, menuItemC);
		navBar.getMenus().add(menu);
		container.setTop(navBar);

		cupManagementLabel = new Label("Cup Management");
		cupManagementLabel.setFont(Font.font("Arial", 24));
		cupManagementLabel.setStyle("-fx-font-weight: bold;");

		cupNameLabel = new Label("Cup Name");
		cupPriceLabel = new Label("Cup Price");

		cupNameField = new TextField();
		cupNameField.setPromptText("Input cup name here");
		cupNameField.setPrefColumnCount(40);

		cupPriceField = new TextField();
		cupPriceField.setPromptText("Input cup price here");
		cupPriceField.setPrefColumnCount(40);

		cupTableView = new TableView<>();
		TableColumn<Cup, String> cupNameColumn = new TableColumn<>("Cup Name");
		TableColumn<Cup, Integer> priceColumn = new TableColumn<>("Cup Price");

		cupNameColumn.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		cupTableView.getColumns().addAll(cupNameColumn, priceColumn);

		addCupButton = new Button("Add Cup");
//		addCupButton.setOnAction(e -> addCupButtonClicked());

		updatePriceButton = new Button("Update Price");
//		updatePriceButton.setOnAction(e -> updatePriceButtonClicked());

		removeCupButton = new Button("Remove Cup");
//		removeCupButton.setOnAction(e -> removeCupButtonClicked());

		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		gridPane.add(cupManagementLabel, 0, 0, 2, 1);
		gridPane.add(cupTableView, 0, 2);

		VBox buttonBox = new VBox(10);
		buttonBox.getChildren().addAll(cupNameLabel, cupNameField, cupPriceLabel, cupPriceField, addCupButton,
				updatePriceButton, removeCupButton);
		gridPane.add(buttonBox, 1, 1, 1, 6);

		container.setCenter(gridPane);

		setEventHandler();
		refreshTable();
		addMouseListener();

	}

	private void setEventHandler() {
		addCupButton.setOnAction(this);
		updatePriceButton.setOnAction(this);
		removeCupButton.setOnAction(this);

	}

	private void redirectToHomePage() {
		Stage primaryStage = new Stage();

		HomePage homePage = new HomePage();
		Parent homePageParent = homePage.getContainer();

		primaryStage.setScene(new Scene(homePageParent, 1000, 500));
		primaryStage.show();

		Stage currentStage = (Stage) container.getScene().getWindow();
		currentStage.close();
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

	public void addCartItem(CartItem cartItem) {
		cartItems.add(cartItem);
		updateCartUI();
	}

	public void updateCartItemQuantity(String cupName, int quantity) {
		for (CartItem item : cartItems) {
			if (item.getCupName().equals(cupName)) {
				item.setQuantity(item.getQuantity() + quantity);
				updateCartUI();
				return;
			}
		}
	}

	public boolean containsCup(String cupName) {
		for (CartItem item : cartItems) {
			if (item.getCupName().equals(cupName)) {
				return true;
			}
		}
		return false;
	}

	private void updateCartUI() {
		cartItemsVBox.getChildren().clear();
		for (CartItem item : cartItems) {
			Label cartItemLabel = new Label(item.getCupName() + " Quantity: " + item.getQuantity());
			cartItemsVBox.getChildren().add(cartItemLabel);
		}
	}

	public Parent getContainer() {
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

	private boolean checkUniqueCup(String cupName) {
		for (Cup cup : cupList) {
			if (cupName.equals(cup.getCupName())) {
				return false;
			}
		}
		return true;

	}

	private String generateCupID() {
		int nextIdx;
		nextIdx = cupList.size() + 1;

		String cupIdx = String.format("%03d", nextIdx);

		return "CU" + cupIdx;
	}

	private void addMouseListener() {
		cupTableView.setOnMouseClicked(event -> {

			Cup selectedCup = cupTableView.getSelectionModel().getSelectedItem();

			if (selectedCup != null) {
				cupNameField.setText(selectedCup.getCupName());
				cupPriceField.setText(String.valueOf(selectedCup.getPrice()));
				cupPriceField.setEditable(true);
			}

		});
	}

	@Override
	public void handle(ActionEvent e) {

		if (e.getSource() == addCupButton) {
			String cupNameTb = cupNameField.getText();
			String cupPriceTb = cupPriceField.getText();

			if (!cupPriceTb.isEmpty() && !cupNameTb.isEmpty()) {

				double cupPrice = Double.parseDouble(cupPriceTb);

				if (cupPrice < 5000 || cupPrice > 1000000) {
					showAlert("Error", "Cup price must be between 5000 and 1000000.");
				}

				boolean unique = checkUniqueCup(cupNameTb);

				if (unique) {

					String cupID = generateCupID();
					Cup newCup = new Cup(cupID, cupNameTb, cupPrice);
//					cupTableView.getItems().add(newCup);
					String query = "INSERT INTO mscup (CupID, CupName, CupPrice) VALUES ('" + cupID + "', '" + cupNameTb
							+ "', " + cupPrice + ")";

					connect.execUpdate(query);
					cupList.add(newCup);

					AddSuccessfulAlert("Cup Management", "Cup Succesfully Added");
					refreshTable();
				} else {
					showAlert("Cup Management", "Cup Already Exists");
				}

			} else {
				showAlert("Cup Management", "Please fill out cup name and price.");
			}

		} else if (e.getSource() == updatePriceButton) {
			String cupNameTb = cupNameField.getText();
			String updatedPriceTb = cupPriceField.getText();

			if (cupNameTb.isEmpty() || updatedPriceTb.isEmpty()) {
				showAlert("Cup Management", "Please select a cup from the table to be updated");
			} else {
				try {
					int updatedPrice = Integer.parseInt(updatedPriceTb);
					if (updatedPrice < 5000 || updatedPrice > 1000000) {
						showAlert("Cup Management", "Cup price must be between 5000 and 1000000.");
					} else {
						String query = "UPDATE mscup SET CupPrice = " + updatedPrice + " WHERE CupName = '" + cupNameTb
								+ "'";
						connect.execUpdate(query);
						for (Cup cup : cupList) {
							if (cup.getCupName().equals(cupNameTb)) {
								cup.setPrice(updatedPrice);
							}
						}
						AddSuccessfulAlert("Cup Management", "Cup Price Successfully Updated");
						refreshTable();
					}
				} catch (NumberFormatException ex) {
					showAlert("Cup Management", "Please select a cup from the table to be updated");
				}
			}
		} else if (e.getSource() == removeCupButton) {
			String cupNameTb = cupNameField.getText();
			String cupPriceTb = cupPriceField.getText();

			if (cupNameTb.isEmpty() || cupPriceTb.isEmpty()) {
				showAlert("Cup Management", "Please select a cup from the table to be deleted");
			} else {
				String query = "DELETE FROM mscup WHERE CupName = '" + cupNameTb + "' AND CupPrice = " + cupPriceTb;
				connect.execUpdate(query);
				AddSuccessfulAlert("Cup Management", "Cup succesfully deleted");
				refreshTable();
			}

		}

	}

}
