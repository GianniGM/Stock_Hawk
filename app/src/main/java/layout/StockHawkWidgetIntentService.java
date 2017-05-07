package layout;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

/**
 * Created by giannig on 4/24/17.
 */

public class StockHawkWidgetIntentService extends IntentService {

    private static final String[] PROJECTION_COLUMNS = {
            Contract.Quote.QUOTE_COLUMNS.get(Contract.Quote.POSITION_SYMBOL),
            Contract.Quote.QUOTE_COLUMNS.get(Contract.Quote.POSITION_PRICE),
            Contract.Quote.QUOTE_COLUMNS.get(Contract.Quote.POSITION_PERCENTAGE_CHANGE),
            Contract.Quote.QUOTE_COLUMNS.get(Contract.Quote.POSITION_ABSOLUTE_CHANGE)
    };

    private final static int INDEX_POSITION_SYMBOL = 0;
    private final static int INDEX_POSITION_PRICE = 1;
    private final static int INDEX_POSITION_PER_CHANGE = 2;
    private final static int INDEX_POSITION_ABS_CHANGE = 3;

    public StockHawkWidgetIntentService(){
        super("StockHawkWidgetIntentService");
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Cursor data) {

        //TODO EXTRACT DATA FROM THE CURSOR
        String symbol = data.getString(INDEX_POSITION_SYMBOL);
        int price = data.getInt(INDEX_POSITION_PRICE);
        float absoluteChange = data.getFloat(INDEX_POSITION_ABS_CHANGE);
        float percentChange = data.getFloat(INDEX_POSITION_PER_CHANGE);

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_hawk_widget);

        if (absoluteChange > 0) {
            int green = ContextCompat.getColor(context, R.color.material_green_700);
            views.setTextViewText(R.id.appwidget_percent_change, "+" + String.valueOf(percentChange));
            views.setTextColor(
                R.id.appwidget_percent_change,
                green
            );
        } else {
            int red = ContextCompat.getColor(context, R.color.material_red_700);
            views.setTextViewText(R.id.appwidget_percent_change, "-" + String.valueOf(percentChange));
            views.setTextColor(
                R.id.appwidget_percent_change,
                red
            );
        }

        views.setTextViewText(R.id.appwidget_text_symbol, symbol);
        views.setTextViewText(R.id.appwidget_text_price, String.valueOf(price));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(
                        this,
                        StockHawkWidgetProvider.class
                )
        );

        // GET DATA FROM THE CONTENT PROVIDER
        Cursor data = getContentResolver().query(
            Contract.Quote.URI,
            PROJECTION_COLUMNS,
            null,
            null,
            null
        );

        // CHECK THE DATA
        if(data == null){
            return;
        }

        if(!data.moveToFirst()){
            data.close();
            return;
        }

        // loop and update widgets
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(this, appWidgetManager, appWidgetId, data);
        }
    }
}
