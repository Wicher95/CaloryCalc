package pl.myapplication.databse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pl.myapplication.R;
import pl.myapplication.application.CaloryCalc;
import pl.myapplication.databse.entities.Product;

public class DataGenerator {

    public static List<Product> generateProducts()
    {
        List<Product> products = new ArrayList<>();
        String[] productsArray = CaloryCalc.getInstance().getResources().getStringArray(R.array.products_array);
        for(String product : productsArray)
        {
            String[] values = product.split(";");
            try {
                Product p = new Product();
                p.setName(values[1]);
                p.setCalories(Float.valueOf(values[2]));
                products.add(p);
            } catch (IndexOutOfBoundsException e) {
                Log.e("DataGenerator", e.getMessage());
            } catch (NumberFormatException e) {
                Log.e("DataGenerator", e.getMessage());
            }
        }
        return products;
    }
}
