package com.testname.favoritefragmentfun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ConverterFragment converterFragment;
    private CalculatorFragment calculatorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }//end onCreate

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

        if(item.getItemId() == R.id.menu_marvel) {
            fragmentTransaction.replace(R.id.placeHolderLayout,
                    converterFragment);
        }
        else if(item.getItemId() == R.id.menu_dc) {
            fragmentTransaction.replace(R.id.placeHolderLayout,
                    calculatorFragment);
        }
        else {
            //default
            return super.onContextItemSelected(item);
        }

        //don't forget to commit!!!
        fragmentTransaction.commit();
        return true;
    }//end onOptionsItemSelected
}
