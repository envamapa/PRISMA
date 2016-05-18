package mx.prisma.generadorPruebas.bs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.prisma.bs.ReferenciaEnum.TipoReferencia;
import mx.prisma.bs.TipoDatoEnum;
import mx.prisma.bs.TipoReglaNegocioEnum;
import mx.prisma.editor.bs.ReglaNegocioBs;
import mx.prisma.editor.model.Accion;
import mx.prisma.editor.model.Atributo;
import mx.prisma.editor.model.CasoUso;
import mx.prisma.editor.model.CasoUsoReglaNegocio;
import mx.prisma.editor.model.Entrada;
import mx.prisma.editor.model.Mensaje;
import mx.prisma.editor.model.Pantalla;
import mx.prisma.editor.model.Paso;
import mx.prisma.editor.model.ReferenciaParametro;
import mx.prisma.editor.model.ReglaNegocio;
import mx.prisma.editor.model.Trayectoria;
import mx.prisma.generadorPruebas.dao.EscenarioDAO;
import mx.prisma.generadorPruebas.dao.ValorEntradaDAO;
import mx.prisma.generadorPruebas.model.Escenario;
import mx.prisma.generadorPruebas.model.EscenarioValorEntrada;
import mx.prisma.generadorPruebas.model.ValorEntrada;
import mx.prisma.util.GeneradorCadenasUtil;
import mx.prisma.util.PRISMARandomUtil;

public class CuPruebasBs {
	
	public static void generarEscenarios(Set<Entrada> entradas, Set<CasoUsoReglaNegocio> casoUso_reglaNegocio) throws Exception {
		Set<ReglaNegocio> reglas = new HashSet<ReglaNegocio>(0);
		for(CasoUsoReglaNegocio curn : casoUso_reglaNegocio) {
			reglas.add(curn.getReglaNegocio());
		}
		
		generarValores(entradas, reglas);
		
		
		for(Entrada entradaPrivote : entradas) {
			generarEscenariosEntrada(entradaPrivote);
		}
	}

	
	private static void generarEscenariosEntrada(Entrada entrada) {
		
		Set<ValorEntrada> valoresValidos = obtenerValoreValidos(entrada.getCasoUso());
		Set<ValorEntrada> valoresInvalidosEntrada = obtenerValoresInvalidos(entrada);
		
		
		for(ValorEntrada valorInvalidoEntrada : valoresInvalidosEntrada) {
			Escenario escenario = new Escenario();
			Set<EscenarioValorEntrada> valoresEntrada = new HashSet<EscenarioValorEntrada>(0);
			//Set<ValorEntrada> valoresEntrada = new HashSet<ValorEntrada>(0);
			EscenarioValorEntrada eve = new EscenarioValorEntrada();
			eve.setEscenario(escenario);
			eve.setValorEntradaid(valorInvalidoEntrada);
			valoresEntrada.add(eve);
			//valoresEntrada.add(valorInvalidoEntrada);
			for(ValorEntrada valorValido : valoresValidos) {
				if(valorValido.getEntrada().getId() != entrada.getId()) {
					eve = new EscenarioValorEntrada();
					eve.setEscenario(escenario);
					eve.setValorEntradaid(valorValido);
					valoresEntrada.add(eve);
					//valoresEntrada.add(valorValido);
				}
			}
			
			escenario.setValoresEntrada(valoresEntrada);
			new EscenarioDAO().registrarEscenario(escenario);
		}
	}

	private static Set<ValorEntrada> obtenerValoresInvalidos(Entrada entrada) {
		Set<ValorEntrada> valoresInvalidos = new HashSet<ValorEntrada>(0);
		List<ValorEntrada> valores = new ValorEntradaDAO().consultarValoresInvalidos(entrada);
		if(valores != null) {
			for(ValorEntrada valor : valores) {
				valoresInvalidos.add(valor);
			}
		}
		return valoresInvalidos;
	}

