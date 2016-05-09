package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Query;
import com.sam_chordas.android.stockhawk.rest.QueryList;
import com.sam_chordas.android.stockhawk.rest.Quote;
import com.sam_chordas.android.stockhawk.rest.Results;
import com.sam_chordas.android.stockhawk.rest.RetroService;
import com.squareup.okhttp.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChartActivity extends Activity {
 TextView textView;
    String symbol;
    List<Quote>  quoteArrayList;
    LineChart lineChart;
    ArrayList<Entry> entries;
    ArrayList<String> labels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        symbol = getIntent().getStringExtra("symbol");
//        getActionBar().setDisplayHomeAsUpEnabled(true);
         lineChart   = (LineChart)findViewById(R.id.chart);
        textView = (TextView)findViewById(R.id.textView);

         entries  = new ArrayList<>();
         labels = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = sdf.format(new Date());
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://query.yahooapis.com").addConverterFactory(GsonConverterFactory.create()).build();
        RetroService retroService =  retrofit.create(RetroService.class);
        String q= "select * from yahoo.finance.historicaldata where symbol = \"" +symbol + "\" and startDate = \"2016-01-01\" and endDate = \"" + dateNow + "\"";
        String env =  "store://datatables.org/alltableswithkeys";
        Call<QueryList> call = retroService.getquotes(q,env,"json");
//
        call.enqueue(new Callback<QueryList>() {
            @Override
            public void onResponse(Call<QueryList> call, Response<QueryList> response) {
                if (response.isSuccessful())
                {
                    QueryList queryList = response.body();

                        List<Quote> quoteArrayList = queryList.query.results.quote;
                    try {
                        textView.setText(getString(R.string.graph_arrrived));
                        for (int i = 0; i < quoteArrayList.size(); i++) {
                            entries.add(new Entry(Float.parseFloat(quoteArrayList.get(i).High), i));
                            labels.add(quoteArrayList.get(i).Date);

                        }
                        LineDataSet dataset = new LineDataSet(entries, getString(R.string.dataset_label));

                        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

                        LineData data = new LineData(labels, dataset);
                        lineChart.setData(data);

                        lineChart.setDescription(getString(R.string.graph_description));
                    } catch (NullPointerException e) {

                        textView.setText(getString(R.string.no_data_available));
                    }




                }
            }

            @Override
            public void onFailure(Call<QueryList> call, Throwable t) {
                textView.setText("Fail to Load Graph");

            }
        });

//        Cursor QueryCursor = this.getContentResolver().query(QuoteProvider.Quotes.withSymbol(symbol),
//                new String[] {
//                        QuoteColumns._ID,QuoteColumns.BIDPRICE }, null,
//                null, QuoteColumns._ID);
//
//        if (QueryCursor != null){
////            DatabaseUtils.dumpCursor(QueryCursor);
//            QueryCursor.moveToFirst();
//            String text = "";
//            for (int i = 0; i < QueryCursor.getCount(); i++){
//                text = text + Float.parseFloat( QueryCursor.getString(QueryCursor.getColumnIndex("bid_price")));
//                entries.add(new Entry(Float.parseFloat( QueryCursor.getString(QueryCursor.getColumnIndex("bid_price"))), i));
//                labels.add(QueryCursor.getString(QueryCursor.getColumnIndex("_id")));
//                QueryCursor.moveToNext();
//            }
//        textView.setVisibility(View.INVISIBLE);
//            entries.add(new Entry(37.0f,1));
//        }

    }

}
