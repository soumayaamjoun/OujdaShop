package com.example.oujdashop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        super(context, R.layout.item_category, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_category, null);
            
            holder = new ViewHolder();
            holder.imageViewIcon = convertView.findViewById(R.id.imageViewIcon);
            holder.textViewName = convertView.findViewById(R.id.textViewCategoryName);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = categories.get(position);
        holder.textViewName.setText(category.getName());
        holder.imageViewIcon.setImageResource(category.getImageResId());

        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewIcon;
        TextView textViewName;
    }
}
