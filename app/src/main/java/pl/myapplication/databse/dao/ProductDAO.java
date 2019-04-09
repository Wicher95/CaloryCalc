package pl.myapplication.databse.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import pl.myapplication.databse.entities.Product;

@Dao
public interface ProductDAO {
    @Insert
    void addProduct(Product product);

    @Update
    void updateUser(Product product);

    @Delete
    void deleteUser(Product product);

    @Query("SELECT * FROM products")
    List<Product> retrieveProducts();

    @Query("SELECT * FROM products WHERE id=:productId")
    Product getProductsById(final int productId);
}
