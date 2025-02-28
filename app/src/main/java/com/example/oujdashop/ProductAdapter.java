package com.example.oujdashop;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> products;
    private LayoutInflater inflater;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.imageViewProduct = convertView.findViewById(R.id.imageViewProduct);
            holder.textViewName = convertView.findViewById(R.id.textViewProductName);
            holder.textViewPrice = convertView.findViewById(R.id.textViewProductPrice);
            holder.textViewDescription = convertView.findViewById(R.id.textViewProductDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);
        
        // Afficher l'image
        if (product.getImageUri() != null && !product.getImageUri().isEmpty()) {
            // Charger l'image depuis l'URI
            try {
                Uri imageUri = Uri.parse(product.getImageUri());
                holder.imageViewProduct.setImageURI(imageUri);
            } catch (Exception e) {
                // En cas d'erreur, afficher l'image par défaut
                holder.imageViewProduct.setImageResource(product.getImageResId());
            }
        } else {
            // Utiliser l'image ressource par défaut
            holder.imageViewProduct.setImageResource(product.getImageResId());
        }

        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText(String.format("%.2f DH", product.getPrice()));
        holder.textViewDescription.setText(product.getDescription());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageViewProduct;
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewDescription;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }
} 