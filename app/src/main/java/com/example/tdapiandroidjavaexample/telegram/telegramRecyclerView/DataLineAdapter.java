package com.example.tdapiandroidjavaexample.telegram.telegramRecyclerView;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tdapiandroidjavaexample.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class DataLineAdapter extends RecyclerView.Adapter<DataLineAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<DataLine> states;
    private final SharedPreferences sharedPreferences;
    public DataLineAdapter(Context context, List<DataLine> states, SharedPreferences sharedPreferences) {
        this.states = states;
        this.inflater = LayoutInflater.from(context);
        this.sharedPreferences = sharedPreferences;
    }
    @Override
    public DataLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.attribute_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataLineAdapter.ViewHolder holder, int position) {
        DataLine line = states.get(position);
        holder.attributeName.setText(line.getNiceName());
        if(line.getValue() == null || line.getValue().equals("")){
            holder.attributeValue.setText(R.string.empty);
        }
        else{
            holder.attributeValue.setText(String.valueOf(line.getValue()));
        }

        holder.alertButton.setOnClickListener(v -> {
            showAttributeAlert(holder, line);
        });
    }
    private void showAttributeAlert(ViewHolder holder, DataLine attributeLine) {
        Context ctx = inflater.getContext();
        Resources r = ctx.getResources();
        TextInputLayout textInputLayout = new TextInputLayout(ctx);
        EditText editText = new EditText(ctx);
        textInputLayout.setPadding(
                r.getDimensionPixelOffset(R.dimen.alert_padding_left),
                r.getDimensionPixelOffset(R.dimen.alert_padding_top),
                r.getDimensionPixelOffset(R.dimen.alert_padding_right),
                r.getDimensionPixelOffset(R.dimen.alert_padding_bottom));
        textInputLayout.addView(editText);

        AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                .setTitle("Enter value")
                .setView(textInputLayout)
                .setMessage(String.format("Enter %s.", attributeLine.getNiceName()))
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    String value = editText.getText().toString().trim();

                    if (value.equals("")) {
                        holder.attributeValue.setText(R.string.empty);
                    }
                    else {
                        switch (attributeLine.getTelegramAttribute()) {
                            case API_HASH:
                                sharedPreferences.edit().putString(r.getString(R.string.raw_api_hash), value).apply();
                                break;
                            case API_ID:
                                sharedPreferences.edit().putString(r.getString(R.string.raw_api_id), value).apply();
                                break;
                            case PHONE_NUMBER:
                                sharedPreferences.edit().putString(r.getString(R.string.raw_phone_number), value).apply();
                                break;
                            case PUBLIC_CHAT_NAME:
                                sharedPreferences.edit().putString(r.getString(R.string.raw_public_chat_name), value).apply();
                                break;
                            default:
                                throw new RuntimeException();
                        }

                        holder.attributeValue.setText(value);
                    }
                    dialogInterface.cancel();
                }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel()).create();
        alertDialog.show();
    }
    @Override
    public int getItemCount() {
        return states.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView attributeName;
        final TextView attributeValue;
        final Button alertButton;
        ViewHolder(View view){
            super(view);
            attributeName = view.findViewById(R.id.tg_attribute_name_tv);
            attributeValue = view.findViewById(R.id.tg_attribute_value_tv);
            alertButton = view.findViewById(R.id.tg_attribute_change_btn);
        }
    }
}