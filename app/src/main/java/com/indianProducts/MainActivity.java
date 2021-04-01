package com.indianProducts;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import customAdaptors.MyListAdaptor;
import productDataBase.DataBaseAccess;
import resources.Constants;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Constants {

    private ListView productDetailsListView;
    private Spinner productTypeDropDown;
    private AutoCompleteTextView searchProductTypeEditText;
    private static long backButtonPressedTime;
    private Toast backToast;
    public DataBaseAccess dataBaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        productTypeDropDown = findViewById(R.id.productTypeDropDown);
        productDetailsListView = findViewById(R.id.productDetails);
        searchProductTypeEditText = findViewById(R.id.searchProductType);
        ImageButton barCodeScanner = findViewById(R.id.barCodeScanner);
        ImageButton recordAndSearch = findViewById(R.id.recordAndSearch);

        // Product Database connection
        dataBaseAccess = DataBaseAccess.getInstance(getApplicationContext());
        dataBaseAccess.openDB();

        // Top avoid duplicate tags
        LinkedHashSet<String> productTagsArrayLHS = new LinkedHashSet<>(Arrays.asList(getAllDataFromTable(TAGS).replaceAll(COMMA_CHAR, NEW_LINE_CHAR).split(NEW_LINE_CHAR)));
        final String[] productTypeArray = getAllDataFromTable(TYPE).split(NEW_LINE_CHAR);
        String[] productTagsArray = productTagsArrayLHS.toArray(new String[0]);
        String[] products = getAllDataFromTable(PRODUCT).split(NEW_LINE_CHAR);

        ArrayAdapter<String> dropDownAdaptor = new ArrayAdapter<>(MainActivity.this, R.layout.text_design);
        // Setting item in drop down
        dropDownAdaptor.add(SELECT_PRODUCT_TYPE);
        dropDownAdaptor.addAll(productTypeArray);
        productTypeDropDown.setAdapter(dropDownAdaptor);
        productTypeDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchProductTypeEditText.getText().clear();
                String selectedProductType = productTypeDropDown.getItemAtPosition(position).toString();
                productListView(selectedProductType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Closing keyboard if user selects from drop down
        productTypeDropDown.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchProductTypeEditText.getWindowToken(), 0);
                return false;
            }
        });

        final ArrayAdapter<String> searchAdaptor = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1);
        // Setting item in auto suggestion
        searchAdaptor.addAll(productTagsArray);
        searchAdaptor.addAll(products);
        searchProductTypeEditText.setThreshold(1);
        searchProductTypeEditText.setAdapter(searchAdaptor);
        searchProductTypeEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                String searchedProduct = searchProductTypeEditText.getText().toString();
                productListView(searchedProduct);
            }
        });
        // Handling if match is not available in auto suggestion
        searchProductTypeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String searchedProduct = searchProductTypeEditText.getText().toString();
                    // To close auto suggestion is user don't select anything from suggestion and press done on keyboard
                    searchProductTypeEditText.dismissDropDown();
                    productListView(searchedProduct);
                }
                return false;
            }
        });

        // Record and search
        recordAndSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, SPEAK);
                searchProductTypeEditText.getText().clear();
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        barCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarCodeReader.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    protected void productListView(String product) {
        // Setting first selection to handle same value selection from drop down
        productTypeDropDown.setSelection(0);
        String resultFromDB = getProducts(product.trim());
        String[] returnProduct;
        String userId = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        // Update counter for product search in UserSearchData table
        dataBaseAccess.setDataInTable(userId, product.trim());
        if (product.trim().length() == 0) {
            // For empty search
            returnProduct = NO_PRODUCT_FOUND.split(NEW_LINE_CHAR);
        } else if (product.trim().equalsIgnoreCase(SELECT_PRODUCT_TYPE)) {
            // Handling if user selects SELECT_PRODUCT_TYPE option from drop down
            return;
        } else if(resultFromDB.length() == 0) {
            // For invalid search
            returnProduct = NO_PRODUCT_FOUND.split(NEW_LINE_CHAR);
        } else {
            returnProduct = resultFromDB.split(NEW_LINE_CHAR);
        }
        ArrayList<String> listArray = new ArrayList<>(Arrays.asList(returnProduct));
        MyListAdaptor myListAdaptor = new MyListAdaptor(listArray, MainActivity.this, returnProduct, dataBaseAccess);
        productDetailsListView.setAdapter(myListAdaptor);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            assert result != null;
            searchProductTypeEditText.setText(result.get(0));
            String searchedProduct = searchProductTypeEditText.getText().toString();
            searchProductTypeEditText.dismissDropDown();
            productListView(searchedProduct);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Get all products Type from Products table
    protected String getAllDataFromTable(String columnName) {
        return dataBaseAccess.getAllDataFromTable(columnName);
    }

    // Get Products based on searchedProduct from Products table
    protected String getProducts(String searchedProduct) {
        return dataBaseAccess.getProducts(searchedProduct);
    }

    // Will close the application if the back button pressed twice.
    @Override
    public void onBackPressed() {
        if ((backButtonPressedTime + TWO_SECONDS) > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            backToast.cancel();
            finish();
        } else {
            backToast = Toast.makeText(getBaseContext(), PRESS_BACK_TO_EXIT, Toast.LENGTH_SHORT);
            backToast.show();
        }
        backButtonPressedTime = System.currentTimeMillis();
    }
}