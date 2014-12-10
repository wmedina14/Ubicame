package com.Android.Ubicanos1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;




import android.location.Location;
import android.location.LocationManager;
import android.util.Log;


/*
 * Ing. William Medina Romero
 * Grupo Visión | Departamento de Desarrollo
 * 14 de Enero 2013
 * wmedina14@gmail.com
 */

//Esta clase se encarga de Gestionar las sucursales 
public class AdmSucursales {

	private static AdmSucursales admSucursales= null;
	
	private static ArrayList<Sucursal> listaDeSucursales;
	//private static ArrayList<VistaSucursales> listaVistaSucursales;
	
	 private static final String sanJose =  "San José";
	 private static final String alajuela =  "Alajuela";
	 private static final String heredia =   "Heredia";
	 private static final String cartago =   "Cartago";
	 private static final String guanacaste =  "Guanacaste";
	 private static final String puntarenas =  "Puntarenas";
	 private static final String limon =  "Limón";
	 private static boolean procesamiento = false;
	
	
	public static synchronized  AdmSucursales getInstance() throws Exception {
		 if(null == admSucursales){
			 admSucursales = new AdmSucursales();
     }
     return admSucursales;
	}
	
	
	protected AdmSucursales() throws Exception{
		  listaDeSucursales = new ArrayList<Sucursal>();
		 // listaVistaSucursales = new ArrayList<VistaSucursales>();
		  try {
			 for (int i = 0; i < 15000; i++){
			  if (cargaSucursales())
				  return;
		  }
		} catch (Exception e) {
		throw e ;
		}
		  
		  
		 
	}
	
	
	
	public boolean getProcesamiento (){
		return procesamiento;
	}
	
	
	// Este metodo se encarga de cargar todas las sucursales que se encuentran en internet
	public static Boolean cargaSucursales(){
		String result = "";
	
		listaDeSucursales.clear();
		//listaVistaSucursales.clear();
		
		try {
			HttpClient httpCliente = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://www.aseovi.com/opticasvision/sucursales.php");
			
			HttpResponse response = httpCliente.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			
			//Convertir respuesta a las sucursales y agregarlas..
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null){
				sb.append(line+"\n");
			}
			is.close();
			result = sb.toString();
			
			
		} catch (Exception e) {
			Log.e("log_tag","Error en la conexion http: "+e.toString());
			return false;
		}
		
		
		//parse Json data...
		
