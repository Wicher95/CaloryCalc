package pl.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import pl.myapplication.R;
import pl.myapplication.databse.entities.Product;

public class ArrayAdapterHelper extends ArrayAdapter<Product> {

    private OnItemClickListener listener;

    public ArrayAdapterHelper(@NonNull Context context, @NonNull List<Product> objects, OnItemClickListener onItemClickListener) {
        super(context, 0, objects);
        this.listener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Product item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Product product = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_array, parent, false);
        }
        // Lookup view for data population
        TextView pName = convertView.findViewById(R.id.pName);
        TextView pCalories = convertView.findViewById(R.id.pCalories);
        // Populate the data into the template view using the data object
        if(product != null) {
            pName.setText(product.getName());
            String calories = product.getCalories() + " kcal";
            pCalories.setText(calories);
        }
        convertView.setOnClickListener(v -> listener.onItemClick(product));
        // Return the completed view to render on screen
        return convertView;
    }

}
