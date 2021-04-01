package com.indianProducts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import productDataBase.DataBaseAccess;
import resources.Constants;

public class BarCodeReader extends AppCompatActivity implements Constants {
    private TextView barCodeFormat;
    private TextView barCodeResult;
    private TextView country;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_code_detail);
        intent = new Intent(BarCodeReader.this, MainActivity.class);
        barCodeFormat = findViewById(R.id.barCodeFormat);
        barCodeResult = findViewById(R.id.barCodeResult);
        country = findViewById(R.id.country);

        IntentIntegrator intentIntegrator = new IntentIntegrator(BarCodeReader.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt(SCAN_BAR_CODE);
        intentIntegrator.setCameraId(ZERO);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getFormatName() == null && intentResult.getContents() == null) {
            startActivity(intent);
            this.finish();
        } else {
            barCodeFormat.setText(intentResult.getFormatName());
            barCodeResult.setText(intentResult.getContents());
            String dataDB = DataBaseAccess.getCountryForCode(intentResult.getContents().substring(0,3));
            if (dataDB.length() == 0) {
                country.setText(COUNTRY_NA);
            } else {
                country.setText(dataDB);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(intent);
        this.finish();
    }
}
