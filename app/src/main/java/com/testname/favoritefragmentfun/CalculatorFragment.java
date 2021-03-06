package com.testname.favoritefragmentfun;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CalculatorFragment extends Fragment {

    private Spinner initialSpinner,
            targetSpinner;
    Spinner symbolSpinner;
    private String initialType = "Select currency…",
            targetType = "Select currency…",
            symbol = "+";
    private EditText op1Text, op2Text;
    private TextView text;
    private float result;
    private static final float EURO = 1.0f;

    private static final String TARGET_SPINNER2 = "RESULT_T2";
    private static final String INPUT_SPINNER2 = "INPUT_T2";
    private static final String INPUT2 = "INPUT2";
    private static final String INPUT22 = "INPUT22";
    private static final String TEXT2 = "TEXT2";
    private static final String SYMBOL_T2 = "SYMBOL_T2";
    private Context mContext;

    private RequestQueue requestQueue = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getPreferences(MODE_PRIVATE);

        View view = inflater.inflate(R.layout.calculator_fragment,
                container, false);


        // set up initial spinner
        initialSpinner = view.findViewById(R.id.initialSpinner);
        // create adapter with spinner items
        final ArrayAdapter<String> initialAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.spinner_resource,
                getResources().getStringArray(R.array.typeOf)
        );
        initialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initialSpinner.setAdapter(initialAdapter);
        // spinner event
        initialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initialType = initialSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        // for spinners
        // create spinner
        // create spinner adapter, set it up with defaults and then link it to a string-array
        // add the adapter to the spinner
        // set up on item selected

        // set up target spinner
        targetSpinner = view.findViewById(R.id.targetSpinner);
        // create adapter with spinner items
        final ArrayAdapter<String> targetAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.spinner_resource,
                getResources().getStringArray(R.array.typeOf)
        );
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetSpinner.setAdapter(targetAdapter);
        // spinner event
        targetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                targetType = targetSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });


        // set up symbol spinner
        symbolSpinner = (Spinner) view.findViewById(R.id.spinner);
        final ArrayAdapter<String> symbolAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.spinner_resource,
                getResources().getStringArray(R.array.operations)
        );
        symbolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        symbolSpinner.setAdapter(symbolAdapter);
        symbolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                symbol = symbolSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        // grab texts / inputs
        text = view.findViewById(R.id.textView);
        op1Text = view.findViewById(R.id.toConvert1Text);
        op2Text = view.findViewById(R.id.toConvert2Text);

        // set up button
        Button mainButton = view.findViewById(R.id.button);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                // if spinners are in default value, then we can't proceed
                if (initialType.equals("Select currency…") || targetType.equals("Select currency…")) {
                    Toast toast = Toast.makeText(getActivity(), "Select currencies…", Toast.LENGTH_SHORT);
                    toast.show();
                } else { // if there is no input we cannot proceed
                    if ((op1Text.getText().toString().equals("") ||
                            op1Text.getText().toString().equals(".") ||
                            op2Text.getText().toString().equals("") ||
                            op2Text.getText().toString().equals("."))) {
                        Toast toast = Toast.makeText(getActivity(), "Input amounts convert…", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {// get number to convert
                        calculate((initialType.split(" "))[0],
                                targetType,
                                Float.parseFloat(op1Text.getText().toString()),
                                Float.parseFloat(op2Text.getText().toString()));

                    }
                }
            }
        });

        // at the end of intializing we grab prefs and fill out the app using them

        int initialSpinnerPosition = initialAdapter.getPosition(settings.getString(INPUT_SPINNER2, initialType));
        initialSpinner.setSelection(initialSpinnerPosition);

        int targetSpinnerPosition = targetAdapter.getPosition(settings.getString(TARGET_SPINNER2, targetType));
        targetSpinner.setSelection(targetSpinnerPosition);

        int symbolSpinnerPosition = symbolAdapter.getPosition(settings.getString(SYMBOL_T2, symbol));
        symbolSpinner.setSelection(symbolSpinnerPosition);

        op1Text.setText(settings.getString(INPUT2,""));

        text.setText(settings.getString(TEXT2, "Select currencies to add. The second one is the final currency"));

        op2Text.setText(settings.getString(INPUT22, ""));

        return view;

    }//end onCreateView

    // API call then calculates and displays the changes
    private void calculate(final String from, final String to, final float first, final float second) {

        String url = "http://data.fixer.io/api/latest?access_key="
                +  getString(R.string.api_key)
                +  "&symbols="
                + from + ","
                + to;

        requestQueue = Volley.newRequestQueue(getActivity());
        if (getActivity() == null) return;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.i("----------JSON response", response.toString());
                        try {

                            JSONObject element = response.getJSONObject("rates");

                            // Get conversion rate to EURO's from both of the currencies
                            // then we divide the first one by 1 so we get the rate from EURO's to the currency
                            // next we multiply the number we started with by that number to get it into the second units
                            // if the symbol was + we add, if - we subtract

                            float baseInitial = Float.parseFloat(element.getString(from));
                            float baseResult = Float.parseFloat(element.getString(to));

                            float firstInitialConversionCoefficient = EURO / baseInitial;
                            result = first * firstInitialConversionCoefficient;
                            result *= baseResult;

                            if (symbol.equals("+")) result += second;
                            else result -= second;

                            text.setText(String.format(Locale.US, "%.2f %s", result, to));
                            text.setGravity(Gravity.CENTER_HORIZONTAL);



                        } catch (JSONException ex) {
                            System.out.println("start stack trace");
                            ex.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("----------JSON response", error.toString());//8
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    // save instance
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INPUT_SPINNER2, initialType);
        outState.putString(TARGET_SPINNER2, targetType);
        outState.putString(INPUT2, op1Text.getText().toString());
        outState.putString(INPUT22, op2Text.getText().toString());
        outState.putString(TEXT2, text.getText().toString());
        outState.putString(SYMBOL_T2, symbol);
    }

    // on pause
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(INPUT_SPINNER2, initialType);
        editor.putString(TARGET_SPINNER2, targetType);
        editor.putString(INPUT2, op1Text.getText().toString());
        editor.putString(INPUT22, op2Text.getText().toString());
        editor.putString(TEXT2, text.getText().toString());
        editor.putString(SYMBOL_T2, symbol);
        editor.apply();
    }
}

