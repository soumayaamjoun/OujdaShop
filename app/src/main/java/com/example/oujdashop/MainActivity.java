package com.example.oujdashop;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseHelper dbHelper;
    private ArrayList<Category> categories;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuration de la Toolbar (Action Bar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listViewCategories);
        
        // Chargement des catégories
        loadCategories();
        
        // Navigation vers ProductActivity lors du clic sur une catégorie
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            intent.putExtra("category", categories.get(position).getName());
            startActivity(intent);
        });

        // Gestion du clic long : Modifier ou supprimer une catégorie
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(position);
            return true;
        });
    }

    private void loadCategories() {
        categories = dbHelper.getAllCategories();
        if (adapter == null) {
            adapter = new CategoryAdapter(this, categories);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(categories);
            adapter.notifyDataSetChanged();
        }
    }

    private void showOptionsDialog(final int position) {
        String[] options = {"Modifier", "Supprimer"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showEditCategoryDialog(position);
            } else {
                showDeleteConfirmationDialog(position);
            }
        });
        builder.show();
    }

    private void showEditCategoryDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier la catégorie");

        final EditText input = new EditText(this);
        input.setText(categories.get(position).getName());
        builder.setView(input);

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String newName = input.getText().toString();
            if (!newName.isEmpty()) {
                if (dbHelper.updateCategory(categories.get(position).getId(), newName)) {
                    loadCategories();
                    Toast.makeText(MainActivity.this, "Catégorie modifiée", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Voulez-vous vraiment supprimer cette catégorie ?")
            .setPositiveButton("Oui", (dialog, which) -> {
                if (dbHelper.deleteCategory(categories.get(position).getName())) {
                    loadCategories();
                    Toast.makeText(MainActivity.this, "Catégorie supprimée", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Non", null)
            .show();
    }

    // Affichage du menu d'options dans l'Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Gestion du clic sur les éléments du menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_scan) {
            startActivity(new Intent(this, ScannerActivity.class));
            return true;
        } else if (itemId == R.id.action_add_category) {
            showAddCategoryDialog();
            return true;
        } else if (itemId == R.id.action_profile) {
            startActivity(new Intent(this, UserActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nouvelle catégorie");

        final EditText input = new EditText(this);
        input.setHint("Nom de la catégorie");
        input.setPadding(20, 20, 20, 20);
        builder.setView(input);

        builder.setPositiveButton("Ajouter", null); // On définit le listener plus tard
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String categoryName = input.getText().toString().trim();
                
                if (categoryName.isEmpty()) {
                    input.setError("Veuillez entrer un nom");
                    return;
                }

                if (categoryName.length() < 3) {
                    input.setError("Le nom doit contenir au moins 3 caractères");
                    return;
                }

                // Vérifier si la catégorie existe déjà
                boolean categoryExists = false;
                for (Category category : categories) {
                    if (category.getName().equalsIgnoreCase(categoryName)) {
                        categoryExists = true;
                        break;
                    }
                }

                if (categoryExists) {
                    input.setError("Cette catégorie existe déjà");
                    return;
                }

                if (dbHelper.addCategory(categoryName)) {
                    // Recharger la liste
                    categories = dbHelper.getAllCategories();
                    adapter.clear();
                    adapter.addAll(categories);
                    adapter.notifyDataSetChanged();
                    
                    Toast.makeText(MainActivity.this, "Catégorie ajoutée avec succès", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de l'ajout de la catégorie", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
