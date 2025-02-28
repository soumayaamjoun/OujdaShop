package com.example.oujdashop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText name, email, password, confirmPassword;
    private Button register, mloginBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialisation des vues
        initializeViews();
        
        // Initialisation de la base de données
        dbHelper = new DatabaseHelper(this);

        // Configuration des boutons
        setupButtons();
    }

    private void initializeViews() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        register = findViewById(R.id.btn_register);
        mloginBtn = findViewById(R.id.btn_login);
    }

    private void setupButtons() {
        register.setOnClickListener(v -> handleRegistration());
        
        mloginBtn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegistration() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String userConfirmPass = confirmPassword.getText().toString().trim();

        // Validation des champs
        if (TextUtils.isEmpty(userName)) {
            name.setError("Veuillez entrer votre nom");
            return;
        }

        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Veuillez entrer votre email");
            return;
        }

        if (TextUtils.isEmpty(userPass)) {
            password.setError("Veuillez entrer votre mot de passe");
            return;
        }

        if (TextUtils.isEmpty(userConfirmPass)) {
            confirmPassword.setError("Veuillez confirmer votre mot de passe");
            return;
        }

        if (!userPass.equals(userConfirmPass)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Tentative d'inscription
            if (dbHelper.registerUser(userName, userEmail, userPass)) {
                Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                // Redirection vers la page de connexion
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "L'email existe déjà", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
