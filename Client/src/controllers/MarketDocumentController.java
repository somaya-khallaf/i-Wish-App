/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import client.LoadScenes;
import client.ServerConnection;
import client.Utils;
import com.google.gson.*;
import dto.ProductDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MarketDocumentController implements Initializable {

    private final ServerConnection serverConnection;
    private final Gson gson = new Gson();

    @FXML
    private TextField searchBox;
    @FXML
    private VBox marketListContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button backButton;

    private List<ProductDTO> allMarkets = new ArrayList<>();

    public MarketDocumentController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadProducts();
        setupSearchBox();

        Platform.runLater(() -> {
            System.out.println("Controller initialized");
            loadProducts();
        });
    }

    private void loadProducts() {
        JsonObject response = serverConnection.sendRequest("getProductlist", null);

        if (response.has("Result") && response.get("Result").getAsString().equals("succeed")) {
            JsonArray productsArray = response.getAsJsonArray("products");
            ProductDTO[] products = gson.fromJson(productsArray, ProductDTO[].class);
            allMarkets = Arrays.asList(products); // Store all products
            populateProducts(allMarkets);
        } else {
            String errorMsg = response.has("message") ? response.get("message").getAsString() : "Unknown error";
            Utils.showAlert(Alert.AlertType.ERROR, "Error", errorMsg);
        }

    }

    private void populateProducts(List<ProductDTO> products) {
        marketListContainer.getChildren().clear();
        for (ProductDTO product : products) {
            HBox productItem = createProductItem(product);
            marketListContainer.getChildren().add(productItem);
        }
    }

    private HBox createProductItem(ProductDTO product) {
        HBox row = new HBox(10);
        row.getStyleClass().add("product-row");

        CheckBox selectBox = new CheckBox();
        selectBox.setUserData(product); // Store product info

        Label nameLabel = new Label(product.getProductName());
        Label priceLabel = new Label("$" + product.getProductPrice());
        Label dateLabel = new Label(product.getmanufacture_date());

        row.getChildren().addAll(selectBox, nameLabel, priceLabel, dateLabel);
        return row;
    }

    private void setupSearchBox() {
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            filterMarketList(newValue.toLowerCase());
        });
    }

    private void filterMarketList(String searchTerm) {
        List<ProductDTO> filteredMarkets = new ArrayList<>();
        for (ProductDTO product : allMarkets) {
            if (product.getProductName().toLowerCase().contains(searchTerm)) {
                filteredMarkets.add(product);
            }
        }
        populateProducts(filteredMarkets);
    }

    @FXML
    public void handleAddToWishlist() {
        List<ProductDTO> selectedProducts = new ArrayList<>();
        for (Node node : marketListContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                if (!row.getChildren().isEmpty() && row.getChildren().get(0) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) row.getChildren().get(0);
                    if (checkBox.isSelected()) {
                        System.out.println(checkBox.getUserData());
                        ProductDTO product = (ProductDTO) checkBox.getUserData();
                        JsonObject jsonResponse = serverConnection.sendRequest("addWish", product.getProductId());
                        selectedProducts.add(product);
                        String result = jsonResponse.get("Result").getAsString();
                        if (result.equals("succeed")) {
                            
                            Utils.showAlert(Alert.AlertType.INFORMATION, "Adding Wish", "Your wish added successfully.");
                            
                        } else {
                            Utils.showAlert(Alert.AlertType.ERROR, "Adding Wish", "Failed to add the product to your wishlist. Please try again.");
                        }     
                        checkBox.setSelected(false);
                    }
                }
            }
        }

    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        LoadScenes.loadHomeScene();
    }
}
