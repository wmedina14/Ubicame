package com.Android.Ubicanos1;




import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Ubicanos1Activity extends Activity {

	private Button bVerTodasSucursales;
	private AdmSucursales admSucursales;
	private Button bUbicanos;
	private Button bTiendaVirtual;
	private ImageView imgBuAbrir;
	public static final String ubicanos = "ubicanos";
	public static final String verSucursal = "verSucursal";
	public static final String verMapa = "verMapa";
	private ProgressDialog pd = null;
	private boolean procesamiento;
	

	private AsyncTask<integer, Void, Object> a;

	/** Called when the activity is first created. **/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		procesamiento = false;
		pd = ProgressDialog.show(this, "Por favor espere",
				"Cargando Sucursales...", true, false);
		bVerTodasSucursales = (Button) findViewById(R.id.bTodasSucursales);
		bUbicanos = (Button) findViewById(R.id.bUbicame);
		imgBuAbrir = (ImageView) findViewById(R.id.ibAbrirPagina);
		bTiendaVirtual = (Button) findViewById(R.id.bTiendaVirtual);
		initListeners();

		try {
			a = new DownloadTask().execute(); // aqui llamamos la clase.. admite
											// parametros...
			new cronometro().execute();
		} catch (Exception e) {
			Toast.makeText(this, "Hubo un error procesando las sucursales.\nPor Favor vuelva a intentarlo", Toast.LENGTH_SHORT).show();
		}

	}

	// Este metodo se encarga de inicializar los Listener de los botones
	private void initListeners() {
		bVerTodasSucursales.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buscarSucursal();
			}
		});
		bUbicanos.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ubicanos();
			}
		});
		imgBuAbrir.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				abrirPagina("http://www.opticasvision.co.cr/");
			}
		});
		
		bTiendaVirtual.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				abrirPagina("http://www.grupovisiontienda.com/");
			}
		});
		
	}// Fin del metodo...

	/*
	 * Este metodo se encarga de abrir la paginas web de Opticas Vision
	 */
	private void abrirPagina(String url) {
		Intent i = new Intent("android.intent.action.VIEW",
				Uri.parse(url));
		startActivity(i);
	}

	/*
	 * Este metodo se encarga de mostrar todas las Sucursales...
	 */
	private void buscarSucursal() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(this, consultaSucursales.class));
		intent.putExtra("modo", verSucursal);
		startActivity(intent);
	}

	/*
	 * Este metodo se mostras las 5 sucursales mas cercanas...
	 */
	private void ubicanos() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(this, Ubicanos.class));
		intent.putExtra("modo", ubicanos);
		startActivity(intent);
	}

	private void mostrarMensaje(int codigo) {

		switch (codigo) {
		case 1:// todo esta bien
			procesamiento = true;
			break;
		case 2:// error al obtener los datos...
			Toast.makeText(this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
			this.finish();
			this.closeContextMenu();
			break;
		case 3:// time out
			Toast.makeText(this, "No se ha podido comunicar con internet. \n Intentelo de nuevo", Toast.LENGTH_SHORT).show();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(admSucursales.getProcesamiento()){
				return 1;
			}
			
			if (admSucursales == null) {
				return 2;
			}
			return 2;
		}// fin del 1 metodoo...

		protected void onPostExecute(Object result) {
			// Pasamos el resultado de los datos a la Activity principal
			
			mostrarMensaje((Integer) result);
			if (Ubicanos1Activity.this.pd != null) {
				Ubicanos1Activity.this.pd.dismiss();
			}
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
				if(!a.cancel(true)){
					return 1;
				}
				return 3;
			}
			return 1;
		}// fin background...

	}

}