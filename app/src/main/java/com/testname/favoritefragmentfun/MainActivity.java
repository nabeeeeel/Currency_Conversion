package com.testname.favoritefragmentfun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    static double[] conversions = {0.0, 0.0};
    private ConverterFragment converterFragment;
    private CalculatorFragment calculatorFragment;
    static final String CURRENT = "CURRENT";
    static final int CONV = R.id.menu_converter;
    static final int CALC = R.id.menu_calculator;
    static int current = CONV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if savedInstance is not null, we create everything
        if (savedInstanceState == null) {
            converterFragment = new ConverterFragment();
            calculatorFragment = new CalculatorFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();

            //begin placement of the fragment
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();

            //set marvel as the default fragment
            //we will use value 0 for the showingFragment (default)
            fragmentTransaction.add(R.id.placeHolderLayout, converterFragment);
            //commit the change
            fragmentTransaction.commit();
        } else { // we set up using the current fragment
            SharedPreferences settings = getPreferences(MODE_PRIVATE);
            current = settings.getInt(CURRENT, CONV);
             setFragment();
        }
    }//end onCreate

    // prefs
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }//end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager =
                getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        // current keeps track of which fragment we are currently on
        // this is for prefs so we can open it on load when we need too
        if(item.getItemId() == R.id.menu_converter) {
            fragmentTransaction.replace(R.id.placeHolderLayout,
                    converterFragment);
            current = CONV;
            setTitle("Currency Converter");
        }
        else if(item.getItemId() == R.id.menu_calculator) {
            fragmentTransaction.replace(R.id.placeHolderLayout,
                    calculatorFragment);
            current = CALC;
            setTitle("Currency Calculator");
        }
        else {
            //default
            return super.onContextItemSelected(item);
        }

        //don't forget to commit!!!
        fragmentTransaction.commit();
        return true;
    }//end onOptionsItemSelected

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        current = savedInstanceState.getInt(CURRENT);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(CURRENT, current);
        setFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CURRENT, current);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        current = settings.getInt(CURRENT, CONV);
        setFragment();
    }

    // sets fragment on load, if we are loaded into another fragment
    private void setFragment() {

        FragmentManager fragmentManager =
                getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        if(current == CONV) {
            setTitle("Currency Converter");
            fragmentTransaction.replace(R.id.placeHolderLayout,
                    converterFragment);
            fragmentTransaction.commit();

        }
        else if(current == CALC) {
            setTitle("Currency Calculator");
            fragmentTransaction.replace(R.id.placeHolderLayout,
                    calculatorFragment);
            fragmentTransaction.commit();

        }
    }

}
