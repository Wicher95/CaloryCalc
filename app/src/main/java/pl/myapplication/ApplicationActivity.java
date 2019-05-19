package pl.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.myapplication.adapters.ArrayAdapterHelper;
import pl.myapplication.adapters.ProductAdapter;
import pl.myapplication.application.CaloryCalc;
import pl.myapplication.databse.entities.Product;
import pl.myapplication.databse.entities.User;
import pl.myapplication.databse.entities.UserProduct;

public class ApplicationActivity extends AppCompatActivity {

    private User user;
    private Context mContext;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private TextView currentDay;
    private Date selectedDate;
    private Date nextDay;
    private List<UserProduct> products;
    private RelativeLayout addProductLayout;
    private RelativeLayout fabHolder;
    private FloatingActionButton fabAddProduct;
    private Button addProduct;
    private Button cancelAddProduct;
    private EditText productName;
    private EditText productCalories;
    private TextView calorySummary;
    private TextView motivationalTextHolder;
    private int userId;
    private int userCalories;

    private SearchView searchView;
    private ListView productsListView;
    private List<Product> productsList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        mContext = this;
        addProductLayout = findViewById(R.id.addProductLayout);
        addProductLayout.setVisibility(View.GONE);
        currentDay = findViewById(R.id.currentDay);
        fabHolder = findViewById(R.id.fabHolder);
        fabAddProduct = findViewById(R.id.fab);
        fabAddProduct.setOnClickListener(v -> addProductLayout.setVisibility(View.VISIBLE));
        addProduct = findViewById(R.id.buttonAddProduct);
        addProduct.setOnClickListener(v -> AddProduct());
        cancelAddProduct = findViewById(R.id.buttonCancelAddProduct);
        cancelAddProduct.setOnClickListener(v -> addProductLayout.setVisibility(View.GONE));
        productName = findViewById(R.id.productName);
        productCalories = findViewById(R.id.productCalories);

        calorySummary = findViewById(R.id.calorySummary);
        motivationalTextHolder = findViewById(R.id.motivationalText);

        searchView = findViewById(R.id.searchBox);
        productsListView = findViewById(R.id.productsListView);

        setTodayDate();
        userId = (int) getIntent().getLongExtra(MainActivity.USER_ID, 1);
        AsyncTask.execute(() -> {
            user = CaloryCalc.getDatabase().userDAO().getUserById(userId);
            setUserCaloriesNeed();
        });
        recyclerView = findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        setRecyclerView();
        updateCaloriesSummary();
        setSearchViewListener();

        AsyncTask.execute(() -> {
            productsList = CaloryCalc.getDatabase().productDao().retrieveProducts();
            new Handler(getMainLooper()).post(() -> setListViewValues(productsList));
        });

