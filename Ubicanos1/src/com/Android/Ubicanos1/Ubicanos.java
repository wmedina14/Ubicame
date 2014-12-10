package com.Android.Ubicanos1;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.Android.Ubicanos1.R;


import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



public class Ubicanos extends FragmentActivity {


	private AdmSucursales admSucursales;
	private ProgressDialog pd = null;
	private boolean procesamiento;
	private AsyncTask<integer, Void, Object> a;

	private GoogleMap mapa;
	private LocationManager locManager;
	private LocationListener loclistener;
	private CameraUpdate camara;
	
	private static final int MNU_MAPA_SATELITAL = 1;
	private static final int MNU_MAPA_HIBRIDO = 2;
	private static final int MNU_MAPA_NORMAL = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ubicanos);

		mapa = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		
		mapa.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker arg0) {
				call(arg0.getTitle());
			}
		});
		
	
		mostraeMensajeInicial();
		
		Bundle bundle = getIntent().getExtras();
		if(bundle.getString("modo") != null){
			String variables = bundle.getString("modo");
			if (variables.equals("verSucursal")){
				modoVerSucursal(bundle.getString("nomSucursal"));
			}else if (variables.equals("ubicanos")) {
				modoUbicanos();
			}
		}//fin if

	}// fin metodo "onCreate"
	
	
	 private void modoUbicanos(){
		 try {
				admSucursales = AdmSucursales.getInstance();
				initGPS_Seguimiento();
			} catch (Exception e) {
				e.printStackTrace();
			}
	 }
	
	 private void modoVerSucursal(String nombreSucursal){
		 this.setTitle("Consultando Sucursal"); 
		 try {
			admSucursales = AdmSucursales.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 Sucursal s = admSucursales.getSucursal(nombreSucursal);
		 mapa.addMarker(new MarkerOptions()
			.position(s.getGeoPosition())
			.title(s.getNombre())
			.snippet(s.getLowInfo())
			.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.marker)));
		 
			camara = CameraUpdateFactory.newLatLngZoom(new LatLng(s.getGeoPosition().latitude, s.getGeoPosition().longitude), 16);

			mapa.animateCamera(camara);
			
	 }// fin modoVerSucursal.
	
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(Menu.NONE, MNU_MAPA_SATELITAL, Menu.NONE, "Mapa Satelital").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				return false;
			}
		});
		
		menu.add(Menu.NONE, MNU_MAPA_HIBRIDO, Menu.NONE, "Mapa Hibrido").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				return false;
			}
		});
		
		menu.add(Menu.NONE, MNU_MAPA_NORMAL, Menu.NONE, "Mapa Normal").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				return false;
			}
		});
		
		
		return true;
	}

	
	
	// este metodo se encarga de mostrar un mensaje inicial al usuario
	// para indicarle que puede obtenergetLowInfo
	private void mostraeMensajeInicial() {

		AlertDialog.Builder alerta = new AlertDialog.Builder(this);

		alerta.setMessage(getString(R.string.gMensaje_InFormacion_Datos_Sucursales));
		alerta.setTitle(getString(R.string.gTitulo_InFormacion_Datos_Sucursales));
		alerta.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// nothig..
				
			}
		});
		
		alerta.show();
	}

	// ************** Programacion del Sistema *************** //

	// Este metodo se encarga de cargar los datos de las sucursales en el
	// Sistema.
	private void obtenerDatosSucursales() {
		procesamiento = false;
		pd = ProgressDialog.show(this, "Por favor espere",
				"Cargando Sucursales", true, false);

		try {
			a = new DownloadTask().execute(); // aqui llamamos la clase.. admite
												// parametros...
			// new cronometro().execute();
		} catch (Exception e) {
			Toast.makeText(
					this,
					"Hubo un error procesando las sucursales.\nPor Favor vuelva a intentarlo",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void mostrarMensaje(int codigo) {

		switch (codigo) {
		case 1:// todo esta bien
				// Toast.makeText(this, "Carga completa",
				// Toast.LENGTH_SHORT).show();
			procesamiento = true;
			break;
		case 2:// error al obtener los datos...
			Toast.makeText(this, "Error al obtener los datos",
					Toast.LENGTH_SHORT).show();
			this.finish();
			this.closeContextMenu();
			break;
		case 3:// time out
			Toast.makeText(
					this,
					"No se ha podido comunicar con internet. \n Intentelo de nuevo",
					Toast.LENGTH_SHORT).show();
			this.finish();
			this.closeContextMenu();
			break;

		}

	}

	// hilo de la aplicacion..
	private class DownloadTask extends AsyncTask<integer, Void, Object> {

		protected Object doInBackground(integer... args) {

			try {
				admSucursales = AdmSucursales.getInstance();
			} catch (Exception e) {
				return 2;
			}
			if (admSucursales.getProcesamiento()) {
				return 1;
			}

			if (admSucursales == null) {
				return 2;
			}
			return 2;
		}// fin del 1 metodoo...

		protected void onPostExecute(Object result) {
			// Pasamos el resultado de los datos a la Activity principal
			try {
				if (Ubicanos.this.pd != null) {
					Ubicanos.this.pd.dismiss();
				}
			} catch (Exception e) {

			}

			mostrarMensaje((Integer) result);

		}// fin del 2 metodo..
	}// fin de la clase del hilo..

	private class cronometro extends AsyncTask<integer, Void, Object> {

		@Override
		protected void onPostExecute(Object result) {
			mostrarMensaje((Integer) result);
		}// fin del 2 metodo..

		@Override
		protected Object doInBackground(integer... params) {

			SystemClock.sleep(10000);

			if (!procesamiento) {
				pd.cancel();
				if (!a.cancel(true)) {
					return 1;
				}
				return 3;
			}
			return 1;
		}// fin background...

	}

	// Este metodo inicia el servicio GPS, para asi
	// poder acceder a sus recursos..
	private void initGPS_Seguimiento() {
		try {

			locManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
			Location loc = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (loc != null) {
				actualizarTodo(loc);
			}
			// actualizarLocalizacionUsuarioMapa(loc);

			// nos registramos para recibir actualizaciones de la posicion..
			loclistener = new LocationListener() {
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}

				public void onProviderEnabled(String provider) {
					Toast.makeText(getApplicationContext(),
							"El proveedor esta Habilitado", Toast.LENGTH_SHORT)
							.show();
				}

				public void onProviderDisabled(String provider) {
					Toast.makeText(
							getApplicationContext(),
							"GPS Deshabilitado. \nEs necesario activarla para poder ubicar su localización actual",
							Toast.LENGTH_LONG).show();
					finish();
				}

				public void onLocationChanged(Location location) {
					actualizarTodo(location);
				}
			};
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					25000, 0, loclistener);
		} catch (Exception e) {
			Toast.makeText(
					this,
					"Hubo un error procesando su localización.\nPor Favor vuelva a intentarlo",
					Toast.LENGTH_SHORT).show();
		}

	}// fin del metodo..

	// Este metodo se encarga de actualizar datos en modo Ubicanos...
	// es necesario introducir primero la capa de usuario xq
	// la ultima capa es la que responde a los eventos unicamente (ej: onTap)
	private void actualizarTodo(Location location) {
		mapa.clear();

		actualizarLocalizacionUsuarioMapa(location);//

		// listaCapas.clear();//limpiamos todas las capas..

		if (admSucursales != null) {
			admSucursales.calcularDistancia(location);// primero calculamos la
														// distancia
			actualizaSucursales();// actualizamos la ubicacion de las
									// sucursales..
		}

	}

	// Capas... la posicion 0 -- Sucursales
	// la posicion 1 -- Usuario
	private void actualizarLocalizacionUsuarioMapa(Location localizacion) {
		double lat = (localizacion.getLatitude());
		double lon = (localizacion.getLongitude());
		float zoom = 16;

		

		
		mapa.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
				.title(getString(R.string.gMapsUsuario))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario)));

		// gPunto = new LatLng(lat,lon );
		// camara = CameraUpdateFactory.newLatLng(new LatLng(lat,lon));

		//camara = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), zoom);
		camara = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));

		mapa.animateCamera(camara);

		// mapController.animateTo(gPunto);
		// CapaUsuario cs = new CapaUsuario(gPunto);
		// listaCapas.add(cs);//se agrega la capa de Usuario pero en la posicion
		// 1
		// mapView.postInvalidate();
	}

	public void actualizaSucursales() {
		ArrayList<Sucursal> sucursales = admSucursales.getTop(5);
		Sucursal s;

		for (int i = 0; i < sucursales.size(); i++) {
			s = sucursales.get(i);
			mapa.addMarker(new MarkerOptions()
					.position(s.getGeoPosition())
					.title(s.getNombre())
					.snippet(s.getLowInfo())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.marker)));
		}

	}// fin del metodo actualizaSucursales
	

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

}
