package cangkIR;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UserNavigationBarPage {

    private BorderPane container;
    private MenuBar navBar;
    private Menu menu;
    private MenuItem menuItemA, menuItemB, menuItemC;

    private HomePage homePage;
    private CartPage cartPage;

    public UserNavigationBarPage() {
        init();
    }

    private void init() {
        container = new BorderPane();
        homePage = new HomePage();
        cartPage = new CartPage();

        navBar = new MenuBar();
        menu = new Menu("Menu");

        menuItemA = new MenuItem("Home");
        menuItemB = new MenuItem("Chart");
        menuItemC = new MenuItem("Log Out");

        menuItemA.setOnAction(e -> redirectToHomePage());
        menuItemB.setOnAction(e -> redirectToCartPage());
        menuItemC.setOnAction(e -> redirectToMainPage());

        menu.getItems().addAll(menuItemA, menuItemB, menuItemC);

        navBar.getMenus().add(menu);
        container.setTop(navBar);
    }

    private void redirectToHomePage() {
        switchScene(homePage.getContainer());
    }

    private void redirectToCartPage() {
        switchScene(cartPage.getContainer());
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

    private void switchScene(Parent parent) {
        Stage stage = (Stage) container.getScene().getWindow();
        stage.setScene(new Scene(parent, 1000, 500));
    }

    public Parent getContainer() {
        return container;
    }
}
