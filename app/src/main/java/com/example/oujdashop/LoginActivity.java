package com.example.oujdashop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText password;
    private EditText email;
    private Button login, register;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.btn_login);
        register = findViewById(R.id.btn_register);

        login.setOnClickListener(v -> handleLogin());

        register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void handleLogin() {
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        Log.d("LoginActivity", "Attempting login with email: " + userEmail);

        if (dbHelper.checkUser(userEmail, userPass)) {
            try {
                // Sauvegarder l'email dans les préférences
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_email", userEmail);
                boolean saved = editor.commit();

                Log.d("LoginActivity", "Email saved in preferences: " + userEmail);

                // Vérification simple que l'email a été sauvegardé
                String savedEmail = prefs.getString("user_email", "");
                Log.d("LoginActivity", "Retrieved saved email: " + savedEmail);

                if (savedEmail.isEmpty()) {
                    Log.e("LoginActivity", "Failed to save email in preferences");
                    Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } catch (Exception e) {
                Log.e("LoginActivity", "Error during login: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de la connexion", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }
    }
}
