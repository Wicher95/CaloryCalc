package pl.myapplication.databse.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import pl.myapplication.databse.entities.Product;
import pl.myapplication.databse.entities.UserProduct;

@Dao
public interface ProductDAO {
    @Insert
    long addProduct(Product product);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void addAllProducts(List<Product> products);

    @Update
    void updateUser(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("SELECT * FROM products")
    List<Product> retrieveProducts();

    @Query("SELECT * FROM products WHERE id=:productId")
    Product getProductById(final int productId);
}
