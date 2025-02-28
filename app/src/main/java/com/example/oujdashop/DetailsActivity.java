package com.example.oujdashop;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";
    
    private ImageView productImage;
    private TextView productName, productPrice, productDescription, productCategory;
    private Product currentProduct;
    private DatabaseHelper dbHelper;
    private RadioGroup radioGroupSize;
    private RadioGroup radioGroupColor;
    private CheckBox checkBoxExpressDelivery;
    private CheckBox checkBoxGiftWrap;
    private Switch switchFavorite;
    private Switch switchNotify;
    private LinearLayout layoutSize, layoutColor;
    private String selectedSize = "";
    private String selectedColor = "";
    private boolean isExpressDelivery = false;
    private boolean isGiftWrap = false;
    private double expressDeliveryCost = 30.0;
    private double giftWrapCost = 15.0;

    // Définir les tailles standard
    private static final String[] SIZES_CLOTHES = {"XS", "S", "M", "L", "XL", "XXL"};
    private static final String[] COLORS = {"Noir", "Blanc", "Bleu", "Rouge", "Vert"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initializeViews();
        setupToolbar();
        loadProductData();
        setupListeners();
    }

    private void initializeViews() {
        // Initialize views
        productImage = findViewById(R.id.imageViewProductDetail);
        productName = findViewById(R.id.textViewProductNameDetail);
        productPrice = findViewById(R.id.textViewProductPriceDetail);
        productDescription = findViewById(R.id.textViewProductDescriptionDetail);
        productCategory = findViewById(R.id.textViewProductCategoryDetail);
        radioGroupSize = findViewById(R.id.radioGroupSize);
        radioGroupColor = findViewById(R.id.radioGroupColor);
        checkBoxExpressDelivery = findViewById(R.id.checkBoxExpressDelivery);
        checkBoxGiftWrap = findViewById(R.id.checkBoxGiftWrap);
        switchFavorite = findViewById(R.id.switchFavorite);
        switchNotify = findViewById(R.id.switchNotify);
        layoutSize = findViewById(R.id.layoutSize);
        layoutColor = findViewById(R.id.layoutColor);

        dbHelper = new DatabaseHelper(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Détails du produit");
        }
    }

    private void loadProductData() {
        currentProduct = getProductFromIntent();
        if (currentProduct != null) {
            Log.d(TAG, "Product loaded: " + currentProduct.getName());
            displayProductDetails();
            setupProductOptions();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(currentProduct.getName());
            }
        } else {
            Log.e(TAG, "Failed to load product data");
            Toast.makeText(this, "Erreur lors du chargement du produit", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupListeners() {
        // Gérer la sélection de taille
        radioGroupSize.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            if (selectedButton != null) {
                selectedSize = selectedButton.getText().toString();
                updateProductSummary();
            }
        });

        // Gérer la sélection de couleur
        radioGroupColor.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            if (selectedButton != null) {
                selectedColor = selectedButton.getText().toString();
                updateProductSummary();
            }
        });

        // Gérer la livraison express
        checkBoxExpressDelivery.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isExpressDelivery = isChecked;
            updateProductSummary();
        });

        // Gérer l'emballage cadeau
        checkBoxGiftWrap.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isGiftWrap = isChecked;
            updateProductSummary();
        });

        // Gérer l'ajout aux favoris
        switchFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                addToFavorites();
            } else {
                removeFromFavorites();
            }
        });

        // Gérer les notifications
        switchNotify.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableNotifications();
            } else {
                disableNotifications();
            }
        });
    }

    private Product getProductFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                int productId = extras.getInt("product_id", -1);
                if (productId != -1) {
                    // Charger le produit depuis la base de données
                    Product product = dbHelper.getProductById(productId);
                    if (product != null) {
                        Log.d(TAG, "Product loaded successfully: " + product.getName());
                        return product;
                    } else {
                        Log.e(TAG, "Product not found in database for ID: " + productId);
                    }
                } else {
                    Log.e(TAG, "Invalid product ID from intent");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading product: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "No extras found in intent");
        }
        return null;
    }

    private void displayProductDetails() {
        if (currentProduct == null) {
            Log.e(TAG, "Cannot display details: product is null");
            Toast.makeText(this, "Erreur: Impossible de charger les détails du produit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Afficher l'image
        try {
            if (currentProduct.getImageUri() != null && !currentProduct.getImageUri().isEmpty()) {
                Uri imageUri = Uri.parse(currentProduct.getImageUri());
                productImage.setImageURI(imageUri);
                
                // Vérifier si l'image a été chargée correctement
                if (productImage.getDrawable() == null) {
                    Log.w(TAG, "Failed to load image from URI, using resource ID");
                    if (currentProduct.getImageResId() != 0) {
                        productImage.setImageResource(currentProduct.getImageResId());
                    } else {
                        productImage.setImageResource(R.drawable.placeholder_image);
                    }
                }
            } else if (currentProduct.getImageResId() != 0) {
                productImage.setImageResource(currentProduct.getImageResId());
            } else {
                Log.w(TAG, "No image available, using placeholder");
                productImage.setImageResource(R.drawable.placeholder_image);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading product image: " + e.getMessage());
            productImage.setImageResource(R.drawable.placeholder_image);
        }
        
        // Afficher les informations textuelles
        try {
            productName.setText(currentProduct.getName());
            productPrice.setText(String.format("%.2f DH", currentProduct.getPrice()));
            productDescription.setText(currentProduct.getDescription());
            productCategory.setText("Catégorie : " + currentProduct.getCategory());
            
            Log.d(TAG, "Product details displayed successfully");
            Log.d(TAG, "Name: " + currentProduct.getName());
            Log.d(TAG, "Price: " + currentProduct.getPrice());
            Log.d(TAG, "Category: " + currentProduct.getCategory());
        } catch (Exception e) {
            Log.e(TAG, "Error displaying product details: " + e.getMessage());
            Toast.makeText(this, "Erreur lors de l'affichage des détails", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupProductOptions() {
        // Gestion de la visibilité des options de taille
        boolean isClothing = "Vêtements".equals(currentProduct.getCategory());
        layoutSize.setVisibility(isClothing ? View.VISIBLE : View.GONE);

        // Si c'est un vêtement, ajouter les options de taille
        if (isClothing) {
            radioGroupSize.removeAllViews();
            for (String size : SIZES_CLOTHES) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(size);
                radioButton.setId(View.generateViewId());
                radioGroupSize.addView(radioButton);
            }
        }

        // Gestion de la visibilité des options de couleur
        boolean hasColorOptions = isClothing || "Accessoires".equals(currentProduct.getCategory());
        layoutColor.setVisibility(hasColorOptions ? View.VISIBLE : View.GONE);

        // Si le produit a des options de couleur, les ajouter
        if (hasColorOptions) {
            radioGroupColor.removeAllViews();
            for (String color : COLORS) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(color);
                radioButton.setId(View.generateViewId());
                radioGroupColor.addView(radioButton);
            }
        }
    }

    private void addToFavorites() {
        try {
            // Ajouter aux favoris dans la base de données
            Toast.makeText(this, "Produit ajouté aux favoris", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error adding to favorites: " + e.getMessage());
            switchFavorite.setChecked(false);
            Toast.makeText(this, "Erreur lors de l'ajout aux favoris", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromFavorites() {
        try {
            // Retirer des favoris dans la base de données
            Toast.makeText(this, "Produit retiré des favoris", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error removing from favorites: " + e.getMessage());
            switchFavorite.setChecked(true);
            Toast.makeText(this, "Erreur lors du retrait des favoris", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableNotifications() {
        try {
            // Activer les notifications pour ce produit
            Toast.makeText(this, "Notifications activées pour ce produit", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error enabling notifications: " + e.getMessage());
            switchNotify.setChecked(false);
            Toast.makeText(this, "Erreur lors de l'activation des notifications", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableNotifications() {
        try {
            // Désactiver les notifications pour ce produit
            Toast.makeText(this, "Notifications désactivées pour ce produit", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error disabling notifications: " + e.getMessage());
            switchNotify.setChecked(true);
            Toast.makeText(this, "Erreur lors de la désactivation des notifications", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProductSummary() {
        StringBuilder summary = new StringBuilder();
        if (!selectedSize.isEmpty()) {
            summary.append("Taille: ").append(selectedSize);
        }
        if (!selectedColor.isEmpty()) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("Couleur: ").append(selectedColor);
        }
        
        // Calculer et afficher le prix total
        double totalPrice = currentProduct.getPrice();
        if (isExpressDelivery) {
            totalPrice += expressDeliveryCost;
        }
        if (isGiftWrap) {
            totalPrice += giftWrapCost;
        }
        
        // Construire le texte du prix avec les détails
        StringBuilder priceText = new StringBuilder(String.format("%.2f DH", totalPrice));
        if (isExpressDelivery || isGiftWrap) {
            priceText.append(" (");
            if (isExpressDelivery) {
                priceText.append("Livraison +").append(expressDeliveryCost).append(" DH");
            }
            if (isGiftWrap) {
                if (isExpressDelivery) priceText.append(", ");
                priceText.append("Emballage +").append(giftWrapCost).append(" DH");
            }
            priceText.append(")");
        }
        productPrice.setText(priceText.toString());
        
        // Afficher le résumé des sélections
        if (summary.length() > 0) {
            Toast.makeText(this, summary.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
