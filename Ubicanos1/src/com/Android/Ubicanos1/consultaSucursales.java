
package com.Android.Ubicanos1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.AdapterView.OnItemLongClickListener;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

/*
 * Opticas Vision
 * William Medina Romero
 * Departamento de Desarrollo. 
 */
public class consultaSucursales extends Activity{

	private Spinner sProvincias;
	private ListView listaSucursales;
	private Button bConsultar;
	final String[] datos = new String[]{"San José","Alajuela","Heredia","Cartago","Guanacaste","Puntarenas","Limón"};
	private AdmSucursales admSucursales;
	String[] listaS;
	static final String infoSucursal = "InfoSucursal";
	static final String nomSucursal = "nomSucursal";

	
	/** Called when the activity is first created. **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.consulta_sucursales);
        sProvincias = (Spinner) findViewById(R.id.sProvincias);
        listaSucursales = (ListView) findViewById(R.id.list);
        
        
        
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, datos);
        adaptador.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        
        
        sProvincias.setAdapter(adaptador);
        initListeners();
        try {
			admSucursales = AdmSucursales.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    
    
    // Este metodo se encarga de inicializar los listernes de los componentes...
    private void initListeners(){
    	sProvincias.setOnItemSelectedListener(
    	        new AdapterView.OnItemSelectedListener() {
    	        public void onItemSelected(AdapterView<?> parent,
    	            android.view.View v, int position, long id) {
    	             cargaListaSucursales(sProvincias.getSelectedItem().toString());
    	        }
    	        public void onNothingSelected(AdapterView<?> parent) {
    	        }
    	});
    	
    listaSucursales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				mostrarInfoSucursal(listaS[position]);
			}
		});
    	
    	listaSucursales.setOnItemLongClickListener(new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
    			call(listaS[position]);
    			   return false;
		}
    		});
    	
    }// fin de initListeners...
    
    public void mostrarInfo(String mensaje){
    	Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
    	
    }
    
    
    // Este metodo se encarga de realizar una llamada
    private void call(String nombreSucursal) {
    	String numero = admSucursales.getTelefono(nombreSucursal);
    	String numeroFormateado = numero.replaceAll("-", "");
    	Intent callIntent;
    	
    	if (!(numero.equals("") || (numero.equals(null)))){
    		try {
            callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+numeroFormateado));
            startActivity(callIntent);
            return;
        } catch (ActivityNotFoundException activityException) {
           Toast.makeText(this, "Ha ocurrido un error. No se pudo realizar la llamada", Toast.LENGTH_LONG).show();
           return;
        }
    	}
    	Toast.makeText(this, "Lo sentimos. La sucursal no tiene un numero de teléfono.", Toast.LENGTH_LONG).show();
    }
    
    
    
    
    // 	Este metodo se encarga de mostrar la informacion de una sucursal..
    private void mostrarInfoSucursal(String nombre){
    	Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Ubicanos.class));
    	intent.putExtra(nomSucursal, nombre);
    	intent.putExtra("modo" , "verSucursal");
    	intent.putExtra(nomSucursal, nombre);
    	startActivity(intent);
    }
    
    
    // Este metodo carga la lista de las sucursales de acuerdo a una Provincia...
    private void cargaListaSucursales (String provincia){
    	ArrayList<Sucursal> lista = admSucursales.getSucursalesXProvincia(provincia);
    	listaS = new String[lista.size()];
    	Sucursal s;
    	for (int i = 0; i<lista.size(); i++){
    		s = lista.get(i);
    		listaS[i]=s.getNombre();
    	}
    	Arrays.sort(listaS);
    	 ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                 android.R.layout.simple_list_item_1, listaS);
    	listaSucursales.setAdapter(adaptador);
    }// fin del metodo...
 
    
    
    
/* 
 * 
class AdaptadorSucursales extends ArrayAdapter {
	 
    Activity context;
 
    AdaptadorSucursales(Activity context) {
            super(context, R.layout.consulta_sucursales, admSucursales.getVistaSucursales());
            this.context = context;
        }
 
        public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.consulta_sucursales, null);
 
        return(item);
    }
    
}*/


}
