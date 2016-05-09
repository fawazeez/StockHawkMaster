package com.example.sam_chordas.stockhawk;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by fawaz on 5/8/2016.
 */
public class QuoteWidgetRemoteViewsService extends RemoteViewsService {

    private static final String[] STOCK_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.ISUP

    };
    static final int INDEX_QUOTE_ID = 0;
    static final int INDEX_SYMBOL = 1;
    static final int INDEX_BIDPRICE = 2;
    static final int INDEX_PERCENT_CHANGE = 3;
    static final int INDEX_CHANGE = 4;
    static final int INDEX_ISUP = 5;


    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                Uri quoteUri = QuoteProvider.Quotes.CONTENT_URI;
                data = getContentResolver().query(quoteUri,STOCK_COLUMNS,QuoteColumns.ISCURRENT + " = ?",new String[]{"1"},QuoteColumns.SYMBOL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews remoteViews  = new RemoteViews(getPackageName(),R.layout.collection_widget_item );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(remoteViews, data.getString(INDEX_SYMBOL));
                }
                remoteViews.setTextViewText(R.id.stock_symbol, data.getString(INDEX_SYMBOL));
                remoteViews.setTextViewText(R.id.bid_price, data.getString(INDEX_BIDPRICE));
                remoteViews.setTextViewText(R.id.change, data.getString(INDEX_CHANGE));
                if (Integer.parseInt(data.getString(INDEX_ISUP))==1)
                remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_green);
                else
                    remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_red);

                final  Intent fillIntent = new Intent();
                fillIntent.putExtra("symbol", data.getString(INDEX_SYMBOL));
                remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);
                return remoteViews;
            }
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews remoteViews, String string) {
                remoteViews.setContentDescription(R.id.stock_symbol, string);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.collection_widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_QUOTE_ID);
                return position;

            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
