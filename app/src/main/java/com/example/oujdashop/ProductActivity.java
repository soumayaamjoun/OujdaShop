package com.example.oujdashop;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import android.Manifest;
import android.content.pm.PackageManager;

public class ProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 2;
    private GridView gridViewProducts;
    private ProductAdapter adapter;
    private DatabaseHelper dbHelper;
    private String selectedCategory;
    private String selectedImageUri = null;
    private ImageView dialogImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Récupérer la catégorie depuis l'intent
        selectedCategory = getIntent().getStringExtra("category");

        // Configurer la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(selectedCategory);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialiser la GridView
        gridViewProducts = findViewById(R.id.gridViewProducts);
        dbHelper = new DatabaseHelper(this);
        
        // Charger les produits
        loadProducts();

        // Gérer le clic sur un produit
        gridViewProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product product = adapter.getItem(position);
            Intent intent = new Intent(ProductActivity.this, DetailsActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        // Gérer le clic long pour modifier/supprimer
        gridViewProducts.setOnItemLongClickListener((parent, view, position, id) -> {
            showProductOptionsDialog(adapter.getItem(position));
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_add_product) {
            showAddProductDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 et plus
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST);
            } else {
                openImagePicker();
            }
        } else {
            // Android 12 et moins
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission d'accès aux images refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);
        builder.setTitle("Ajouter un produit");

        // Initialiser les vues
        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextPrice = dialogView.findViewById(R.id.editTextPrice);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        dialogImageView = dialogView.findViewById(R.id.imageViewSelectedProduct);

        // Gérer la sélection d'image
        btnSelectImage.setOnClickListener(v -> checkPermissionAndPickImage());

        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String name = editTextName.getText().toString().trim();
            String priceStr = editTextPrice.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            boolean success = dbHelper.addProduct(name, price, description, selectedCategory, selectedImageUri);

            if (success) {
                Toast.makeText(this, "Produit ajouté avec succès", Toast.LENGTH_SHORT).show();
                loadProducts();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout du produit", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void showProductOptionsDialog(Product product) {
        String[] options = {"Modifier", "Supprimer"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options du produit")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditProductDialog(product);
                    } else {
                        showDeleteConfirmationDialog(product);
                    }
                });
        builder.create().show();
    }

    private void showEditProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);
        builder.setTitle("Modifier le produit");

        // Initialiser les vues
        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextPrice = dialogView.findViewById(R.id.editTextPrice);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        dialogImageView = dialogView.findViewById(R.id.imageViewSelectedProduct);

        // Pré-remplir les champs
        editTextName.setText(product.getName());
        editTextPrice.setText(String.format(Locale.getDefault(), "%.2f", product.getPrice()));
        editTextDescription.setText(product.getDescription());

        // Afficher l'image actuelle
        selectedImageUri = product.getImageUri();
        if (selectedImageUri != null && !selectedImageUri.isEmpty()) {
            dialogImageView.setImageURI(Uri.parse(selectedImageUri));
        } else {
            dialogImageView.setImageResource(product.getImageResId());
        }

        // Gérer la sélection d'image
        btnSelectImage.setOnClickListener(v -> checkPermissionAndPickImage());

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String name = editTextName.getText().toString().trim();
            String priceStr = editTextPrice.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            boolean success = dbHelper.updateProduct(product.getId(), name, price, description, selectedImageUri);

            if (success) {
                Toast.makeText(this, "Produit modifié avec succès", Toast.LENGTH_SHORT).show();
                loadProducts();
            } else {
                Toast.makeText(this, "Erreur lors de la modification du produit", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmer la suppression")
                .setMessage("Voulez-vous vraiment supprimer ce produit ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    if (dbHelper.deleteProduct(product.getId())) {
                        loadProducts();
                        Toast.makeText(this, "Produit supprimé avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression du produit", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void loadProducts() {
        ArrayList<Product> products = dbHelper.getProductsByCategory(selectedCategory);
        if (adapter == null) {
            adapter = new ProductAdapter(this, products);
            gridViewProducts.setAdapter(adapter);
        } else {
            adapter.updateProducts(products);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            selectedImageUri = imageUri.toString();
            
            // Copier l'image dans le stockage de l'application
            try {
                String fileName = "product_" + System.currentTimeMillis() + ".jpg";
                File destFile = new File(getFilesDir(), fileName);
                InputStream is = getContentResolver().openInputStream(imageUri);
                OutputStream os = new FileOutputStream(destFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                is.close();
                os.close();
                
                // Mettre à jour l'URI avec le chemin interne
                selectedImageUri = Uri.fromFile(destFile).toString();
                
                // Afficher l'image sélectionnée
                if (dialogImageView != null) {
                    dialogImageView.setImageURI(Uri.parse(selectedImageUri));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de la sauvegarde de l'image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