		try {
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i<jArray.length(); i++){
				JSONObject json_data = jArray.getJSONObject(i);
				/***Creacion de sucursales.**/
				String provincia = json_data.getString("provincia_id");
				
				if(!provincia.equals("null")){
					crearSucursal(json_data.getString("longitud"), json_data.getString("latitud"), json_data.getString("nombre"), json_data.getString("direccion"), json_data.getString("telefono"), json_data.getString("horario_semana"), json_data.getString("horario_sabado"), json_data.getString("horario_domingo"),json_data.getString("provincia_id"));
				}
			}
			procesamiento = true;
			return true;
		} catch (Exception e) {
			Log.e("log_tag","Error convirtiendo datos: "+e.toString());
			return false;
		}
		
	}// fin del metodo....
	
	
	
	/** Provincias
	 * 
	 * 1: San José
	 * 2: Alajuela
	 * 3: Heredia
	 * 4: Cartago
	 * 5: Guanacaste
	 * 6: Puntarenas
	 * 7: Limón
	 *
	 * */
	// Aqui hacemos todos los metodos necesarios para crear la sucursal..
	private static void crearSucursal(String lon, String lat, String nombre, String direccion, String telefono, String horario_semana, String horario_sabado, String horario_domingo, String provinciaId){
		double longitud;
		double latitud;
		longitud = (Double.parseDouble(lon));
		latitud = (Double.parseDouble(lat));
		LatLng gPunto = new LatLng(latitud, longitud);
		String provincia="";
		int i = Integer.parseInt(provinciaId);
		
		switch (i){
		case 1:
			provincia = sanJose;
			break;
		case 2:
			provincia = alajuela;
			break;
		case 3:
			provincia = heredia;
			break;
		case 4:
			provincia = cartago;
			break;
		case 5:
			provincia = guanacaste;
			break;
		case 6:
			provincia = puntarenas;
			break;
		case 7:
			provincia = limon;
			break;
		}
		Sucursal s = new Sucursal(direccion, nombre, telefono, horario_semana, horario_sabado, horario_domingo, gPunto, provincia);
		//VistaSucursales vSucursal = new VistaSucursales(nombre, telefono);
		listaDeSucursales.add(s);
		//listaVistaSucursales.add(vSucursal);
	}// fin del metodo
	
	public int size () {
		return listaDeSucursales.size();
	}
	
	public void calcularDistancia(Location loc){
		Sucursal s;
		Location l = new Location(LocationManager.GPS_PROVIDER);
		double lon;
		double lat;
		LatLng gpunto;
		for (int i = 0; i<listaDeSucursales.size(); i++){
			s = listaDeSucursales.get(i);
			 gpunto = s.getGeoPosition();
			 lon = (double)gpunto.longitude;
			 lat = (double)gpunto.latitude;
			 l.setLatitude(lat);
			 l.setLongitude(lon);
			 s.setDistancia(loc.distanceTo(l));
		}
		ajustar();
	}
	
	
	
	public String getTelefono(String nombre){
		Sucursal s;
		for (int i = 0; i<listaDeSucursales.size();i++){
			s = listaDeSucursales.get(i);
			if (s.getNombre().equals(nombre)){
				return s.getTelefono();
			}
		}
		return "";	
	}
	
	
	
	// Este metodo se encarga de ordenar el arrayList
	// la variable mensurable es la distancia
	public void ajustar(){
		Collections.sort(listaDeSucursales,new ComparadorSucursales());
	}
	
	
	
	
	/*public ArrayList<VistaSucursales> getVistaSucursales(){
	return listaVistaSucursales;	
	}*/
	
	
	
	
	
	// Este metodo se encarga de consultar las sucursales de una provincia
	public ArrayList<Sucursal> getSucursalesXProvincia(String provincia){
		ArrayList<Sucursal> listaSucProvincia = new ArrayList<Sucursal>();
		Sucursal s;
		for (int i = 0;i<listaDeSucursales.size(); i++){
			s = listaDeSucursales.get(i);
			if (s.getProvincia().equals(provincia)){
				listaSucProvincia.add(s);
			}
		}
		return listaSucProvincia;
	}
	
	
	public Sucursal getSucursal(String nombreSucursal){
		Sucursal a;
		Sucursal s = new Sucursal();
		for (int i = 0;i<listaDeSucursales.size(); i++){
			s = listaDeSucursales.get(i);
			if (s.getNombre().equals(nombreSucursal)){
				return s;
			}
		}
		return s;
	}
	
	
	public String sucursalToString(String nombreSucursal){
		String info="";
		Sucursal s;
		for (int i = 0;i<listaDeSucursales.size(); i++){
			s = listaDeSucursales.get(i);
			if (s.getNombre().equals(nombreSucursal)){
				info = s.toString();
			}
		}
		return info;
	}
	
	
	public LatLng getGeoPoint(String nombre){
		LatLng g = new LatLng(40.41, -3.69);
		Sucursal s;
		for (int i = 0;i<listaDeSucursales.size(); i++){
			s = listaDeSucursales.get(i);
			if (s.getNombre().equals(nombre)){
				g = s.getGeoPosition();
			}
		}
		return g;
	}

	
	// Este metodo 
	public ArrayList<Sucursal> getTop(int top){
		ArrayList listaSuc = new ArrayList<Sucursal>();
		for (int i = 0; i<top; i++){
			listaSuc.add(listaDeSucursales.get(i));
		}
		return listaSuc;
	}
	
	
}
