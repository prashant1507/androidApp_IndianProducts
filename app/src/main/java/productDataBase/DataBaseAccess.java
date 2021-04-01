package productDataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import androidx.annotation.RequiresApi;
import resources.Constants;

public class DataBaseAccess implements Constants {
    private SQLiteOpenHelper openHelper;
    private static SQLiteDatabase db;
    private static DataBaseAccess dataBaseAccess;
    static Cursor cursor = null;

    private DataBaseAccess(Context context) {
        this.openHelper = new DataBaseHelper(context);
    }

    // to return single instance of database
    public static DataBaseAccess getInstance(Context context) {
        if (dataBaseAccess == null) {
            dataBaseAccess = new DataBaseAccess(context);
        }
        return dataBaseAccess;
    }

    public void openDB() {
        db = openHelper.getWritableDatabase();
    }

//    public void closeDB() {
//        if (db !=null) {
//            db.close();
//        }
//    }

    // Return products based on Tags from Products table
    public String getProducts(String productSearched) {
        cursor = db.rawQuery("select Product from Products where upper(Tags) like upper('%"+productSearched+"%') or upper(Product) like upper('%"+productSearched+"%')", new String[]{});
        StringBuilder stringBuffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String product = cursor.getString(0);
            stringBuffer.append(product).append(NEW_LINE_CHAR);
        }
        return stringBuffer.toString().trim();
    }

    // Return Type, Tags, Product, ManufacturerCountry and ManufacturerCompany from Products table based on columnName
    public String getAllDataFromTable(String columnName) {
        cursor = db.rawQuery("select distinct "+columnName+" from Products", new String[]{});
        StringBuilder stringBuffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String product = cursor.getString(0);
            stringBuffer.append(product).append(NEW_LINE_CHAR);
        }
        return stringBuffer.toString().trim();
    }

    // Return column details for matching product from Products table
    public static String getProductDetails(String columnName, String productName) {
        cursor = db.rawQuery("select "+columnName+" from Products where Product='"+productName+"'", new String[]{});
        StringBuilder stringBuffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String product = cursor.getString(0);
            stringBuffer.append(product).append(NEW_LINE_CHAR);
        }
        return stringBuffer.toString().trim();
    }

    // Return Product Rating or UserRating from Products table
    public static int getRating(String columnName, String productName) {
        cursor = db.rawQuery("select "+columnName+" from Products where Product='"+productName+"'", new String[]{});
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        return count;
    }

    // Set User Rating for Product in Products table
    public static void setUserRatingForProduct(int userRating, String productName) {
        String query = "Update Products set Userrating="+userRating+" where Product='"+productName+"'";
        db.execSQL(query);
    }

    // Update/Insert into UserSearchData table for user searches
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDataInTable(String userId, String searchedText) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        List<String> valid = Arrays.asList(getAllDataFromTable(TYPE).split(NEW_LINE_CHAR));
        if (!valid.contains(searchedText)) {
            if (!searchedText.equalsIgnoreCase(SELECT_PRODUCT_TYPE)) {
                String query;
                int currentCount = getSearchCount(searchedText);
                if ( currentCount  > 0 ) {
                    query = "Update UserSearchData SET LastSearchDateTime='"+dtf.format(now)+"',SearchCount="+(currentCount+1)+" WHERE upper(SearchedItem)=upper('"+searchedText+"')";
                } else {
                    query = "INSERT into UserSearchData (UserId,LastSearchDateTime,SearchedItem,SearchCount) VALUES ('"+userId+"','"+dtf.format(now)+"','"+searchedText+"',1)";
                }
                db.execSQL(query);
            }
        }
    }

    // Return counter from UserSearchData table
    public int getSearchCount(String searchedText) {
        cursor = db.rawQuery("select SearchCount from UserSearchData where upper(SearchedItem)=upper('"+searchedText+"')", new String[]{});
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        return count;
    }

    // Return Country from BarCodes table for matching Code
    public static String getCountryForCode(String code) {
        cursor = db.rawQuery("select Country from BarCodes where Code='"+code+"'", new String[]{});
        StringBuilder stringBuffer = new StringBuilder();
        while (cursor.moveToNext()) {
            String product = cursor.getString(0);
            stringBuffer.append(product).append(NEW_LINE_CHAR);
        }
        return stringBuffer.toString().trim();
    }
}
