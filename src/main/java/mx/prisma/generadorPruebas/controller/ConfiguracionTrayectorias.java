package mx.prisma.generadorPruebas.controller;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.convention.annotation.Results;

import mx.prisma.util.ActionSupportPRISMA;

@ResultPath("/content/generadorPruebas/")
@Results({@Result(name = "pantallaConfiguracionTrayectorias", type = "dispatcher", location = "configuracion/trayectorias.jsp")})
public class ConfiguracionTrayectorias extends ActionSupportPRISMA{

	public String prepararConfiguracion() throws Exception {
		return "pantallaConfiguracionTrayectorias";
	}
}
