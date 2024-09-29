package cangkIR;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Checkout extends Application implements EventHandler<ActionEvent> {

	Scene scene;
	Label checkoutConfirmation, purchase;
	Button yesButton, noButton;
	VBox vb;
	HBox hb;
	GridPane gridPane;
	Button submitButton;
	FlowPane flowPane;
	BorderPane borderPane;
	
	Connect connect = Connect.getInstance();

	void initialize() {
		vb = new VBox(10);
		hb = new HBox(20);
		borderPane = new BorderPane();

		checkoutConfirmation = new Label();
		purchase = new Label();

		yesButton = new Button();
		noButton = new Button();

		scene = new Scene(borderPane, 1000, 500);
	}

	void addComponent() {
		checkoutConfirmation.setText("Checkout Confirmation");
		purchase.setText("Are you sure you want to purchase?");
		yesButton.setText("Yes");
		noButton.setText("No");

		hb.getChildren().addAll(yesButton, noButton);

		vb.getChildren().addAll(purchase, hb);

		borderPane.setTop(checkoutConfirmation);
		borderPane.setCenter(vb);
	}

	void arrangeComponent() {
		BorderPane.setAlignment(checkoutConfirmation, Pos.TOP_CENTER);
		BorderPane.setAlignment(vb, Pos.CENTER);

		hb.setAlignment(Pos.CENTER);
		vb.setAlignment(Pos.CENTER);

	}
	
	int countTransactionHeader() {
	    try {
	        String query = "SELECT COUNT(*) FROM transactionheader";
	        connect.rs = connect.execQuery(query);

	        if (connect.rs.next()) {
	            return connect.rs.getInt(1);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0; 

	}
	
	String generateTransactionID() {
		
		countTransactionHeader();
		 int transactionHeadCount = countTransactionHeader();
		    int nextTransaction = transactionHeadCount + 1;

		    String formatTransactionID = String.format("%03d", nextTransaction);

		    String transactionID = "TR" + formatTransactionID;
		    
		    return transactionID;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		initialize();
		addComponent();
		arrangeComponent();
		

		mainStage.setScene(scene);
//		mainStage.setTitle("");
		mainStage.show();
	}

	public void show(Stage mainStage) {
		try {
			start(mainStage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handle(ActionEvent e) {
		if (e.getSource() == yesButton) {
			String transactionId = generateTransactionID();
			
			String query = "INSERT INTO transactionheader (TransactionID, UserID, CourierID, TransactionDate, UseDeliveryInsurance) VALUES ('"
					+ TransactionID + "', '" + UserID + "', '" + CourierID + "', '" + TransactionDate + "', '" + UseDeliveryInsurance + "')";

			connect.execUpdate(query);			
		}
		
		
	}

}