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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CartPage implements EventHandler<ActionEvent> {

	private MenuBar navBar;
	private Menu menu;
	private MenuItem menuItemA, menuItemB, menuItemC;

	private ObservableList<CartItem> cartItems;
	private VBox cartItemsVBox;
	private BorderPane container;

	private Label ownerLabel, deleteItemLabel, courierLabel, courierPriceLabel, totalPriceLabel;
	private TableView<CartItem> cupTableView;
	private ComboBox<Courier> courierComboBox;
	private CheckBox insuranceCheckBox;
	private Button deleteItemButton, checkoutButton;

	Connect connect = Connect.getInstance();

	ArrayList<Courier> courierList = new ArrayList<>();

	public CartPage() {
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

		ownerLabel = new Label("vncnt's Cart");
		ownerLabel.setFont(Font.font("Arial", 23));
		ownerLabel.setStyle("-fx-font-weight: bold;");

		deleteItemLabel = new Label("Delete Item");
		deleteItemLabel.setFont(Font.font("Arial", 20));
		deleteItemLabel.setStyle("-fx-font-weight: bold;");

		courierLabel = new Label("Courier");
		courierLabel.setFont(Font.font("Arial", 20));
		courierLabel.setStyle("-fx-font-weight: bold;");

		courierPriceLabel = new Label("Courier Price: ");
		courierPriceLabel.setFont(Font.font("Arial", 20));
		courierPriceLabel.setStyle("-fx-font-weight: bold;");

		totalPriceLabel = new Label("Total Price: ");
		totalPriceLabel.setFont(Font.font("Arial", 20));
		totalPriceLabel.setStyle("-fx-font-weight: bold;");

		cupTableView = new TableView<>();
		TableColumn<CartItem, String> cupNameColumn = new TableColumn<>("Cup Name");
		TableColumn<CartItem, Double> priceColumn = new TableColumn<>("Cup Price");
		TableColumn<CartItem, Integer> quantityColumn = new TableColumn<>("Quantity");
		TableColumn<CartItem, Integer> totalColumn = new TableColumn<>("Total");

		cupNameColumn.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

		cupTableView.setItems(cartItems);

		cupTableView.getColumns().addAll(cupNameColumn, priceColumn, quantityColumn, totalColumn);

		courierComboBox = new ComboBox<>();

		insuranceCheckBox = new CheckBox("Use Delivery Insurance (+$2000)");

		deleteItemButton = new Button("Delete Item");

		checkoutButton = new Button("Checkout");

		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		gridPane.add(ownerLabel, 0, 0, 2, 1);
		gridPane.add(cupTableView, 0, 2);

		VBox buttonBox = new VBox(10);
		buttonBox.getChildren().addAll(deleteItemLabel, deleteItemButton, courierLabel, courierComboBox,
				courierPriceLabel, insuranceCheckBox, totalPriceLabel, checkoutButton);
		gridPane.add(buttonBox, 1, 1, 1, 6);

		container.setCenter(gridPane);

		setEventHandler();

		courierComboBoxRefresh();
		courierPrice();
		getTotalPrice();
	}

	private void setEventHandler() {
		deleteItemButton.setOnAction(this);
		checkoutButton.setOnAction(this);
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
		String query = "SELECT * FROM mscourier";
		connect.rs = connect.execQuery(query);
		try {
			while (connect.rs.next()) {
				String courierID = connect.rs.getString("CourierID");
				String courierName = connect.rs.getString("CourierName");
				int courierPrice = connect.rs.getInt("CourierPrice");

				Courier courier = new Courier(courierID, courierName, courierPrice);
				courierList.add(courier);
			}
		} catch (Exception e) {

		}
	}

	private void courierComboBoxRefresh() {
		getData();
		ObservableList<Courier> courierData = FXCollections.observableArrayList(courierList);
		courierComboBox.setItems(courierData);
	}

	public void courierPrice() {
		courierComboBox.setOnAction(e -> {
			Courier selectedCourier = courierComboBox.getSelectionModel().getSelectedItem();
			if (selectedCourier != null) {
				courierPriceLabel.setText("Courier Price: $" + selectedCourier.getCourierPrice());
			}
		});
	}

	public void addCartItem(CartItem cartItem) {
		cartItems.add(cartItem);
		updateCartUI();
		getTotalPrice();
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
			Label cartItemLabel = new Label(item.getCupName() + " - Quantity: " + item.getQuantity());
			cartItemsVBox.getChildren().add(cartItemLabel);
		}
	}

	public double getTotalPrice() {
		double totalPrice = 0.0;
		for (CartItem item : cartItems) {
			double cupTotalPrice = item.getPrice() * item.getQuantity();
			totalPrice += cupTotalPrice;
		}

		Courier selectedCourier = courierComboBox.getSelectionModel().getSelectedItem();
		if (selectedCourier != null) {
			totalPrice += selectedCourier.getCourierPrice();
		}

		if (insuranceCheckBox.isSelected()) {
			totalPrice += 2000;
		}

		totalPriceLabel.setText("Total Price: $" + totalPrice);

		System.out.println("Total Price: " + totalPrice);
		return totalPrice;
	}

	public ObservableList<CartItem> getCartItems() {
		return cartItems;
	}

	public Parent getContainer() {
		return container;
	}

	private void showAlertInfo(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Message");
		alert.setHeaderText("Deletion Information");
		alert.setContentText("Cart Deleted Succesfully");
		alert.showAndWait();
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setContentText("Please select the item you want to delete");
		alert.setTitle("Error");
		alert.setHeaderText("Deletion Error");
		alert.setContentText(content);
		alert.showAndWait();
	}

	@Override
	public void handle(ActionEvent e) {
		if (e.getSource() == deleteItemButton) {
			System.out.println("Delete item button clicked");
			CartItem selected = cupTableView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				cartItems.remove(selected);
				showAlertInfo("Message", "Cart Deleted Succesfully");
			} else if (selected == null) {
				showAlert("Error", "Please select the item you want to delete");
			}
		} else if (e.getSource() == checkoutButton) {
			Checkout checkout = new Checkout();
			Stage mainStage = new Stage();
			checkout.show(mainStage);
			Stage currentStage = (Stage) container.getScene().getWindow();
			currentStage.close();
		}

		getTotalPrice();
	}
}
