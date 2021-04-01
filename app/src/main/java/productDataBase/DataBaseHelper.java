package productDataBase;

import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import resources.Constants;

public class DataBaseHelper extends SQLiteAssetHelper implements Constants {
    public DataBaseHelper (Context context) {
        super(context, DETAILS_DB_NAME, null, DETAILS_DB_VERSION);
    }
}