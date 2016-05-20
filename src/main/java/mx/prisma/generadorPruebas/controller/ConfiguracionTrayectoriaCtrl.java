package mx.prisma.generadorPruebas.controller;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.convention.annotation.Results;

import mx.prisma.util.ActionSupportPRISMA;

@ResultPath("/content/generadorPruebas/")
@Results({@Result(name = "pantallaConfiguracionTrayectoria", type = "dispatcher", location = "configuracion/trayectoriaPrincipal.jsp")})
public class ConfiguracionTrayectoriaCtrl extends ActionSupportPRISMA{

	public String prepararConfiguracionTrayectoria() throws Exception {
		return "pantallaConfiguracionTrayectoria";
	}
}
