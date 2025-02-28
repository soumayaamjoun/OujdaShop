package com.example.oujdashop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "OujdaShop.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_PRODUCTS = "products";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    // Users Table Columns
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    // Categories Table Columns
    private static final String KEY_ICON = "icon_res_id";

    // Products Table Columns
    private static final String KEY_PRICE = "price";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_IMAGE = "image_res_id";
    private static final String KEY_BARCODE = "barcode";

    // Create table statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT NOT NULL,"
            + KEY_EMAIL + " TEXT UNIQUE NOT NULL,"
            + KEY_PASSWORD + " TEXT NOT NULL,"
            + "image_uri TEXT"
            + ")";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT UNIQUE NOT NULL,"
            + KEY_ICON + " INTEGER NOT NULL DEFAULT " + R.drawable.baseline_shopping_cart_24 + ")"
            + ")";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT NOT NULL,"
            + KEY_PRICE + " REAL NOT NULL,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_CATEGORY_ID + " INTEGER NOT NULL,"
            + KEY_IMAGE + " INTEGER NOT NULL DEFAULT " + R.drawable.baseline_shopping_cart_24 + ","
            + "image_uri TEXT,"
            + KEY_BARCODE + " TEXT UNIQUE,"
            + "FOREIGN KEY(" + KEY_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + ") ON DELETE CASCADE"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Supprimer les tables existantes si elles existent
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

            // Enable foreign key support
            db.execSQL("PRAGMA foreign_keys=ON");

            // Create users table
            db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "image_uri TEXT)");

            // Create categories table
            db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE NOT NULL, " +
                    "icon_res_id INTEGER NOT NULL DEFAULT " + R.drawable.baseline_shopping_cart_24 + ")");

            // Create products table with barcode
            db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "description TEXT, " +
                    "category_id INTEGER NOT NULL, " +
                    "image_res_id INTEGER NOT NULL DEFAULT " + R.drawable.baseline_shopping_cart_24 + ", " +
                    "image_uri TEXT, " +
                    "barcode TEXT UNIQUE, " +
                    "FOREIGN KEY (category_id) REFERENCES " + TABLE_CATEGORIES + "(id) ON DELETE CASCADE)");

            // Create indexes
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_categories_name ON " + TABLE_CATEGORIES + "(name)");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_products_category ON " + TABLE_PRODUCTS + "(category_id)");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_products_barcode ON " + TABLE_PRODUCTS + "(barcode)");
            
            // Ajouter les catégories par défaut
            ContentValues values = new ContentValues();
            
            // Catégorie Accessoires
            values.put("name", "Accessoires");
            values.put("icon_res_id", R.drawable.ic_accessories);
            long accessoiresId = db.insert("categories", null, values);
            values.clear();
            
            // Catégorie Vêtements
            values.put("name", "Vêtements");
            values.put("icon_res_id", R.drawable.ic_clothes);
            long vetementsId = db.insert("categories", null, values);
            values.clear();
            
            // Catégorie Cosmétique
            values.put("name", "Cosmétique");
            values.put("icon_res_id", R.drawable.ic_cosmetic);
            long cosmetiqueId = db.insert("categories", null, values);
            values.clear();
            
            // Catégorie Pharmacie
            values.put("name", "Pharmacie");
            values.put("icon_res_id", R.drawable.ic_pharmacy);
            long pharmacieId = db.insert("categories", null, values);
            values.clear();
            
            // Catégorie Nourriture
            values.put("name", "Nourriture");
            values.put("icon_res_id", R.drawable.ic_food);
            long nourritureId = db.insert("categories", null, values);
            values.clear();

            // Ajouter plus de produits par défaut avec codes-barres
            // Accessoires
            values.put("name", "Sac à main");
            values.put("price", 200.99);
            values.put("description", "Sac à main en cuir élégant");
            values.put("category_id", accessoiresId);
            values.put("image_res_id", R.drawable.sac_cuir);
            values.put("barcode", "8901234567890");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Montre femme");
            values.put("price", 299.99);
            values.put("description", "Montre élégante pour femme");
            values.put("category_id", accessoiresId);
            values.put("image_res_id", R.drawable.montre_femme);
            values.put("barcode", "8901234567895");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Ceinture en cuir");
            values.put("price", 149.99);
            values.put("description", "Ceinture en cuir véritable");
            values.put("category_id", accessoiresId);
            values.put("image_res_id", R.drawable.cceinture_cuir);
            values.put("barcode", "8901234567896");
            db.insert("products", null, values);
            values.clear();

            // Vêtements
            values.put("name", "Robe d'été");
            values.put("price", 199.99);
            values.put("description", "Robe légère pour l'été");
            values.put("category_id", vetementsId);
            values.put("image_res_id", R.drawable.robe_hijab);
            values.put("barcode", "8901234567891");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Jilbab moderne");
            values.put("price", 399.99);
            values.put("description", "Jilbab moderne et élégant");
            values.put("category_id", vetementsId);
            values.put("image_res_id", R.drawable.jilbab_moderne);
            values.put("barcode", "8901234567897");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Abaya casual");
            values.put("price", 299.99);
            values.put("description", "Abaya pour un style décontracté");
            values.put("category_id", vetementsId);
            values.put("image_res_id", R.drawable.abaya_casual);
            values.put("barcode", "8901234567898");
            db.insert("products", null, values);
            values.clear();

            // Cosmétique
            values.put("name", "Rouge à lèvres, Flormar");
            values.put("price", 89.99);
            values.put("description", "Rouge à lèvres longue tenue");
            values.put("category_id", cosmetiqueId);
            values.put("image_res_id", R.drawable.flormar);
            values.put("barcode", "8901234567892");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Palette de maquillage");
            values.put("price", 199.99);
            values.put("description", "Palette complète de maquillage");
            values.put("category_id", cosmetiqueId);
            values.put("image_res_id", R.drawable.palette_maquillage);
            values.put("barcode", "8901234567899");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Mascara waterproof");
            values.put("price", 79.99);
            values.put("description", "Mascara longue tenue waterproof");
            values.put("category_id", cosmetiqueId);
            values.put("image_res_id", R.drawable.mascara);
            values.put("barcode", "8901234567900");
            db.insert("products", null, values);
            values.clear();

            // Pharmacie
            values.put("name", "Vitamines C comprimé");
            values.put("price", 79.99);
            values.put("description", "Complément vitamine C");
            values.put("category_id", pharmacieId);
            values.put("image_res_id", R.drawable.vitaminc);
            values.put("barcode", "8901234567893");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "ACM crème Lavante");
            values.put("price", 149.99);
            values.put("description", "Crème lavante douce pour le visage et le corps");
            values.put("category_id", pharmacieId);
            values.put("image_res_id", R.drawable.acm_creme);
            values.put("barcode", "3760095253022");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Crème hydratante, ACM");
            values.put("price", 95.99);
            values.put("description", "Crème hydratante pour le visage");
            values.put("category_id", pharmacieId);
            values.put("image_res_id", R.drawable.acm_hydra);
            values.put("barcode", "8901234567901");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Gel antibactérien");
            values.put("price", 29.99);
            values.put("description", "Gel nettoyant antibactérien");
            values.put("category_id", pharmacieId);
            values.put("image_res_id", R.drawable.gel_antibacterien);
            values.put("barcode", "8901234567902");
            db.insert("products", null, values);
            values.clear();

            // Nourriture
            values.put("name", "Chocolat noir");
            values.put("price", 29.99);
            values.put("description", "Chocolat noir 70% cacao");
            values.put("category_id", nourritureId);
            values.put("image_res_id", R.drawable.chocolat_noir);
            values.put("barcode", "8901234567894");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Thé vert bio");
            values.put("price", 39.99);
            values.put("description", "Thé vert biologique marocain");
            values.put("category_id", nourritureId);
            values.put("image_res_id", R.drawable.the_vert);
            values.put("barcode", "8901234567903");
            db.insert("products", null, values);
            values.clear();

            values.put("name", "Huile d'argan");
            values.put("price", 89.99);
            values.put("description", "Huile d'argan pure bio");
            values.put("category_id", nourritureId);
            values.put("image_res_id", R.drawable.huile_argan);
            values.put("barcode", "8901234567904");
            db.insert("products", null, values);
            values.clear();

            Log.d("DatabaseHelper", "Database tables, categories and products created successfully");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean registerUser(String name, String email, String password) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            
            Cursor cursor = db.query("users", null, "email = ?", 
                new String[]{email}, null, null, null);
            
            if (cursor != null && cursor.getCount() > 0) {
                Log.e("DatabaseHelper", "User already exists with email: " + email);
                cursor.close();
                return false;
            }
            
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("email", email);
            values.put("password", password);
            
            long result = db.insert("users", null, values);
            Log.d("DatabaseHelper", "Register user result: " + result + " for email: " + email);
            
            if (result != -1) {
                cursor = db.query("users", null, "email = ?", 
                    new String[]{email}, null, null, null);
                boolean exists = cursor != null && cursor.getCount() > 0;
                Log.d("DatabaseHelper", "Verify after insert - user exists: " + exists);
                cursor.close();
            }
            
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            Log.d("DatabaseHelper", "Checking credentials for email: " + email);

            String[] columns = {"email", "password"};
            String selection = "email = ? AND password = ?";
            String[] selectionArgs = {email, password};

            cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
            
            boolean exists = cursor != null && cursor.getCount() > 0;
            Log.d("DatabaseHelper", "User authentication result: " + exists);
            
            return exists;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user credentials: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT id, name, icon_res_id FROM categories " +
                         "ORDER BY CASE name " +
                         "WHEN 'Accessoires' THEN 1 " +
                         "WHEN 'Vêtements' THEN 2 " +
                         "WHEN 'Cosmétique' THEN 3 " +
                         "WHEN 'Pharmacie' THEN 4 " +
                         "WHEN 'Nourriture' THEN 5 " +
                         "ELSE 6 END";
            
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    int iconResId = cursor.getInt(cursor.getColumnIndexOrThrow("icon_res_id"));
                    categories.add(new Category(id, name, iconResId));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    public boolean addCategory(String name) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            cursor = db.rawQuery("SELECT id FROM categories WHERE name = ? COLLATE NOCASE", 
                new String[]{name});

            if (cursor != null && cursor.getCount() > 0) {
                Log.e("DatabaseHelper", "Category already exists: " + name);
                return false;
            }

            int iconResId;
            switch (name.toLowerCase()) {
                case "vêtements":
                    iconResId = R.drawable.ic_clothes;
                    break;
                case "accessoires":
                    iconResId = R.drawable.ic_accessories;
                    break;
                case "nourriture":
                    iconResId = R.drawable.ic_food;
                    break;
                case "pharmacie":
                    iconResId = R.drawable.ic_pharmacy;
                    break;
                case "cosmétique":
                    iconResId = R.drawable.ic_cosmetic;
                    break;
                default:
                    iconResId = R.drawable.baseline_shopping_cart_24;
                    break;
            }

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("icon_res_id", iconResId);

            long result = db.insertOrThrow("categories", null, values);

            if (result != -1) {
                db.setTransactionSuccessful();
                Log.d("DatabaseHelper", "Category added successfully with ID: " + result);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding category: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) {
                if (db.inTransaction()) db.endTransaction();
            }
        }
    }

    public boolean updateCategory(int id, String name) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            int result = db.update("categories", values, "id=?", new String[]{String.valueOf(id)});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(String name) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete("categories", "name=?", new String[]{name});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Product> getProductsByCategory(String category) {
        ArrayList<Product> products = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT p.*, c.name as category_name FROM products p " +
                         "JOIN categories c ON p.category_id = c.id " +
                         "WHERE c.name = ?";
            
            Cursor cursor = db.rawQuery(query, new String[]{category});

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image_res_id"));
                    String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
                    
                    Product product = new Product(id, name, price, description, category);
                    product.setImageResId(imageResId);
                    product.setImageUri(imageUri);
                    products.add(product);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean addProduct(String name, double price, String description, String category, String imageUri) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            
            Cursor cursor = db.query("categories", new String[]{"id"}, "name=?", 
                new String[]{category}, null, null, null);
            
            if (cursor.moveToFirst()) {
                int categoryId = cursor.getInt(0);
                cursor.close();

                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("price", price);
                values.put("description", description);
                values.put("category_id", categoryId);
                if (imageUri != null && !imageUri.isEmpty()) {
                    values.put("image_uri", imageUri);
                }
                
                long result = db.insert("products", null, values);
                return result != -1;
            }
            cursor.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete("products", "id=?", new String[]{String.valueOf(id)});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            
            String[] columns = {"id", "name", "email", "image_uri"};
            String selection = "email = ?";
            String[] selectionArgs = {email};
            
            cursor = db.query("users", columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));

                User user = new User(id, name, userEmail, imageUri);
                Log.d("DatabaseHelper", "Found user: " + name + " with email: " + userEmail);
                return user;
            }
            Log.e("DatabaseHelper", "No user found for email: " + email);
            return null;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting user: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean updateUserProfile(String email, String newName) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", newName);

            int result = db.update("users", values, "email=?", new String[]{email});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, "id=?", 
            new String[]{String.valueOf(userId)}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
            user = new User(userId, name, email, imageUri);
        }
        cursor.close();
        return user;
    }

    public boolean updateUserImage(String email, String imageUri) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("image_uri", imageUri);

            int result = db.update("users", values, "email=?", new String[]{email});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            
            Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?",
                new String[]{email, oldPassword});
                
            if (cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put("password", newPassword);
                
                int result = db.update("users", values, "email=?", new String[]{email});
                cursor.close();
                return result > 0;
            }
            cursor.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            Log.d("DatabaseHelper", "Checking if user exists for email: " + email);
            
            cursor = db.query("users", 
                new String[]{"email"}, 
                "email = ?", 
                new String[]{email}, 
                null, null, null);

            boolean exists = cursor != null && cursor.getCount() > 0;
            Log.d("DatabaseHelper", "User exists check result: " + exists);
            return exists;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void debugUserTable(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query("users", null, null, null, null, null, null);

            Log.d("DatabaseHelper", "Total users in database: " + cursor.getCount());
            
            if (cursor.moveToFirst()) {
                do {
                    String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    Log.d("DatabaseHelper", "Found user - Email: " + userEmail + ", Name: " + name);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error debugging user table: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean updateProduct(int id, String name, double price, String description, String imageUri) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("price", price);
            values.put("description", description);
            if (imageUri != null && !imageUri.isEmpty()) {
                values.put("image_uri", imageUri);
            }
            
            int result = db.update("products", values, "id=?", new String[]{String.valueOf(id)});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Product product = null;

        try {
            String query = "SELECT p.*, c.name as category_name FROM products p " +
                         "JOIN categories c ON p.category_id = c.id " +
                         "WHERE p.id = ?";
            
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image_res_id"));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));

                product = new Product(id, name, price, description, category);
                product.setImageResId(imageResId);
                product.setImageUri(imageUri);
                
                Log.d("DatabaseHelper", "Product found: " + name + ", Category: " + category);
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting product by id: " + e.getMessage());
        }

        return product;
    }

    public Product getProductByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Product product = null;

        try {
            String query = "SELECT p.*, c.name as category_name FROM " + TABLE_PRODUCTS + " p " +
                         "JOIN " + TABLE_CATEGORIES + " c ON p.category_id = c.id " +
                         "WHERE p.barcode = ?";
            
            Cursor cursor = db.rawQuery(query, new String[]{barcode});

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image_res_id"));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));

                product = new Product(id, name, price, description, category);
                product.setImageResId(imageResId);
                product.setImageUri(imageUri);
                
                Log.d("DatabaseHelper", "Product found by barcode: " + name);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting product by barcode: " + e.getMessage());
        }

        return product;
    }
}
