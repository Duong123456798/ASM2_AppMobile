package com.example.asm2_applicationdevelopment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2_applicationdevelopment.DatabaseSQLite.ExpenseDatabase;
import com.example.asm2_applicationdevelopment.Model.Expense;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText editTextDescription, editTextDate, editTextAmount;
    private Spinner spinnerCategory;
    private Button buttonSave, buttonCancel, buttonDelete;
    private ExpenseDatabase expenseDatabase;
    private int expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        // Initialize the views
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextAmount = findViewById(R.id.editTextAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonDelete = findViewById(R.id.buttonDelete); // Ensure this ID matches your layout

        // Initialize the database
        expenseDatabase = new ExpenseDatabase(this);

        // Populate the Spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Get the expense ID from the intent
        expenseId = getIntent().getIntExtra("EXPENSE_ID", -1); // Use the correct intent key "EXPENSE_ID"

        // Load expense data if editing
        if (expenseId != -1) {
            loadExpenseData(expenseId);
        }

        // Set click listeners for buttons
        buttonSave.setOnClickListener(v -> saveExpense());
        buttonCancel.setOnClickListener(v -> finish());
        buttonDelete.setOnClickListener(v -> confirmDeleteExpense()); // Handle delete with confirmation
    }

    private void loadExpenseData(int id) {
        Expense expense = expenseDatabase.getExpense(id);

        if (expense != null) {
            editTextDescription.setText(expense.getDescription());
            editTextDate.setText(expense.getDate());
            editTextAmount.setText(String.valueOf(expense.getAmount()));

            // Set the correct category in the spinner
            String category = expense.getCategory();
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
            int spinnerPosition = adapter.getPosition(category);
            if (spinnerPosition >= 0) {
                spinnerCategory.setSelection(spinnerPosition);
            }
        }
    }

    private void saveExpense() {
        String description = editTextDescription.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (description.isEmpty() || date.isEmpty() || amountStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        Expense expense = new Expense(expenseId, description, date, amount, category);

        // Update the existing expense
        int result = expenseDatabase.updateExpense(expense);

        if (result > 0) {
            Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteExpense() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Do you want to delete this expense?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteExpense();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteExpense() {
        if (expenseId != -1) {
            // Perform the delete operation
            int result = expenseDatabase.deleteExpense(expenseId);

            if (result > 0) {
                Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after deletion
            } else {
                Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No expense to delete", Toast.LENGTH_SHORT).show();
        }
    }
}
