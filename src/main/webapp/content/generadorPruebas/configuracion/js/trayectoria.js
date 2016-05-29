function aceptar() {
	$('form').get(0).setAttribute("action", contextPath + "/configuracion-trayectorias!prepararConfiguracion");
	//if(prepararEnvio()) {
		$('form').get(0).submit();		
		
	//}
}
