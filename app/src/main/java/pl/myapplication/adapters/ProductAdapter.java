package pl.myapplication.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.myapplication.R;
import pl.myapplication.application.CaloryCalc;
import pl.myapplication.databse.entities.Product;
import pl.myapplication.databse.entities.UserProduct;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private final List<UserProduct> userProducts;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onItemClick(UserProduct item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView caloriesValue;
        ImageView delete;
        ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            caloriesValue = itemView.findViewById(R.id.caloriesValue);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public ProductAdapter(Context context, List<UserProduct> userProducts, OnDeleteClickListener listener) {
        this.context = context;
        this.userProducts = userProducts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_product, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AsyncTask.execute(() -> {
            final UserProduct userProduct = userProducts.get(position);
            final Product product = CaloryCalc.getDatabase().productDao().getProductById(userProduct.getProductId());
            new Handler(Looper.getMainLooper()).post(() -> {
                holder.productName.setText(product.getName());
                String calories = product.getCalories() + " kcal";
                holder.caloriesValue.setText(calories);
                holder.delete.setOnClickListener(v -> listener.onItemClick(userProduct));
            });
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return userProducts.size();
    }
}