	private static Set<ValorEntrada> obtenerValoreValidos(CasoUso casoUso) {
		Set<ValorEntrada> valoresValidos = new HashSet<ValorEntrada>(0);
		
		for(Entrada entrada : casoUso.getEntradas()) {
			ValorEntrada valorValido = new ValorEntradaDAO().consultarValorValido(entrada);
			valoresValidos.add(valorValido);
		}
		return valoresValidos;
	}

	public static void generarValores(Set<Entrada> entradas,
			Set<ReglaNegocio> reglasNegocio) throws Exception {
		for(ReglaNegocio reglaNegocio: reglasNegocio) {
				for(Entrada entrada : entradas) {
					if(entrada.getAtributo() != null) {

						ValorEntrada valorInvalidoBD = null;
						String valorCadenaInvalido = null;
						
						valorInvalidoBD = ValorEntradaBs.consultarValorInvalido(reglaNegocio, entrada);
						
						if(valorInvalidoBD == null) {
							valorInvalidoBD = new ValorEntrada();
							valorInvalidoBD.setReglaNegocio(reglaNegocio);
							valorInvalidoBD.setEntrada(entrada);
							valorInvalidoBD.setValido(false);
						}
						
						if(!ReglaNegocioBs.esGlobal(reglaNegocio.getTipoReglaNegocio())) {
							if(entradaPerteneceReglaNegocio(entrada, reglaNegocio)) {
								valorCadenaInvalido = generarValor(entrada, reglaNegocio, reglasNegocio, false);
							}
						} else {
							valorCadenaInvalido = generarValor(entrada, reglaNegocio, reglasNegocio, false);
						}
						
						if(valorCadenaInvalido != null) { 
							valorInvalidoBD.setValor(valorCadenaInvalido);
							ValorEntradaBs.registrarValorEntrada(valorInvalidoBD);
						}
					} 
					
				}
		}
		
		for(Entrada entrada : entradas) {
			ValorEntrada valorValidoBD = ValorEntradaBs.consultarValorValido(entrada);
			String valorCadenaValido = null;
			
			if(valorValidoBD == null) {
				valorValidoBD = new ValorEntrada();
				valorValidoBD.setReglaNegocio(null);
				valorValidoBD.setEntrada(entrada);
				valorValidoBD.setValido(true);
			}
			
			if(entrada.getAtributo() != null) {	
				valorCadenaValido = generarValor(entrada, null, reglasNegocio, true);
			} else if(entrada.getTerminoGlosario() != null) {
				valorCadenaValido = "1";
			}
			
			if(valorCadenaValido != null) {
				valorValidoBD.setValor(valorCadenaValido);
				ValorEntradaBs.registrarValorEntrada(valorValidoBD);
			}
		}
	}
	
	
	private static boolean entradaPerteneceReglaNegocio(Entrada entrada,
			ReglaNegocio reglaNegocio) {
		Atributo atributo = entrada.getAtributo();
		switch(TipoReglaNegocioEnum.getTipoReglaNegocio(reglaNegocio.getTipoReglaNegocio())) {
		case FORMATOCAMPO:
			if(reglaNegocio.getAtributoExpReg().getId() == atributo.getId()) {
				return true;
			} else {
				return false;
			}
		case COMPATRIBUTOS:
			if(reglaNegocio.getAtributoComp1().getId() == atributo.getId() || reglaNegocio.getAtributoComp2().getId() == atributo.getId()) {
				return true;
			} else {
				return false;
			}
		case UNICIDAD:
			if(reglaNegocio.getAtributoUnicidad().getId() == atributo.getId()) {
				return true;
			} else {
				return false;
			}
			default:
				break;
		
		}
		return false;
	}

