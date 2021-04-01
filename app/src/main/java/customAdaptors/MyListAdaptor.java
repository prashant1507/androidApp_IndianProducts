package customAdaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import com.indianProducts.PopInfo;
import java.util.ArrayList;
import com.indianProducts.R;
import productDataBase.DataBaseAccess;
import resources.Constants;

public class MyListAdaptor extends BaseAdapter implements ListAdapter, Constants {
    private ArrayList<String> list;
    private Context context;
    private String[] productsFromDB;
    public LayoutInflater layoutInflater;
    public DataBaseAccess dataBaseAccess;

    public MyListAdaptor(ArrayList<String> list, Context context, String[] productsFromDB, DataBaseAccess dataBaseAccess) {
        this.list = list;
        this.context = context;
        this.productsFromDB = productsFromDB;
        this.dataBaseAccess = dataBaseAccess;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        // Handle text of List View
        TextView textView;
        RatingBar ratingBar;
        if (view == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (productsFromDB[0].equalsIgnoreCase(SELECT_PRODUCT_TYPE) || productsFromDB[0].equalsIgnoreCase(NO_PRODUCT_FOUND)) {
                view = layoutInflater.inflate(R.layout.text_design, null);
                textView = view.findViewById(R.id.listViewText2);
                textView.setText(list.get(position));
            } else {
                view = layoutInflater.inflate(R.layout.info_button_design, null);
                textView = view.findViewById(R.id.listViewText);
                ratingBar = view.findViewById(R.id.ratingBar);
                ImageButton infoButton = view.findViewById(R.id.infoButton);
                textView.setText(list.get(position));
                ratingBar.setRating(DataBaseAccess.getRating(PRODUCT_RATING, list.get(position)));
                // Handle info button and add onClickListeners
                infoButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    Intent intent = new Intent(context, PopInfo.class);
                    intent.putExtra(SELECTED_PRODUCT, list.get(position).trim());
                    context.startActivity(intent);
                    }
                });
            }
        }
        return view;
    }
}