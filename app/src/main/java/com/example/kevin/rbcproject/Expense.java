package com.example.kevin.rbcproject;

/**
 * Created by Kevin on 2016-09-24.
 */
public class Expense {

    private String mExpenseName;
    private double mExpenseValue;


    public Expense(String expenseName, double expenseValue) {
        mExpenseName = expenseName;
        mExpenseValue = expenseValue;
    }


    public String getExpenseName() {
        return mExpenseName;
    }


    public void setExpenseName(String expenseName) {
        mExpenseName = expenseName;
    }


    public double getExpenseValue() {
        return mExpenseValue;
    }


    public void setExpenseValue(float expenseValue) {
        mExpenseValue = expenseValue;
    }


    @Override
    public String toString() {
        return "Expense: " + mExpenseName + " at " + mExpenseValue;
    }
}
