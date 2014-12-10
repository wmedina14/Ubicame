package com.Android.Ubicanos1;


import java.util.Comparator;

public class ComparadorSucursales implements Comparator{

	public int compare(Object sucursal1, Object sucursal2) {
		Sucursal s1 = (Sucursal)sucursal1;
		Sucursal s2 = (Sucursal)sucursal2;
		
		 if (s1.getDistancia()<s2.getDistancia())
			 return -1;
		 else
			 return 1;
	}

}
