package com.Android.Ubicanos1;

public class VistaSucursales {
 
	private String nombre; 
	private String telefono;
	
	
	
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	
		public VistaSucursales(String nombre, String telefono) {
		this.nombre = nombre;
		this.telefono = telefono;
	}
	
	
	
}
