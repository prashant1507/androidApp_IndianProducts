package resources;

public interface Constants {
    String SELECT_PRODUCT_TYPE = "Select Product Type";
    String NO_PRODUCT_FOUND = "No product found. Please select from drop down";
    String PRESS_BACK_TO_EXIT = "Press back again to exit.";
    String NEW_LINE_CHAR = "\n";
    String COMMA_CHAR = ",";
    String SPEAK = "Hi, Speak to search";
    String SELECTED_PRODUCT = "selectedProduct";
    String RATING_MSG = "Thanks for sharing your rating with us.";
    String PLEASE_PROVIDE_RATING = "Please provide rating with us.";
    String NO_CHANGE_RATING = "No change in rating.";
    String SCAN_BAR_CODE = "Scan Bar Code";
    String COUNTRY_NA = "Country not available.";

    int TWO_SECONDS = 2000;
    int REQUEST_CODE = 1;
    int ZERO = 0;

    // details.db
    int DETAILS_DB_VERSION = 4;
    String DETAILS_DB_NAME = "details.db";

    // Product table
    String TYPE = "Type";
    String PRODUCT = "Product";
    String MANUFACTURER_COMPANY = "ManufacturerCompany";
    String MANUFACTURER_COUNTRY = "ManufacturerCountry";
    String PRODUCT_RATING = "ProductRating";
    String USER_RATING = "UserRating";
    String TAGS = "Tags";

    // UserSearchData table

    // BarCodes Table
}
