package com.Android.Ubicanos1;

import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/*
 * William Medina Romero 
 * Grupo Visión | Depto Desarrollo
 * 16-11-2012
 * 
 */

public class SucursalAdapter extends BaseAdapter{
	protected Activity activity;
	protected ArrayList<Sucursal> items;
	  
	
	public SucursalAdapter(Activity activity, ArrayList<Sucursal> items){
		this.activity = activity;
		this.items = items;
	}
	
	  
	  
	public int getCount() {
		return items.size();
	}

	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		
		if (convertView == null){
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.consulta_sucursales, null);
		}
		
		
		Sucursal item = items.get(position);
		
		// ImageView image = (ImageView) vi.findViewById(R.id.imageView1);
		
		//ImageView image = (ImageView) vi.findViewById(R.id.i);

		//int imageResource = activity.getResources().getIdentifier(item., defType, defPackage)
		int imageResource = activity.getResources().getIdentifier("drawable-hdpi/boton-de-llamada", null, activity.getPackageName());
		//image.setImageBitmap(activity.getResources().getDrawable(imageResource));
		
		
		
		
		return null;
	}
	
	

}
