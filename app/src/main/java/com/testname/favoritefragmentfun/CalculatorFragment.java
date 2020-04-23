package com.testname.favoritefragmentfun;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import java.lang.reflect.Array;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CalculatorFragment extends Fragment {

    private Spinner FirstSpinner, SecondSpinner, OperationsSpinner;
    private EditText FirstText, SecondText;
    private TextView ResultsText;
    private String FirstType = "Select initial currency…",
            SecondType = "Select desired currency…",
            OperationsType = "Select desired operation…";

    private float result;
    private RequestQueue requestQueue = null;

    private static final float EURO = 1.0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getPreferences(MODE_PRIVATE);

        View view = inflater.inflate(R.layout.calculator_fragment, container, false);

        //set the first spinner
        FirstSpinner = view.findViewById(R.id.firstSpinner);
        //create adapter with spinner items
        final ArrayAdapter<String> firstAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.spinner_resource,
                getResources().getStringArray(R.array.conversionsFrom)
        );
        firstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        FirstSpinner.setAdapter(firstAdapter);
        //spinner event
        FirstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FirstType = FirstSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set the second spinner
        SecondSpinner = view.findViewById(R.id.secondSpinner);
        // create adapter with spinner items
        final ArrayAdapter<String> secondAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.spinner_resource,
                getResources().getStringArray(R.array.conversionsTo)
        );
        secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SecondSpinner.setAdapter(secondAdapter);
        //spinner event
        SecondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SecondType = SecondSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set the operations spinner
        OperationsSpinner = view.findViewById(R.id.operationsSpinner);
        //create adapter with spinner items
        final ArrayAdapter<String> operationsAdapter = new ArrayAdapter<String>(
                view.getContext(),
                R.layout.spinner_resource,
                getResources().getStringArray(R.array.operations)
        );
        operationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        OperationsSpinner.setAdapter(secondAdapter);
        //spinner event
        OperationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OperationsType = OperationsSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ResultsText = view.findViewById(R.id.ResultsText);
        FirstText = view.findViewById(R.id.FirstNumberText);
        SecondText = view.findViewById(R.id.SecondNumberText);
        Button calculateButton = view.findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onClick(View v) {
                    if (FirstType.equals("Select initial currency…") || SecondType.equals("Select desired currency…")) {
                        Toast toast = Toast.makeText(getActivity(), "Select initial and desired currency…", Toast.LENGTH_SHORT);
                        toast.show();
                    }else if (OperationsType.equals("Select desired operation…")){
                        Toast toast = Toast.makeText(getActivity(), "Select desired operation…", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        if((FirstText.getText().toString()).equals("") || (SecondText.getText().toString()).equals("")){
                            Toast toast = Toast.makeText(getActivity(), "Input amount to convert…", Toast.LENGTH_SHORT);
                            toast.show();
                        }else{//get number to convert
                            calculate((FirstType.split(" "))[0], SecondType, Float.parseFloat(FirstText.getText().toString()), OperationsType, Float.parseFloat(SecondText.getText().toString()));
                    }

                }
            }
        });



        return view;
    }//end onCreateView

    private void calculate(final String from, final String to, final float startingNumber, final String operation, final float secondNumber) {
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


                            float baseInitial = Float.parseFloat(element.getString(from));
                            float baseResult = Float.parseFloat(element.getString(to));

                            float baseInitialConversionCoefficient = EURO / baseInitial;
                            result = startingNumber * baseInitialConversionCoefficient;
                            result *= baseResult;

                            ResultsText.setText(String.format(Locale.US, "%.2f %s", result, to));


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
}

