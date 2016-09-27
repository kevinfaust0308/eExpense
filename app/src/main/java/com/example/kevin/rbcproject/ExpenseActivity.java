package com.example.kevin.rbcproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private static final String TAG = ExpenseActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private PieChart mPieChart;

    private TextView mCurrentMonth;
    private TextView mTotalExpenseText;
    private ImageButton mRefreshTotalExpenseButton;

    private SharedPreferences mSharedPreferences;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        mPieChart = (PieChart) findViewById(R.id.chart);
        mRecyclerView = (RecyclerView) findViewById(R.id.expenseRecyclerView);
        mCurrentMonth = (TextView) findViewById(R.id.currentMonth);
        mTotalExpenseText = (TextView) findViewById(R.id.totalExpenseText);
        mRefreshTotalExpenseButton = (ImageButton) findViewById(R.id.refreshTotalExpenseButton);

        //prevent keyboard automatically opening on an activity with an edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /*mSharedPreferences = getSharedPreferences(
                PREFS_FILE, MODE_PRIVATE);*/
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        String expenseJson = mSharedPreferences.getString(SharedPreferenceFields.EXPENSES, null);
        Log.d(TAG, "expenseJson is " + expenseJson);

        //determine whether user has expense data or not
        if (expenseJson == null) {
            //if it is first time user for opening the app/no previous data
            Expenses.expenseList = createDefaultExpenseList();
        } else {
            //expenseList = get data from shared preferences
            Expenses.expenseList = gson.fromJson(expenseJson, new TypeToken<List<Expense>>(){}.getType());
        }

        //pie chart configuration
        setUpPieChart(Expenses.expenseList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(100);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setAdapter(new ExpenseAdapater(getApplication(), Expenses.expenseList, mSharedPreferences));

        double totExp = refreshTotalExpenses();
        mTotalExpenseText.setText("$" + Double.toString(new BigDecimal(totExp).setScale(2, RoundingMode.HALF_UP).doubleValue()));

        //change total expense text color depending on current expense % of expense limit
        final double expenseLimit = getIntent().getDoubleExtra(SharedPreferenceFields.EXPENSE_LIMIT, 0.00);
        refreshExpenseStatus(totExp, expenseLimit);

        mRefreshTotalExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double totExp = refreshTotalExpenses();
                mTotalExpenseText.setText("$" + Double.toString(new BigDecimal(totExp).setScale(2, RoundingMode.HALF_UP).doubleValue()));
                setUpPieChart(Expenses.expenseList);
                refreshExpenseStatus(totExp, expenseLimit);
            }
        });

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        mCurrentMonth.setText("Expenses for " + String.format("%02d", month) + "/" + year);

    }

/*    public void notificationCreator() {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotifyBuilder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("New Message")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.ic_notify_status)

        mNotifyBuilder.setContentText(currentText)
                .setNumber(++numMessages);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        notificationManager.notify(
                notifyID,
                mNotifyBuilder.build());
    }*/


    public void refreshExpenseStatus(double totExp, double expenseLimit) {
        if (((totExp / expenseLimit) * 100) >= 100.00) {
            mTotalExpenseText.setTextColor(ContextCompat.getColor(this, R.color.md_red_700));
            ((TextView) findViewById(R.id.totalExpenseTextDescription)).setText("EXPENSE LIMIT REACHED");
            ((TextView) findViewById(R.id.totalExpenseTextDescription)).setTextColor(ContextCompat.getColor(this, R.color.md_red_900));
        }
        else if (((totExp / expenseLimit) * 100) >= 90.00) {
            mTotalExpenseText.setTextColor(ContextCompat.getColor(this, R.color.md_red_700));
            ((TextView) findViewById(R.id.totalExpenseTextDescription)).setText("Expense at dangerous level");
            ((TextView) findViewById(R.id.totalExpenseTextDescription)).setTextColor(ContextCompat.getColor(this, R.color.md_red_700));
        } else if (((totExp / expenseLimit) * 100) >= 80.00) {
            mTotalExpenseText.setTextColor(ContextCompat.getColor(this, R.color.md_yellow_700));
            ((TextView) findViewById(R.id.totalExpenseTextDescription)).setText("Approaching expense limit");
            ((TextView) findViewById(R.id.totalExpenseTextDescription)).setTextColor(ContextCompat.getColor(this, R.color.md_yellow_700));
        } else {
            mTotalExpenseText.setTextColor(ContextCompat.getColor(this, R.color.md_green_500));
            ((TextView) findViewById(R.id.totalExpenseTextDescription)).setText("Expense at safe level");
            ((TextView) findViewById(R.id.totalExpenseTextDescription)).setTextColor(ContextCompat.getColor(this, R.color.md_green_500));
        }
    }


    public double refreshTotalExpenses() {
        double totalExpense = 0.00;

        for (Expense e : Expenses.expenseList) {
            totalExpense += e.getExpenseValue();
        }

        return totalExpense;
    }

    public void setUpPieChart(List<Expense> expenses) {
        List<PieEntry> entries = new ArrayList<>();

        //dynamically add entry values based on current expenses
        for (Expense expense : expenses) {
            entries.add(new PieEntry((float) expense.getExpenseValue(), expense.getExpenseName()));
        }

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : PieGraph.PIEGRAPH_COLOR_LEGEND) {
            colors.add(ContextCompat.getColor(getApplication(), color));
        }
        dataset.setColors(colors);

        mPieChart.setData(new PieData(dataset));
        mPieChart.setDrawEntryLabels(false); //hides labels
        mPieChart.getLegend().setEnabled(false);

        //other settings
        mPieChart.animateX(500);
        mPieChart.setUsePercentValues(true);
        mPieChart.setCenterText("Expenses\nby %");
        mPieChart.setHoleRadius(70);
        mPieChart.setDescription("");
    }

    //if no previous expense data, create new data list
    public ArrayList<Expense> createDefaultExpenseList() {
        ArrayList<Expense> list = new ArrayList<>();
        for (String expense : PieGraph.SMALL_BUSINESS_EXPENSES) {
            list.add(new Expense(expense, 1.50));
        }
        return list;
    }


    @Override
    protected void onPause() {
        super.onPause();
        Gson gson = new Gson();
        String expensesJson = gson.toJson(Expenses.expenseList);
        mSharedPreferences.edit().putString(SharedPreferenceFields.EXPENSES, expensesJson).apply();
        Log.d(TAG, "Saving data");
    }
}