        // Setting on Touch Listener for handling the touch inside ScrollView
        productsListView.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }

    private void setTodayDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        selectedDate = c.getTime();
        c.add(Calendar.DATE, 1);
        nextDay = c.getTime();
        currentDay.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(selectedDate));
    }

    public void NextDay(View view) {
        Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);
        c.add(Calendar.DATE, 1);
        if (c.getTime().before(Calendar.getInstance().getTime())) {
            selectedDate = c.getTime();
            c.setTime(selectedDate);
            c.add(Calendar.DATE, 1);
            nextDay = c.getTime();
            currentDay.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(selectedDate));
            setRecyclerView();
        } else {
            Toast.makeText(mContext, "Nie możesz dodawać produktów z przyszłości", Toast.LENGTH_LONG).show();
        }
    }

    public void PreviousDay(View view) {
        Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);
        c.add(Calendar.DATE, -1);
        selectedDate = c.getTime();
        c.setTime(selectedDate);
        c.add(Calendar.DATE, 1);
        nextDay = c.getTime();
        currentDay.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(selectedDate));
        setRecyclerView();
    }

    private void setRecyclerView() {
        AsyncTask.execute(() -> {
            List<UserProduct> userProducts = CaloryCalc.getDatabase().userProductDAO().getUserProducts(userId);
            products = new ArrayList<>();
            for (UserProduct userProduct : userProducts) {
                if (userProduct.getDate().after(selectedDate) && userProduct.getDate().before(nextDay)) {
                    products.add(userProduct);
                }
            }
            new Handler(getMainLooper()).post(() -> setRecyclerViewData(products));
        });
    }

    private void AddProduct() {
        hideFab();
        if (!productCalories.getText().toString().equals("") && !productName.getText().toString().equals("")) {
            Product product = new Product();
            product.setName(productName.getText().toString());
            product.setCalories(Float.valueOf(productCalories.getText().toString()));
            AsyncTask.execute(() -> {
                int id = (int) CaloryCalc.getDatabase().productDao().addProduct(product);
                UserProduct userProduct = new UserProduct();
                userProduct.setProductId(id);
                userProduct.setUserId(userId);
                Calendar c = Calendar.getInstance();
                c.setTime(selectedDate);
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                c.add(Calendar.HOUR_OF_DAY, hour);
                userProduct.setDate(c.getTime());
                int userProductId = (int) CaloryCalc.getDatabase().userProductDAO().addUserProduct(userProduct);
                userProduct.setId(userProductId);
                new Handler(getMainLooper()).post(() -> {
                    productCalories.setText("");
                    productName.setText("");
                    searchView.setQuery("",false);
                    products.add(userProduct);
                    updateCaloriesSummary();
                    productAdapter.notifyDataSetChanged();
                    addProductLayout.setVisibility(View.GONE);
                    showFab();
                });
                productsList = CaloryCalc.getDatabase().productDao().retrieveProducts();
            });
        } else {
            Toast.makeText(mContext, "Musisz wypełnić wszystkie pola", Toast.LENGTH_LONG).show();
        }
    }

    private void setRecyclerViewData(List<UserProduct> products) {
        productAdapter = new ProductAdapter(mContext, products, item -> buildAlertDialog(item, productAdapter));
        recyclerView.setAdapter(productAdapter);
        updateCaloriesSummary();
    }

    private void buildAlertDialog(final UserProduct userProduct, ProductAdapter productAdapter) {
        AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setTitle(CaloryCalc.getInstance().getResources().getString(R.string.delete_product));
        dialog.setMessage(CaloryCalc.getInstance().getResources().getString(R.string.delete_product_message));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                CaloryCalc.getInstance().getResources().getString(R.string.yes),
                (dialog1, which) -> {
                    AsyncTask.execute(() -> {
                        CaloryCalc.getDatabase().userProductDAO().deleteUserProduct(userProduct);
                        products.remove(userProduct);
                        new Handler(getMainLooper()).post(() -> {
                            updateCaloriesSummary();
                            productAdapter.notifyDataSetChanged();
                            dialog1.dismiss();
                        });
                    });
                });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                CaloryCalc.getInstance().getResources().getString(R.string.cancel),
                (dialog1, which) -> {
                    dialog1.dismiss();
                    productCalories.setText("");
                    productName.setText("");
                    searchView.setQuery("",false);
                });
        dialog.show();
    }

    private void updateCaloriesSummary() {
        AsyncTask.execute(() -> {
            float eatenCalories = 0;
            for (UserProduct userProduct : products) {
                Product product = CaloryCalc.getDatabase().productDao().getProductById(userProduct.getProductId());
                eatenCalories += product.getCalories();
            }
            float finalEatenCalories = eatenCalories;
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String summary = finalEatenCalories + " / " + userCalories + " kcal";
                    calorySummary.setText(summary);
                    String motivationalText;
                    if (finalEatenCalories > userCalories + 100) {
                        if (user.getGender().equals("Kobieta"))
                            motivationalText = "Znów zjadłaś za dużo. Ty grubasku :)";
                        else
                            motivationalText = "Znów zjadłeś za dużo. Ty grubasku :)";
                        motivationalTextHolder.setTextColor(Color.RED);
                    } else if (finalEatenCalories < userCalories - 100) {
                        if (user.getGender().equals("Kobieta"))
                            motivationalText = "Powinnaś coś jeszcze zjeść";
                        else
                            motivationalText = "Powinieneś coś jeszcze zjeść";
                        motivationalTextHolder.setTextColor(Color.MAGENTA);
                    } else {
                        if (user.getGender().equals("Kobieta"))
                            motivationalText = "Idealnie! Gratulacje ;)";
                        else
                            motivationalText = "Idealnie! Gratulacje ;)";
                        motivationalTextHolder.setTextColor(Color.GREEN);
                    }
                    motivationalTextHolder.setText(motivationalText);
                }
            });
        });
    }

    private void setSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    List<Product> listFound = new ArrayList<>();
                    for (Product product : productsList) {
                        if (product.getName().toLowerCase().contains(newText.toLowerCase())) {
                            listFound.add(product);
                        }
                    }
                    setListViewValues(listFound);
                } else if (newText != null) {
                    setListViewValues(productsList);
                }
                return true;
            }
        });
    }

    private void setListViewValues(List<Product> productsList) {
        ArrayAdapterHelper adapter = new ArrayAdapterHelper(mContext, productsList, product -> {
            AsyncTask.execute(() -> {
                UserProduct userProduct = new UserProduct();
                userProduct.setProductId(product.getId());
                userProduct.setUserId(userId);
                Calendar c = Calendar.getInstance();
                c.setTime(selectedDate);
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                c.add(Calendar.HOUR_OF_DAY, hour);
                userProduct.setDate(c.getTime());
                int userProductId = (int) CaloryCalc.getDatabase().userProductDAO().addUserProduct(userProduct);
                userProduct.setId(userProductId);
                new Handler(getMainLooper()).post(() -> {
                    searchView.setQuery("",false);
                    products.add(userProduct);
                    updateCaloriesSummary();
                    productAdapter.notifyDataSetChanged();
                    addProductLayout.setVisibility(View.GONE);
                    showFab();
                });
            });
        });
        productsListView.setAdapter(adapter);
    }

    private void setUserData() {
        String text = "Użytkownik: " + user.getName() + "\nPłeć: " + user.getGender() + "\nWiek: " + user.getAge() + "\nWaga: " + user.getWeight() + "kg"
                + "\nTryb życia: " + user.getLifestyle();
        //((TextView)findViewById(R.id.userData)).setText(text);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private void hideFab() {
        fabHolder.setVisibility(View.GONE);
    }

    private void showFab() {
        fabHolder.setVisibility(View.VISIBLE);
    }

    private void setUserCaloriesNeed()
    {
        if(user.getAge() <= 9)
        {
            if(user.getAge() <= 3)
                userCalories = 1300;
            else if(user.getAge() <= 6)
                userCalories = 1700;
            else if(user.getAge() <= 9)
                userCalories = 2100;
        }
        else if(user.getGender().equals("Kobieta"))
        {
            if(user.getAge() <= 20) {
                if(user.getAge() <= 12) {
                    userCalories = 2300;
                } else if(userCalories <= 15) {
                    userCalories = 2700;
                } else {
                    userCalories = 2600;
                }
            } else {
                if(user.getAge() <= 59)
                {
                    switch (user.getLifestyle())
                    {
                        case "Siedzący, brak aktywności fizycznej":
                            userCalories = 2200;
                            break;
                        case "Siedzący, niewielka aktywność fizyczna":
                            userCalories = 2600;
                            break;
                        case "Praca/Wysiłek fizyczny":
                            userCalories = 2900;
                            break;
                        case "Duży wysiłek fizyczny":
                            userCalories = 3200;
                            break;
                    }
                } else if (user.getAge() <= 75) {
                    userCalories = 2200;
                } else {
                    userCalories = 2000;
                }
            }
        }
        else
        {
            if(user.getAge() <= 20) {
                if(user.getAge() <= 12) {
                    userCalories = 2600;
                } else if(userCalories <= 15) {
                    userCalories = 3150;
                } else {
                    userCalories = 3500;
                }
            } else {
                if(user.getAge() <= 64)
                {
                    switch (user.getLifestyle())
                    {
                        case "Siedzący, brak aktywności fizycznej":
                            userCalories = 2500;
                            break;
                        case "Siedzący, niewielka aktywność fizyczna":
                            userCalories = 3100;
                            break;
                        case "Praca/Wysiłek fizyczny":
                            userCalories = 3800;
                            break;
                        case "Duży wysiłek fizyczny":
                            userCalories = 4350;
                            break;
                    }
                } else if (user.getAge() <= 75) {
                    userCalories = 2300;
                } else {
                    userCalories = 2100;
                }
            }
        }
    }
}




