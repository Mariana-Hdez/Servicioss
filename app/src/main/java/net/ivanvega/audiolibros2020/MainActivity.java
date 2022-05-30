package net.ivanvega.audiolibros2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SelectorFragment selectorFragment = new SelectorFragment();

        if (findViewById(R.id.contenedor_pequeno) != null && getSupportFragmentManager().findFragmentById(R.id.contenedor_pequeno) == null){
            getSupportFragmentManager().beginTransaction().add(R.id.contenedor_pequeno, selectorFragment).commit();
        }

        if(getIntent().getExtras()!=null) {
            Log.d("App", "onCreate: Se inició con Intent: " + getIntent().getIntExtra("rep", 0));
            mostrarDetallenotif(getIntent().getIntExtra("rep", 0));
        } else {
            Log.d("App", "Aun no se envua nada");
        }

    }
    DetalleFragment detalleFragment;
    public void mostrarDetalle(int index){

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(fragmentManager.findFragmentById(R.id.detalle_fragment)!=null){

         detalleFragment =
                    (DetalleFragment)
                    fragmentManager.findFragmentById(R.id.detalle_fragment);

            detalleFragment.ponInfoLibro(index);


        }else{
            detalleFragment =
                    new DetalleFragment();

            Bundle bundle = new Bundle();

            bundle.putInt(DetalleFragment.ARG_ID_LIBRO, index);

            detalleFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentManager.beginTransaction().replace(R.id.contenedor_pequeno
            , detalleFragment).addToBackStack(null).commit();

        }

    }

    public void mostrarDetallenotif(int index){
        FragmentManager fragmentManager = getSupportFragmentManager();
        detalleFragment = new DetalleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DetalleFragment.ARG_ID_LIBRO, index);
        bundle.putBoolean("Service", true);
        detalleFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.contenedor_pequeno, detalleFragment).addToBackStack(null).commit();
    }

    @Override
    protected void onDestroy() {
        detalleFragment.destruir();
        super.onDestroy();
    }
}