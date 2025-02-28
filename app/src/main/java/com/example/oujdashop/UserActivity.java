package com.example.oujdashop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class UserActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    
    // Vues pour les informations personnelles
    private EditText etNom, etPrenom, etEmail;
    private ImageView imgProfile;
    private Button btnUpdate, btnChangePassword, btnChangePhoto;
    
    // Vues pour les préférences
    private Switch switchNotifications;
    private RadioGroup radioGroupTheme;
    private RadioButton rbLight, rbDark;
    
    // Données de l'utilisateur
    private DatabaseHelper dbHelper;
    private String userEmail;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialisation
        initializeDatabase();
        initializeViews();
        setupActionBar();
        loadUserData();
        setupListeners();
    }

    private void initializeDatabase() {
        try {
            dbHelper = new DatabaseHelper(this);
            // Récupérer l'email de l'utilisateur connecté
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            userEmail = prefs.getString("user_email", "");
            
            Log.d("UserActivity", "Retrieved email from preferences: " + userEmail);

            if (userEmail.isEmpty()) {
                Log.e("UserActivity", "No email found in preferences");
                showError("Utilisateur non connecté");
                finish();
                return;
            }

            // Vérifier si l'utilisateur existe dans la base de données
            currentUser = dbHelper.getUserByEmail(userEmail);
            if (currentUser == null) {
                Log.e("UserActivity", "User not found in database for email: " + userEmail);
                // Effacer les préférences car l'utilisateur n'existe pas
                prefs.edit().clear().apply();
                showError("Utilisateur non trouvé");
                finish();
            } else {
                Log.d("UserActivity", "User found successfully: " + currentUser.getName());
            }
        } catch (Exception e) {
            Log.e("UserActivity", "Error in initializeDatabase: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur d'initialisation");
            finish();
        }
    }

    private void initializeViews() {
        // Informations personnelles
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etEmail = findViewById(R.id.etEmail);
        imgProfile = findViewById(R.id.imgProfile);
        
        // Boutons
        btnUpdate = findViewById(R.id.btnUpdate);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        
        // Préférences
        switchNotifications = findViewById(R.id.switchNotifications);
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        rbLight = findViewById(R.id.rbLight);
        rbDark = findViewById(R.id.rbDark);
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profil");
        }
    }

    private void setupListeners() {
        // Boutons principaux
        btnUpdate.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnChangePhoto.setOnClickListener(v -> selectImageFromGallery());

        // Préférences
        setupPreferencesListeners();
    }

    private void setupPreferencesListeners() {
        // Notifications
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            showMessage(isChecked ? "Notifications activées" : "Notifications désactivées");
        });

        // Thème
        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbLight) {
                applyTheme(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.rbDark) {
                applyTheme(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });
    }

    private void loadUserData() {
        try {
            if (currentUser == null) {
                Log.e("UserActivity", "Attempting to load data for null user");
                return;
            }

            Log.d("UserActivity", "Loading data for user: " + currentUser.getEmail());
            
            // Afficher les informations personnelles
            displayUserInfo();
            
            // Charger les préférences
            loadUserPreferences();
            
        } catch (Exception e) {
            Log.e("UserActivity", "Error loading user data: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement des données");
        }
    }

    private void displayUserInfo() {
        try {
            // Nom et prénom
            String fullName = currentUser.getName();
            Log.d("UserActivity", "Displaying user info - Full name: " + fullName);
            
            String[] nameParts = fullName.split(" ", 2);
            etNom.setText(nameParts.length > 0 ? nameParts[0] : "");
            etPrenom.setText(nameParts.length > 1 ? nameParts[1] : "");
            etEmail.setText(currentUser.getEmail());

            // Photo de profil
            loadProfileImage();
        } catch (Exception e) {
            Log.e("UserActivity", "Error displaying user info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadProfileImage() {
        String imageUri = currentUser.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri));
                imgProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("UserActivity", "Error loading profile image", e);
                imgProfile.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }

    private void loadUserPreferences() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        
        // Notifications
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);
        switchNotifications.setChecked(notificationsEnabled);

        // Thème
        int currentTheme = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        rbLight.setChecked(currentTheme == AppCompatDelegate.MODE_NIGHT_NO);
        rbDark.setChecked(currentTheme == AppCompatDelegate.MODE_NIGHT_YES);
    }

    private void updateProfile() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();

        if (nom.isEmpty() || prenom.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        String fullName = nom + " " + prenom;
        if (dbHelper.updateUserProfile(userEmail, fullName)) {
            showMessage("Profil mis à jour avec succès");
            loadUserData();
        } else {
            showError("Erreur lors de la mise à jour");
        }
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            handleImageSelection(data.getData());
        }
    }

    private void handleImageSelection(Uri sourceUri) {
        try {
            // Copier l'image et obtenir le nouvel URI
            String destinationUri = copyImageToInternalStorage(sourceUri);
            if (destinationUri == null) {
                showError("Erreur lors de la copie de l'image");
                return;
            }

            // Mettre à jour l'affichage
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sourceUri);
            imgProfile.setImageBitmap(bitmap);

            // Sauvegarder dans la base de données
            if (dbHelper.updateUserImage(userEmail, destinationUri)) {
                showMessage("Photo de profil mise à jour");
            } else {
                showError("Erreur lors de la sauvegarde de l'image");
            }
        } catch (Exception e) {
            Log.e("UserActivity", "Error handling image selection", e);
            showError("Erreur lors du traitement de l'image");
        }
    }

    private String copyImageToInternalStorage(Uri sourceUri) {
        try {
            String fileName = "profile_" + userEmail + ".jpg";
            File destinationFile = new File(getFilesDir(), fileName);
            
            InputStream is = getContentResolver().openInputStream(sourceUri);
            OutputStream os = new FileOutputStream(destinationFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            
            os.flush();
            os.close();
            is.close();
            
            return Uri.fromFile(destinationFile).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier le mot de passe")
               .setView(dialogView)
               .setPositiveButton("Modifier", null) // On définit le listener plus tard
               .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String oldPassword = etOldPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showError("Veuillez remplir tous les champs");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showError("Les nouveaux mots de passe ne correspondent pas");
                    return;
                }

                if (dbHelper.updatePassword(userEmail, oldPassword, newPassword)) {
                    showMessage("Mot de passe modifié avec succès");
                    dialog.dismiss();
                } else {
                    showError("Ancien mot de passe incorrect");
                }
            });
        });

        dialog.show();
    }

    private void applyTheme(int themeMode) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().putInt("theme_mode", themeMode).apply();
        AppCompatDelegate.setDefaultNightMode(themeMode);
        recreate();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
