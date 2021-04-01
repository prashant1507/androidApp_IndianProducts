package com.indianProducts;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import productDataBase.DataBaseAccess;
import resources.Constants;

public class PopInfo extends AppCompatActivity implements Constants {
    private RatingBar userRatingBar;
    private Button submitUserRating;
    private String selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        selectedProduct = getIntent().getStringExtra(SELECTED_PRODUCT);

        TextView productCategory = findViewById(R.id.productCategory);
        TextView productName = findViewById(R.id.productName);
        TextView companyName = findViewById(R.id.companyName);
        TextView companyOrigin = findViewById(R.id.companyOrigin);
        userRatingBar = findViewById(R.id.userRatingBar);
        submitUserRating = findViewById(R.id.submitUserRating);
        submitUserRating.setClickable(false);
        submitUserRating.setEnabled(true);

        productCategory.setText(DataBaseAccess.getProductDetails(TYPE, selectedProduct));
        productName.setText(selectedProduct);
        companyName.setText(DataBaseAccess.getProductDetails(MANUFACTURER_COMPANY, selectedProduct));
        companyOrigin.setText(DataBaseAccess.getProductDetails(MANUFACTURER_COUNTRY, selectedProduct));
        userRatingBar.setRating(DataBaseAccess.getRating(USER_RATING,selectedProduct));

        submitUserRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentUserRating = DataBaseAccess.getRating(USER_RATING,selectedProduct);
                int rating = Math.round(userRatingBar.getRating());
                if (currentUserRating == 0 && rating == 0) {
                    Toast.makeText(getApplicationContext(), PLEASE_PROVIDE_RATING, Toast.LENGTH_SHORT).show();
                } else if (currentUserRating == rating) {
                    Toast.makeText(getApplicationContext(), NO_CHANGE_RATING, Toast.LENGTH_SHORT).show();
                }
                if (currentUserRating != rating) {
                    submitUserRating.setClickable(true);
                    DataBaseAccess.setUserRatingForProduct(rating, selectedProduct);
                    currentUserRating = rating;
                    Toast.makeText(getApplicationContext(), RATING_MSG, Toast.LENGTH_SHORT).show();
                }
                userRatingBar.setRating(currentUserRating);
            }
        });
    }
}