	private static String generarValor(Entrada entrada,
			ReglaNegocio reglaNegocio, Set<ReglaNegocio> reglasNegocio,
			boolean valido) {
		Atributo atributo = entrada.getAtributo();
		String valorCadena = null;

		if(TipoDatoEnum.getTipoDato(atributo.getTipoDato()).equals(TipoDatoEnum.tipoDato.OTRO)) {
			return null;
		}
		
		if(valido) {
			valorCadena = generarCadenaValidaGlobal(entrada, reglasNegocio);
		} else {
			switch(TipoReglaNegocioEnum.getTipoReglaNegocio(reglaNegocio.getTipoReglaNegocio())) {
				case DATOCORRECTO:
					if(TipoDatoEnum.getTipoDato(atributo.getTipoDato()).equals(TipoDatoEnum.tipoDato.CADENA) ) {
						return null;
					}
					valorCadena = "a";
					break;
				/*case FORMATOCAMPO:
					valorCadena = GeneradorCadenasUtil.generarCadenaAleatoria(4, true) + "xX01_ ?";
					break;*/
				case LONGITUD:
					if(TipoDatoEnum.getTipoDato(atributo.getTipoDato()).equals(TipoDatoEnum.tipoDato.BOOLEANO) 
							|| TipoDatoEnum.getTipoDato(atributo.getTipoDato()).equals(TipoDatoEnum.tipoDato.FECHA)) {
						return null;
					}
					boolean numero = false;
					if(TipoDatoEnum.getTipoDato(atributo.getTipoDato()).equals(TipoDatoEnum.tipoDato.CADENA)) {
						numero = false; 
					}
					if(TipoDatoEnum.getTipoDato(atributo.getTipoDato()).equals(TipoDatoEnum.tipoDato.ENTERO) ||
							TipoDatoEnum.getTipoDato(atributo.getTipoDato()).equals(TipoDatoEnum.tipoDato.FLOTANTE)) {
						numero = true; 
					}
					valorCadena = GeneradorCadenasUtil.generarCadenaInvalida(atributo.getLongitud(), numero);
					break;
				case OBLIGATORIOS:
					if(entrada.getAtributo().isObligatorio()) {
						valorCadena = "";
					} else {
						return null;
					}
					break;
				default:
					return null;
			}
		}

		return valorCadena;
	}

	public static String generarCadenaValidaFormatoCorrecto(Entrada entrada, ReglaNegocio reglaNegocio) {
		return GeneradorCadenasUtil.generarCadena(reglaNegocio.getExpresionRegular());
	}
	
	public static String generarCadenaValidaComparacionAtributos(Entrada entrada, String valor, ReglaNegocio reglaNegocio) {
		return null;
	}
	
	public static String generarCadenaValidaUnicidad() {
		return null;
	}
	
	
	public static String generarCadenaValidaGlobal(Entrada entrada,
			Set<ReglaNegocio> reglasNegocio) {
		Atributo atributo = entrada.getAtributo();		
		
		String cadenaValida = null;
		
		switch(TipoDatoEnum.getTipoDato(atributo.getTipoDato())) {
		case ARCHIVO:
			cadenaValida = null;
			break;
		case BOOLEANO:
			if(PRISMARandomUtil.generarRandomBooleano() || atributo.isObligatorio()) {
				cadenaValida = "true";
			} else {
				cadenaValida = "false";
			}
			break;
		case CADENA:
			cadenaValida = GeneradorCadenasUtil.generarCadenaAleatoria(atributo.getLongitud(), atributo.isObligatorio());
			break;
		case ENTERO:
			cadenaValida = GeneradorCadenasUtil.generarEnteroAleatorio(atributo.getLongitud());
			break;
		case FECHA:
			cadenaValida = GeneradorCadenasUtil.generarFechaAleatoria();
			break;
		case FLOTANTE:
			cadenaValida = GeneradorCadenasUtil.generarFlotanteAleatorio(atributo.getLongitud());
			break;
		case OTRO:
			cadenaValida = null;
			break;
		default:
			break;
		}
		return cadenaValida;
	}

