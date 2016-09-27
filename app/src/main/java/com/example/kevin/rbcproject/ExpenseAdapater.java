package com.example.kevin.rbcproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kevin on 2016-09-24.
 */
public class ExpenseAdapater extends RecyclerView.Adapter<ExpenseAdapater.MyViewHolder> {

    private Context mContext;
    private List<Expense> mExpenseList;
    private int[] mColorLegend;

    private SharedPreferences mSharedPreferences;

    public ExpenseAdapater(Context context, List<Expense> expenses, SharedPreferences sp) {
        mContext = context;
        mExpenseList = Expenses.expenseList;
        mSharedPreferences = sp;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.expense_item_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Expense expense = Expenses.expenseList.get(position); //0-index

        holder.mColorLegend.setBackgroundColor(ContextCompat.getColor(mContext, PieGraph.PIEGRAPH_COLOR_LEGEND[position]));
        holder.mExpenseName.setText(expense.getExpenseName());
        holder.mExpenseValue.setText(Double.toString(expense.getExpenseValue()));

        holder.mExpenseValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                expense.setExpenseValue((float) Double.parseDouble(s.toString()));
                Expenses.expenseList.set(position, expense);
            }
        });
    }


    @Override
    public int getItemCount() {
        return Expenses.expenseList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mColorLegend;
        public TextView mExpenseName;
        public EditText mExpenseValue;

        public MyViewHolder(View itemView) {
            super(itemView);
            mColorLegend = (TextView) itemView.findViewById(R.id.colorLegend);
            mExpenseName = (TextView) itemView.findViewById(R.id.expense_name);
            mExpenseValue = (EditText) itemView.findViewById(R.id.expense_value);
        }
    }


}
