package com.Android.Ubicanos1;

import com.google.android.gms.maps.model.LatLng;




/*
 * Ing. William Medina Romero
 * Grupo Visión | Departamento de Desarrollo
 * 14 de Enero 2013
 * wmedina14@gmail.com
 */

public class Sucursal {

	//Atributos del Objeto
	
	private String direccion;
	private String nombre;
	private String telefono;
	private String horarioSemana;
	private String horarioSabado;
	private String horarioDomingo;
	private LatLng geoPosition;
	private String provincia;
    private float distancia;
	

	
	
	// *** Metodos Set y Get ****
	
	
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public LatLng getGeoPosition() {
		return geoPosition;
	}
	public void setGeoPosition(LatLng geoPosition) {
		this.geoPosition = geoPosition;
	}
	public float getDistancia() {
		return distancia;
	}
	public void setDistancia(float distancia) {
		this.distancia = distancia;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
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
	public String getHorarioSemana() {
		return horarioSemana;
	}
	public void setHorarioSemana(String horarioSemana) {
		this.horarioSemana = horarioSemana;
	}
	public String getHorarioSabado() {
		return horarioSabado;
	}
	public void setHorarioSabado(String horarioSabado) {
		this.horarioSabado = horarioSabado;
	}
	public String getHorarioDomingo() {
		return horarioDomingo;
	}
	public void setHorarioDomingo(String horarioDomingo) {
		this.horarioDomingo = horarioDomingo;
	}
	

	// este metodo retorna la informacion mas importante de la sucursal 
	// Nombre y Telefono.
	public String getLowInfo(){
		return "Telefono: "+telefono ;
	}
	
	
	
	@Override
	public String toString(){
		return "Nombre: "+nombre+"\nDirección: "+direccion+"\nTeléfono: +506 "+telefono+"\n\nHorarios:\n"+"• Lunes a Viernes: "+horarioSemana+"\n• Sábados: "+horarioSabado+"\n• Domingos: "+horarioDomingo;
	}
	
	// *** Constructor de la Clase... ***
	public Sucursal(String direccion, String nombre, String telefono,
			String horarioSemana, String horarioSabado, String horarioDomingo,
			LatLng geoPosition, String provincia) {
	
		this.direccion = direccion;
		this.nombre = nombre;
		this.telefono = telefono;
		this.horarioSemana = horarioSemana;
		this.horarioSabado = horarioSabado;
		this.horarioDomingo = horarioDomingo;
		this.geoPosition = geoPosition;
		this.provincia = provincia;
	}
	
	
	public Sucursal(){		
	}
	
	
}