	/*public List<Campo> obtenerCamposPrueba(Trayectoria trayectoria, boolean esPruebaCompleta) {
		List<Campo> campos = new ArrayList<Campo>();
		for(Paso pasox : trayectoria.getPasos()) {
			if(AnalizadorPasosBs.isActorOprimeBoton(pasox)) {
				Accion accion = AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.ACCION).getAccionDestino();
				campos.add(new Campo(accion.getNombre(), "URL", null));
			}
			if(AnalizadorPasosBs.isActorIngresaDatos(paso)){
				for(Entrada entrada : trayectoria.getCasoUso().getEntradas()) {
					campos.add(new Campo(entrada.getAtributo().getNombre(), "Valor", null));
					
					if(esPruebaCompleta) {
						campos.add(new Campo(entrada.getAtributo().getNombre(), "Etiqueta", null));
					}
				}
			}
			if(esPruebaCompleta) {
				if(AnalizadorPasosBs.isSistemaMuestraMensaje(paso)) {
					Mensaje mensaje = (Mensaje) AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.MENSAJE).getElementoDestino();
					if(mensaje.isParametrizado()) {
						for(MensajeParametro mensajeParametro : mensaje.getParametros()) {
							campos.add(new Campo(mensajeParametro.getParametro().getNombre(), "ParametroMensaje", null));
						}
					}
				}
				if(AnalizadorPasosBs.isSistemaMuestraPantalla(paso)) {
					Pantalla pantalla = (Pantalla) AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.PANTALLA).getElementoDestino();
					campos.add(new Campo(pantalla.));
				}
			}
			
		}
		return campos;
	}
*/

	public static List<Accion> obtenerAcciones(
			Trayectoria trayectoria) {
		List<Accion> acciones = new ArrayList<Accion>();
		for(Paso paso : trayectoria.getPasos()) {
			if(AnalizadorPasosBs.isActorOprimeBoton(paso)) {				
				Accion accion = AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.ACCION).getAccionDestino();
				acciones.add(accion);
			}
		}
		return acciones;
	}


	public static Set<Pantalla> obtenerPantallas(
			Trayectoria trayectoria) {
		Set<Pantalla> pantallas = new HashSet<Pantalla>(0);
		for(Paso paso : trayectoria.getPasos()) {
			if(AnalizadorPasosBs.isSistemaMuestraPantalla(paso)) {
				Pantalla pantalla = (Pantalla) AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.PANTALLA).getElementoDestino();
				pantallas.add(pantalla);
			}
		}
		return pantallas;
	}





	public static Set<ReferenciaParametro> obtenerReferenciasReglasNegocioQuery(
			Trayectoria trayectoria) {
		Set<ReferenciaParametro> referencias = new HashSet<ReferenciaParametro>(0);
		
		for(Paso paso : trayectoria.getPasos()) {
			if(AnalizadorPasosBs.isSistemaValidaReglaNegocio(paso) || AnalizadorPasosBs.isSistemaValidaPrecondicion(paso)) {
				ReferenciaParametro referenciaReglaNegocio = AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.REGLANEGOCIO);
				ReglaNegocio reglaNegocio = (ReglaNegocio) referenciaReglaNegocio.getElementoDestino();
				switch(TipoReglaNegocioEnum.getTipoReglaNegocio(reglaNegocio.getTipoReglaNegocio())) {
				case UNICIDAD:
					referencias.add(referenciaReglaNegocio);
					break;
				case VERFCATALOGOS:
					referencias.add(referenciaReglaNegocio);
					break;
				default:
					break;
					
				}
			}
		}
		
		return referencias;
	}


	public static List<ReferenciaParametro> obtenerReferenciasParametroMensajes(
			Trayectoria trayectoria) {
		List<ReferenciaParametro> referencias = new ArrayList<ReferenciaParametro>();
		for(Paso paso : trayectoria.getPasos()) {
			if(AnalizadorPasosBs.isSistemaMuestraMensaje(paso)) {
				ReferenciaParametro referencia = AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.MENSAJE);
				Mensaje mensaje = (Mensaje) referencia.getElementoDestino();
				if(mensaje.getParametros() != null && mensaje.getParametros().size() > 0) {
					referencias.add(referencia);
				}
				
			}
		}
		return referencias;
	}
		
}
