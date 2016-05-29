package mx.prisma.generadorPruebas.controller;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.convention.annotation.Results;

import com.opensymphony.xwork2.Action;

import mx.prisma.admin.model.Colaborador;
import mx.prisma.admin.model.Proyecto;
import mx.prisma.bs.AccessBs;
import mx.prisma.editor.model.CasoUso;
import mx.prisma.editor.model.Modulo;
import mx.prisma.util.ActionSupportPRISMA;
import mx.prisma.util.SessionManager;

@ResultPath("/content/generadorPruebas/")
@Results({@Result(name = "pantallaConfiguracionTrayectoria", type = "dispatcher", location = "configuracion/trayectoriaPrincipal.jsp")})
public class ConfiguracionTrayectoriaCtrl extends ActionSupportPRISMA{
	private static final long serialVersionUID = 1L;
	private CasoUso casoUso;
	private CasoUso previo;
	private Proyecto proyecto;
	private Modulo modulo;
	private Colaborador colaborador;
	private Integer idCU;
	public String prepararConfiguracionTrayectoria() throws Exception {
		String resultado;

		colaborador = SessionManager.consultarColaboradorActivo();
		proyecto = SessionManager.consultarProyectoActivo();
		modulo = SessionManager.consultarModuloActivo();
		casoUso = SessionManager.consultarCasoUsoActivo();
		if (casoUso == null) {
			resultado = "cu";
			return resultado;
		}
		if (modulo == null) {
			resultado = "modulos";
			return resultado;
		}
		if (!AccessBs.verificarPermisos(modulo.getProyecto(), colaborador)) {
			resultado = Action.LOGIN;
			return resultado;
		}
		return "pantallaConfiguracionTrayectoria";
	}
	
	public CasoUso getCasoUso() {
		return casoUso;
	}

	public void setCasoUso(CasoUso casoUso) {
		this.casoUso = casoUso;
	}

	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}

	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public Colaborador getColaborador() {
		return colaborador;
	}

	public void setColaborador(Colaborador colaborador) {
		this.colaborador = colaborador;
	}

	public Integer getIdCU() {
		return idCU;
	}

	public void setIdCU(Integer idCU) {
		this.idCU = idCU;
	}
}
