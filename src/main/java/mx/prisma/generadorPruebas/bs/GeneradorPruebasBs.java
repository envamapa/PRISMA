package mx.prisma.generadorPruebas.bs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mx.prisma.bs.ReferenciaEnum;
import mx.prisma.bs.ReferenciaEnum.TipoReferencia;
import mx.prisma.bs.TipoReglaNegocioEnum;
import mx.prisma.bs.TipoReglaNegocioEnum.TipoReglaNegocioENUM;
import mx.prisma.editor.bs.CuBs;
import mx.prisma.editor.bs.TokenBs;
import mx.prisma.editor.dao.EntradaDAO;
import mx.prisma.editor.dao.ReferenciaParametroDAO;
import mx.prisma.editor.model.Accion;
import mx.prisma.editor.model.CasoUso;
import mx.prisma.editor.model.Entrada;
import mx.prisma.editor.model.Mensaje;
import mx.prisma.editor.model.MensajeParametro;
import mx.prisma.editor.model.Pantalla;
import mx.prisma.editor.model.Paso;
import mx.prisma.editor.model.ReferenciaParametro;
import mx.prisma.editor.model.ReglaNegocio;
import mx.prisma.editor.model.Trayectoria;
import mx.prisma.generadorPruebas.bs.AnalizadorPasosBs.TipoPaso;
import mx.prisma.generadorPruebas.dao.ConfiguracionDAO;
import mx.prisma.generadorPruebas.model.ConfiguracionBaseDatos;
import mx.prisma.generadorPruebas.model.ConfiguracionHttp;
import mx.prisma.generadorPruebas.model.Query;
import mx.prisma.generadorPruebas.model.ValorEntrada;
import mx.prisma.generadorPruebas.model.ValorMensajeParametro;
/*import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;*/

public class GeneradorPruebasBs {
	private static String prefijoCSV = "csv_";
	private static final String prefijoPeticionJDBC = "PJ"; 
	private static final String prefijoPeticionHTTP = "PH";
	private static final String prefijoControladorIf = "CI";
	private static final String prefijoAsercion = "AS";
	private static final String prefijoContenedorCSV = "CSV";
	private static String casoUsoTesting = "";
	private static boolean caminoIdealIncierto = true; 
	private static Paso pasoIncierto;

	public static String encabezado() {
		String bloque = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"
				+ "<jmeterTestPlan version=\"1.2\" properties=\"2.6\" jmeter=\"2.11 r1554548\">" + "\n";
		
		return bloque;
	}
	
	public static String planPruebas() {
		String bloque = 
				"<hashTree>" + "\n"
				+ "<TestPlan guiclass=\"TestPlanGui\" testclass=\"TestPlan\" testname=\"Plan de Pruebas\" enabled=\"true\">" + "\n"
				+ "<stringProp name=\"TestPlan.comments\"></stringProp>" + "\n"
				+ "<boolProp name=\"TestPlan.functional_mode\">false</boolProp>" + "\n"
				+ "<boolProp name=\"TestPlan.serialize_threadgroups\">false</boolProp>" + "\n"
				+ "<elementProp name=\"TestPlan.user_defined_variables\" elementType=\"Arguments\" guiclass=\"ArgumentsPanel\" testclass=\"Arguments\" testname=\"Variables definidas por el Usuario\" enabled=\"true\">" + "\n"
		        + "<collectionProp name=\"Arguments.arguments\"/>" + "\n"
		        + "</elementProp>" + "\n"
		        + "<stringProp name=\"TestPlan.user_define_classpath\"></stringProp>" + "\n"
		        + "</TestPlan>" + "\n";
		
		return bloque;
	}
	
	public static String grupoHilos(String claveCasoUso) {
		String bloque = 
				"<hashTree>" + "\n"
				+ "<ThreadGroup guiclass=\"ThreadGroupGui\" testclass=\"ThreadGroup\" testname=\"TEST" + claveCasoUso + "\" enabled=\"true\">" + "\n"
				+ "<stringProp name=\"ThreadGroup.on_sample_error\">continue</stringProp>" + "\n"
				+ "<elementProp name=\"ThreadGroup.main_controller\" elementType=\"LoopController\" guiclass=\"LoopControlPanel\" testclass=\"LoopController\" testname=\"Controlador Bucle\" enabled=\"true\">" + "\n"
				+ "<boolProp name=\"LoopController.continue_forever\">false</boolProp>" + "\n"
				+ "<stringProp name=\"LoopController.loops\">1</stringProp>" + "\n"
				+ "</elementProp>" + "\n"
				+ "<stringProp name=\"ThreadGroup.num_threads\">1</stringProp>" + "\n"
				+ "<stringProp name=\"ThreadGroup.ramp_time\">1</stringProp>" + "\n"
				+ "<longProp name=\"ThreadGroup.start_time\">1402974414000</longProp>" + "\n"
				+ "<longProp name=\"ThreadGroup.end_time\">1402974414000</longProp>" + "\n"
				+ "<boolProp name=\"ThreadGroup.scheduler\">false</boolProp>" + "\n"
				+ "<stringProp name=\"ThreadGroup.duration\"></stringProp>" + "\n"
				+ "<stringProp name=\"ThreadGroup.delay\"></stringProp>" + "\n"
				+ "</ThreadGroup>" + "\n";
		return bloque;
	}
	
	public static String cookieManager() {
		String bloque = 
				"<hashTree>" + "\n"
				+ "<CookieManager guiclass=\"CookiePanel\" testclass=\"CookieManager\" testname=\"HTTP Cookie Manager\" enabled=\"true\">" + "\n"
				+ "<collectionProp name=\"CookieManager.cookies\"/>" + "\n"
				+ "<boolProp name=\"CookieManager.clearEachIteration\">false</boolProp>" + "\n"
				+ "</CookieManager>" + "\n"
				+ "<hashTree/>" + "\n";
		
		return bloque;
	}
	
	public static String configuracionJDBC(String urlBaseDatos, String driver, String usuario, String password) {
		String bloque =
				"<JDBCDataSource guiclass=\"TestBeanGUI\" testclass=\"JDBCDataSource\" testname=\"JDBC-Default\" enabled=\"true\">" + "\n"
				+ "<stringProp name=\"dataSource\">JDBC Default</stringProp>" + "\n"
				+ "<stringProp name=\"poolMax\">10</stringProp>" + "\n"
				+ "<stringProp name=\"timeout\">10000</stringProp>" + "\n"
				+ "<stringProp name=\"trimInterval\">60000</stringProp>" + "\n"
				+ "<boolProp name=\"autocommit\">true</boolProp>" + "\n"
				+ "<stringProp name=\"transactionIsolation\">DEFAULT</stringProp>" + "\n"
				+ "<boolProp name=\"keepAlive\">true</boolProp>" + "\n"
				+ "<stringProp name=\"connectionAge\">5000</stringProp>" + "\n"
				+ "<stringProp name=\"checkQuery\">Select 1</stringProp>" + "\n"
				+ "<stringProp name=\"dbUrl\">" + urlBaseDatos + "</stringProp>" + "\n"
				+ "<stringProp name=\"driver\">" + driver + "</stringProp>" + "\n"
				+ "<stringProp name=\"username\">" + usuario + "</stringProp>" + "\n"
				+ "<stringProp name=\"password\">"+ password +"</stringProp>" + "\n"
				+ "</JDBCDataSource>" + "\n"
				+ "<hashTree/>" + "\n";
		return bloque;
	}
	
	public static String configuracionHTTP(String ip, String puerto) {
		String bloque = 
				"<ConfigTestElement guiclass=\"HttpDefaultsGui\" testclass=\"ConfigTestElement\" testname=\"HTTP Default\" enabled=\"true\">" + "\n"
				+ "<elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\" guiclass=\"HTTPArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\" enabled=\"true\">" + "\n"
				+ "<collectionProp name=\"Arguments.arguments\"/>" + "\n"
				+ "</elementProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.domain\">"+ ip +"</stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.port\">"+ puerto +"</stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.connect_timeout\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.response_timeout\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.protocol\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.contentEncoding\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.path\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.implementation\">Java</stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.concurrentPool\">4</stringProp>" + "\n"
				+ "</ConfigTestElement>" + "\n"
				+ "<hashTree/>" + "\n";
				
		return bloque;
	}
	
	public static String peticionHTTP(String id, String url, ArrayList<String> parametros, String metodo, String paso, boolean hijos) {
		String bloque =
				"<HTTPSamplerProxy guiclass=\"HttpTestSampleGui\" testclass=\"HTTPSamplerProxy\" testname=\""+ id +"\" enabled=\"true\">" + "\n"
				+ "<elementProp name=\"HTTPsampler.Arguments\" elementType=\"Arguments\" guiclass=\"HTTPArgumentsPanel\" testclass=\"Arguments\" testname=\"User Defined Variables\" enabled=\"true\">" + "\n"
				+ "<collectionProp name=\"Arguments.arguments\">" + "\n";
				
		bloque += parametrosHTTP(id, parametros);
			
		bloque += 				
				"</collectionProp>" + "\n"
				+ "</elementProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.domain\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.port\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.connect_timeout\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.response_timeout\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.protocol\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.contentEncoding\"></stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.path\">"+ url +"</stringProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.method\">"+ metodo +"</stringProp>" + "\n"
				+ "<boolProp name=\"HTTPSampler.follow_redirects\">false</boolProp>" + "\n"
				+ "<boolProp name=\"HTTPSampler.auto_redirects\">false</boolProp>" + "\n"
				+ "<boolProp name=\"HTTPSampler.use_keepalive\">true</boolProp>" + "\n"
				+ "<boolProp name=\"HTTPSampler.DO_MULTIPART_POST\">false</boolProp>" + "\n"
				+ "<boolProp name=\"HTTPSampler.monitor\">false</boolProp>" + "\n"
				+ "<stringProp name=\"HTTPSampler.embedded_url_re\"></stringProp>" + "\n"
				+ "<stringProp name=\"TestPlan.comments\">"+ paso +"</stringProp>" + "\n"
				+ "</HTTPSamplerProxy>" + "\n";
		if (hijos) {
			bloque += "<hashTree>" + "\n";
		} else {
			bloque += "<hashTree/>" + "\n";
		}
		return bloque;
		
	}
	
	public static String parametrosHTTP(String id, ArrayList<String> parametros) {
		String bloque = "";
		
		for (String parametro : parametros) {
			bloque += 
					"<elementProp name=\""+ parametro+"\" elementType=\"HTTPArgument\">" + "\n"
					+ "<boolProp name=\"HTTPArgument.always_encode\">true</boolProp>" + "\n"
					+ "<stringProp name=\"Argument.value\">${"+ prefijoCSV + id + "_" + parametro +"}</stringProp>" + "\n"
					+ "<stringProp name=\"Argument.metadata\">=</stringProp>" + "\n"
					+ "<boolProp name=\"HTTPArgument.use_equals\">true</boolProp>" + "\n"
					+ "<stringProp name=\"Argument.name\">"+ parametro +"</stringProp>" + "\n"
					+ "</elementProp>" + "\n"; 
		}
		return bloque;
	}

	public static String contenedorCSV(String id, String idPeticionHTTP, ArrayList<String> parametros, String paso, boolean terminar, String nombre) {
		String bloque =
				"<CSVDataSet guiclass=\"TestBeanGUI\" testclass=\"CSVDataSet\" testname=\""+ id +"\" enabled=\"true\">" + "\n"
				+ "<stringProp name=\"filename\">entradas/" + nombre + "</stringProp>" + "\n"
				+ "<stringProp name=\"fileEncoding\"></stringProp>" + "\n"
				+ "<stringProp name=\"variableNames\">" + generarNombres(idPeticionHTTP, parametros) +"</stringProp>" + "\n"
				+ "<stringProp name=\"delimiter\">,</stringProp>" + "\n"
				+ "<boolProp name=\"quotedData\">false</boolProp>" + "\n" 
				+ "<boolProp name=\"recycle\">true</boolProp>" + "\n"
				+ "<boolProp name=\"stopThread\">false</boolProp>" + "\n"
				+ "<stringProp name=\"shareMode\">shareMode.all</stringProp>" + "\n"
				+ "<stringProp name=\"TestPlan.comments\">" + paso + "</stringProp>" + "\n"
				+ "</CSVDataSet>" + "\n"
				+ "<hashTree/>" + "\n";
				if (terminar) {
					bloque += "</hashTree>" + "\n";
				}
		
		return bloque;
	}

	public static String generarNombres(String idPeticionHTTP, ArrayList<String> parametros) {
		String bloque = "";
		for (String parametro : parametros) {
			bloque +=  (prefijoCSV + idPeticionHTTP + "_" + parametro) + ",";
		}
		
		if (bloque != "") {
			bloque = bloque.substring(0, bloque.length() - 1);
		}
		
		return bloque;
	}
	
	public static String peticionJDBC(String id, String query, String paso) {
		String bloque = 
				"<JDBCSampler guiclass=\"TestBeanGUI\" testclass=\"JDBCSampler\" testname=\""+ id +"\" enabled=\"true\">" + "\n"
				+ "<stringProp name=\"dataSource\">JDBC Default</stringProp>" + "\n"
				+ "<stringProp name=\"queryType\">Select Statement</stringProp>" + "\n"
				+ "<stringProp name=\"query\">" + query + "</stringProp>" + "\n"
				+ "<stringProp name=\"queryArguments\"></stringProp>" + "\n"
				+ "<stringProp name=\"queryArgumentsTypes\"></stringProp>" + "\n"
				+ "<stringProp name=\"variableNames\">#"+id+"</stringProp>" + "\n"
				+ "<stringProp name=\"resultVariable\"></stringProp>" + "\n"
				+ "<stringProp name=\"queryTimeout\"></stringProp>" + "\n"
				+ "<stringProp name=\"TestPlan.comments\">" + paso + "</stringProp>" + "\n"
				+ "<stringProp name=\"resultSetHandler\">Store as String</stringProp>" + "\n"
				+ "</JDBCSampler>" + "\n"
				+ "<hashTree/>" + "\n";
	
		return bloque;
	}
	
	public static String iniciarControladorIf(String id, String idPeticionJDBC, String paso, String operador) {
		String bloque = 
				"<IfController guiclass=\"IfControllerPanel\" testclass=\"IfController\" testname=\"" + id + "\" enabled=\"true\">" + "\n"
				+ "<stringProp name=\"TestPlan.comments\">"+ paso +"</stringProp>" + "\n"
				+ "<stringProp name=\"IfController.condition\">${#" + idPeticionJDBC + "_#} " +operador+ " 0</stringProp>" + "\n"
				+ "<boolProp name=\"IfController.evaluateAll\">false</boolProp>"+ "\n"
				+ "</IfController>" + "\n"
				+ "<hashTree>" + "\n";
		
		return bloque;
	}
	
	public static String asercion(String id, ArrayList<String> patrones, String paso) {
		String bloque =
				  "<ResponseAssertion guiclass=\"AssertionGui\" testclass=\"ResponseAssertion\" testname=\"" + id + "\" enabled=\"true\"> " + "\n"
				  + "<collectionProp name=\"Asserion.test_strings\">" + "\n"
				  + generarPatrones(patrones) 
				  + "</collectionProp>" + "\n"
				  + "<stringProp name=\"TestPlan.comments\">" + paso + "</stringProp>" + "\n"
				  + "<stringProp name=\"Assertion.test_field\">Assertion.response_data</stringProp>" + "\n"
				  + "<boolProp name=\"Assertion.assume_success\">false</boolProp>" + "\n"
				  + "<intProp name=\"Assertion.test_type\">16</intProp>" + "\n"
				  + "</ResponseAssertion>" + "\n"
				  + "<hashTree/>" + "\n"
				  + "</hashTree>" + "\n";
		
		
		return bloque;
				
	}
	
	public static String generarPatrones(ArrayList<String> patrones) {
		String bloque = "";
		for (String patron : patrones) {
			bloque +=   "<stringProp name=\"873796163\">"+ patron + "</stringProp>\n";
		}
		return bloque;
	}
	
	public static String cerrar() {
		String bloque = 
				"</hashTree>" + "\n"
				+ "</hashTree>" + "\n"
				+ "</hashTree>" + "\n"
				+ "</jmeterTestPlan>" + "\n"; 
	
		return bloque;
	}
	
	public static String terminarControladorIf() {
		return "</hashTree>\n";
	}

	public static String estadisticas() {
		String archivo = "";
		archivo += "<ResultCollector guiclass=\"ViewResultsFullVisualizer\" testclass=\"ResultCollector\" testname=\"View Results Tree\" enabled=\"true\">"
				+ " <boolProp name=\"ResultCollector.error_logging\">false</boolProp>"
				+ " <objProp>"
				+ " <value class=\"SampleSaveConfiguration\">"
				+ " <time>true</time>"
				+ "<latency>true</latency>"
				+ " <timestamp>true</timestamp>"
				+ "<success>true</success>"
				+ " <label>true</label>"
				+ " <code>true</code>"
				+ " <message>true</message>"
				+ "<threadName>true</threadName>"
				+ "<dataType>true</dataType>"
				+ "<encoding>false</encoding>"
				+ "<assertions>true</assertions>"
				+ "<subresults>true</subresults>"
				+ "<responseData>false</responseData>"
				+ "<samplerData>false</samplerData>"
				+ "<xml>false</xml>"
				+ "<fieldNames>false</fieldNames>"
				+ "<responseHeaders>false</responseHeaders>"
				+ "<requestHeaders>false</requestHeaders>"
				+ "<responseDataOnError>false</responseDataOnError>"
				+ "<saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>"
				+ "<assertionsResultsToSave>0</assertionsResultsToSave>"
				+ "<bytes>true</bytes>"
				+ "</value>"
				+ "</objProp>"
				+ "<objProp>"
				+ "<value class=\"SampleSaveConfiguration\">"
				+ "<time>true</time>"
				+ "<latency>true</latency>"
				+ "<timestamp>true</timestamp>"
				+ "<success>true</success>"
				+ "<label>true</label>"
				+ "<code>true</code>"
				+ "<message>true</message>"
				+ "<threadName>true</threadName>"
				+ "<dataType>true</dataType>"
				+ "<encoding>false</encoding>"
				+ "<assertions>true</assertions>"
				+ "<subresults>true</subresults>"
				+ "<responseData>false</responseData>"
				+ "<samplerData>false</samplerData>"
				+ "<xml>false</xml>"
				+ "<fieldNames>false</fieldNames>"
				+ "<responseHeaders>false</responseHeaders>"
				+ "<requestHeaders>false</requestHeaders>"
				+ " <responseDataOnError>false</responseDataOnError>"
				+ "<saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>"
				+ "<assertionsResultsToSave>0</assertionsResultsToSave>"
				+ "<bytes>true</bytes>"
				+ "<threadCounts>true</threadCounts>"
				+ "</value>"
				+ "</objProp>"
				+ "<stringProp name=\"filename\"></stringProp>"
				+ "</ResultCollector>"
				+ " <hashTree/>"
				+ "<ResultCollector guiclass=\"TableVisualizer\" testclass=\"ResultCollector\" testname=\"View Results in Table\" enabled=\"true\">"
				+ "<boolProp name=\"ResultCollector.error_logging\">false</boolProp>"
				+ " <objProp>"
				+ "<value class=\"SampleSaveConfiguration\">"
				+ "<latency>true</latency>"
				+ "<timestamp>true</timestamp>"
				+ "<success>true</success>"
				+ "<label>true</label>"
				+ "<code>true</code>"
				+ "<message>true</message>"
				+ "<threadName>true</threadName>"
				+ "<dataType>true</dataType>"
				+ "<encoding>false</encoding>"
				+ "<assertions>true</assertions>"
				+ "<subresults>true</subresults>"
				+ "<responseData>false</responseData>"
				+ "<samplerData>false</samplerData>"
				+ " <xml>false</xml>"
				+ "<fieldNames>false</fieldNames>"
				+ "<responseHeaders>false</responseHeaders>"
				+ "<requestHeaders>false</requestHeaders>"
				+ "<responseDataOnError>false</responseDataOnError>"
				+ "<saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>"
				+ "<assertionsResultsToSave>0</assertionsResultsToSave>"
				+ "<bytes>true</bytes>"
				+ "</value>"
				+ "</objProp>"
				+ "<objProp>"
				+ "<value class=\"SampleSaveConfiguration\">"
				+ "<time>true</time>"
				+ "<latency>true</latency>"
				+ "<timestamp>true</timestamp>"
				+ "<success>true</success>"
				+ "<label>true</label>"
				+ "<code>true</code>"
				+ "<message>true</message>"
				+ "<threadName>true</threadName>"
				+ " <dataType>true</dataType>"
				+ "<encoding>false</encoding>"
				+ "<assertions>true</assertions>"
				+ "<subresults>true</subresults>"
				+ " <responseData>false</responseData>"
				+ "<samplerData>false</samplerData>"
				+ "<xml>false</xml>"
				+ "<fieldNames>false</fieldNames>"
				+ "<responseHeaders>false</responseHeaders>"
				+ "<requestHeaders>false</requestHeaders>"
				+ "<responseDataOnError>false</responseDataOnError>"
				+ "<saveAssertionResultsFailureMessage>false</saveAssertionResultsFailureMessage>"
				+ "<assertionsResultsToSave>0</assertionsResultsToSave>"
				+ "<bytes>true</bytes>"
				+ "<threadCounts>true</threadCounts>"
				+ "</value>"
				+ "</objProp>"
				+ "<stringProp name=\"filename\"></stringProp>"
				+ "</ResultCollector>"
				+ " <hashTree/>";
		
		return archivo;
	}

	/*
	 * String peticionJDBC(@paso)
	 * 
	 * Genera el bloque de código para una petición JDBC
	 * con base en un paso.
	 * 
	 * 
	 * @paso: Objeto Paso  con la estructura "Verifica que exista al menos una ENT·X con base en la RN·Verificación de catálogos". 
	 * Este paso debe tener asociado el query que permite validar esta condición.
	 * 
	 * El código mostrado a continuación sirve para inicializar los objetos cuya estrategia es LAZY.
	 * 
	 * 		refParam = new ReferenciaParametroDAO().consultarReferenciaParametro(refParam.getId());

	 */
	public static String peticionJDBC(Paso paso) {

		String bloque = null;
		String id = calcularIdentificador(prefijoPeticionJDBC, paso);
		String queryString = null;
		ReferenciaParametro refParam = null;

		refParam = AnalizadorPasosBs.obtenerPrimerReferencia(paso, ReferenciaEnum.TipoReferencia.REGLANEGOCIO);
		refParam = new ReferenciaParametroDAO().consultarReferenciaParametro(refParam.getId());
		
		for(Query sentencia : refParam.getQueries()) {
			queryString = sentencia.getQuery();
			break;
		}
		
		String redaccionPaso = consultarRedaccion(paso);
		bloque = peticionJDBC(id, queryString, redaccionPaso);
		return bloque;
	}

	public static String iniciarControladorIf(Paso paso, String operador) {
		String bloque = null;
		String id = calcularIdentificador(prefijoControladorIf, paso);
		String idPeticionJDBC = calcularIdentificador(prefijoPeticionJDBC, paso);
		String redaccionPaso = consultarRedaccion(paso);
		
		bloque = iniciarControladorIf(id, idPeticionJDBC, redaccionPaso, operador);
		
		return bloque;
	}

	public static String peticionHTTP(Paso paso, boolean hijos) {

		String bloque;
		String id = null;
		ReferenciaParametro refParam;
		Accion accion;
		String url;
		ArrayList<String> nombresParametros;
		ArrayList<Entrada> entradas;

		String metodo;
		String redaccionPaso;
		
		id = calcularIdentificador(prefijoPeticionHTTP, paso);

		refParam = AnalizadorPasosBs.obtenerPrimerReferencia(paso, ReferenciaEnum.TipoReferencia.ACCION);
		accion = (Accion) refParam.getAccionDestino();
		url = accion.getUrlDestino();
		metodo = accion.getMetodo();
		redaccionPaso = consultarRedaccion(paso);
		
		entradas = new ArrayList<Entrada>();
		entradas.addAll(paso.getTrayectoria().getCasoUso().getEntradas());		
		nombresParametros = obtenerNombresParametros(entradas);
		
		bloque = peticionHTTP(id, url, nombresParametros, metodo, redaccionPaso, hijos);
		return bloque;
	}
	
	public static String peticionHTTP(Paso paso, Paso pasoRN, Entrada entrada, boolean hijos) {
		String bloque;
		String id = null;
		ReferenciaParametro refParam;
		Accion accion;
		String url;
		ArrayList<String> nombresParametros;
		ArrayList<Entrada> entradas;

		String metodo;
		String redaccionPaso;
		
		id = calcularIdentificador(prefijoPeticionHTTP, paso, pasoRN, entrada);

		refParam = AnalizadorPasosBs.obtenerPrimerReferencia(paso, ReferenciaEnum.TipoReferencia.ACCION);
		accion = (Accion) refParam.getAccionDestino();
		url = accion.getUrlDestino();
		metodo = accion.getMetodo();
		redaccionPaso = consultarRedaccion(paso);
		
		entradas = new ArrayList<Entrada>();
		entradas.addAll(paso.getTrayectoria().getCasoUso().getEntradas());		
		nombresParametros = obtenerNombresParametros(entradas);
		
		bloque = peticionHTTP(id, url, nombresParametros, metodo, redaccionPaso, hijos);
		return bloque;
	}
		
	public static String asercion(Paso paso) {
		
		String bloque;
		String id;
		ReferenciaParametro refParamMensaje;
		ReferenciaParametro refParamPantalla;
		ArrayList<String> patrones = new ArrayList<String>();
		String redaccionPaso;

		id = calcularIdentificador(prefijoAsercion, paso);

		refParamMensaje = AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.MENSAJE);
		refParamPantalla = AnalizadorPasosBs.obtenerPrimerReferencia(paso, TipoReferencia.PANTALLA);

		if (refParamMensaje != null) {
			patrones.add(calcularPatron(refParamMensaje));

		}
		if (refParamPantalla != null) {
			patrones.add(calcularPatron(refParamPantalla));

		}	
		redaccionPaso = consultarRedaccion(paso);
		
		bloque = asercion(id, patrones, redaccionPaso);
		return bloque;
	}
	
	public static String calcularPatron(ReferenciaParametro refParam) {
		Mensaje mensaje;
		Pantalla pantalla;
		String patron = null;
		refParam = new ReferenciaParametroDAO().consultarReferenciaParametro(refParam.getId());

		switch(ReferenciaEnum.getTipoReferenciaParametro(refParam)) {
		case MENSAJE:
			mensaje = (Mensaje) refParam.getElementoDestino();
			if (!mensaje.getParametros().isEmpty()) {
				patron = mensaje.getRedaccion();
			} else {
				patron = mensaje.getRedaccion();
			}
			for (MensajeParametro mensajeParametro : mensaje.getParametros()) {
				for (ValorMensajeParametro valor : refParam.getValoresMensajeParametro()) {
					if (mensajeParametro.getId().equals(valor.getMensajeParametro().getId())) {
						patron = TokenBs.remplazoToken(patron, TokenBs.tokenPARAM
								+ mensajeParametro.getParametro().getNombre(), valor.getValor());
					}
				}
			}
			
			break;
		case PANTALLA:
			pantalla = (Pantalla) refParam.getElementoDestino();
			patron = pantalla.getPatron();
			break;
		default:
			break;
			
		}
		patron = org.apache.commons.lang.StringEscapeUtils.escapeHtml(patron);
		return patron;
	}
	

	public static String contenedorCSV(Paso paso, boolean terminar) throws Exception {
		String bloque = null;
		String idCSV;
		String idPeticionHTTP;
		String redaccionPaso;
		ArrayList<Entrada> entradas = new ArrayList<Entrada>();
		ArrayList<String> nombresParametros = null;
		ArrayList<String> valoresParametros = null;
		String nombreCSV;
		String ruta;
		
		idCSV = calcularIdentificador(prefijoContenedorCSV, paso);
		idPeticionHTTP = calcularIdentificador(prefijoPeticionHTTP, paso);
		entradas.addAll(paso.getTrayectoria().getCasoUso().getEntradas());
		
		nombresParametros = obtenerNombresParametros(entradas);
		valoresParametros = obtenerValoresParametros(entradas);
		redaccionPaso = consultarRedaccion(paso);
		nombreCSV = calcularNombreCSV(idCSV);
		ruta = generarRutaCSV(paso);
		
		bloque = contenedorCSV(idCSV, idPeticionHTTP, nombresParametros, redaccionPaso, terminar, nombreCSV);
		
		generarCSV(ruta, nombreCSV, valoresParametros);
		return bloque;
	}
	
	
	public static String contenedorCSV(Paso paso, Paso rn, Entrada entradaInvalida, boolean terminar) throws Exception {

		String bloque = null;
		String idCSV;
		String idPeticionHTTP;
		String redaccionPaso;
		ArrayList<Entrada> entradas = new ArrayList<Entrada>();
		ArrayList<String> nombresParametros = null;
		ArrayList<String> valoresParametros = null;
		String nombreCSV;
		String ruta;
		ReferenciaParametro referenciaParametro;
		
		idCSV = calcularIdentificador(prefijoContenedorCSV, paso, rn, entradaInvalida);
		idPeticionHTTP = calcularIdentificador(prefijoPeticionHTTP, paso, rn, entradaInvalida);
		entradas.addAll(paso.getTrayectoria().getCasoUso().getEntradas());
		referenciaParametro = AnalizadorPasosBs.obtenerPrimerReferencia(rn, ReferenciaEnum.TipoReferencia.REGLANEGOCIO);
		
		nombresParametros = obtenerNombresParametros(entradas);
		valoresParametros = obtenerValoresParametros(entradas, entradaInvalida, (ReglaNegocio) referenciaParametro.getElementoDestino());
		redaccionPaso = consultarRedaccion(paso);
		nombreCSV = calcularNombreCSV(idCSV);
		ruta = generarRutaCSV(paso);
		
		bloque = contenedorCSV(idCSV, idPeticionHTTP, nombresParametros, redaccionPaso, terminar, nombreCSV);
		generarCSV(ruta, nombreCSV, valoresParametros);
		return bloque;
	}
	
	
	private static ArrayList<String> obtenerValoresParametros(
			ArrayList<Entrada> entradas) {
		ArrayList<String> valores = new ArrayList<String>();
		for (Entrada entrada : entradas) {
			entrada = new EntradaDAO().findById(entrada.getId());
			for (ValorEntrada valorEntrada : entrada.getValores()) {
				if (valorEntrada.getValido()) {
					String valor = valorEntrada.getValor();
					String valorEscCSV = org.apache.commons.lang.StringEscapeUtils.escapeCsv(valor);
					valores.add(valorEscCSV);
				}
			}
		}
		return valores;
	}
	
	
	private static ArrayList<String> obtenerValoresParametros(
			ArrayList<Entrada> entradas, Entrada entradaInvalida, ReglaNegocio reglaNegocio) {
		ArrayList<String> valores = new ArrayList<String>();
		boolean buscarInvalida = false;
		for (Entrada entrada : entradas) {
			entrada = new EntradaDAO().findById(entrada.getId());
			if (entradaInvalida != null && entrada.getId() == entradaInvalida.getId()) {
				buscarInvalida = true;
			} else {
				buscarInvalida = false;
			}
			for (ValorEntrada valorEntrada : entrada.getValores()) {
				if (buscarInvalida) {
					if (!valorEntrada.getValido() && valorEntrada.getReglaNegocio().getId() == reglaNegocio.getId()) {
						String valor = valorEntrada.getValor();
						String valorEscCSV = org.apache.commons.lang.StringEscapeUtils.escapeCsv(valor);
						valores.add(valorEscCSV);
					}
				} else {
					if (valorEntrada.getValido()) {
						String valor = valorEntrada.getValor();
						String valorEscCSV = org.apache.commons.lang.StringEscapeUtils.escapeCsv(valor);
						valores.add(valorEscCSV);
					}
				}
			}
		}
		return valores;
	}
	
	
	public static ArrayList<String> obtenerNombresParametros(
			ArrayList<Entrada> entradas) {
		ArrayList<String> nombres = new ArrayList<String>();
		entradas = ordenarEntradas(entradas);
		for (Entrada entrada : entradas) {
			nombres.add(entrada.getNombreHTML());
		}
		return nombres;
	}

	
	private static String calcularIdentificador(String prefijo,
			Paso paso, Paso rn, Entrada entrada) {
		if (entrada == null) {
			return prefijo + paso.getTrayectoria().getCasoUso().getClave() + paso.getTrayectoria().getCasoUso().getNumero() + "-" + paso.getTrayectoria().getClave() + paso.getNumero() + rn.getTrayectoria().getClave() + rn.getNumero();
		}
		if (entrada.getAtributo()!= null) {
			return prefijo + paso.getTrayectoria().getCasoUso().getClave() + paso.getTrayectoria().getCasoUso().getNumero() + "-" + paso.getTrayectoria().getClave() + paso.getNumero() + rn.getTrayectoria().getClave() + rn.getNumero() + entrada.getNombreHTML();
		} else if (entrada.getTerminoGlosario() != null) {
			return prefijo + paso.getTrayectoria().getCasoUso().getClave() + paso.getTrayectoria().getCasoUso().getNumero() +"-" + paso.getTrayectoria().getClave() + paso.getNumero() + rn.getTrayectoria().getClave() + rn.getNumero() + entrada.getNombreHTML();
		}
		return null;
	}
	
	
	public static String calcularIdentificador(String prefijo, Paso paso) {
		return prefijo + paso.getTrayectoria().getCasoUso().getClave() + paso.getTrayectoria().getCasoUso().getNumero() + "-" + paso.getTrayectoria().getClave() + paso.getNumero();
	}
	
	
	public static String generarRutaCSV(Paso paso) {
		return casoUsoTesting + "/entradas/";
	}
	
	public static void generarCSV(String ruta, String nombreCSV,
			ArrayList<String> valoresParametros) throws IOException {
		String linea = "";
		
		for(String valor : valoresParametros) {
			linea = linea + valor + ",";
		}
		
		if (!linea.equals("")) {
			linea = linea.substring(0, linea.length() - 1);	
		} else {
			linea = ",";
		}
		crearArchivo(nombreCSV, ruta, linea);
	}

	
	public static String calcularNombreCSV(String id) {
		return  id + ".csv";
	}
	
	
	public static String consultarRedaccion(Paso paso) {
		//return TokenBs.decodificarCadenaSinToken(paso.getRedaccion());
		return paso.getRedaccion();
	}
	
	
	public static ArrayList<Entrada> ordenarEntradas(ArrayList<Entrada> entradas) {
		int longitud = entradas.size();
		Entrada entrada = null;
		for (int i = 0; i < longitud; i++) {
			for (int j = 0; j < longitud; j++) {
				if (entradas.get(i).getId() < entradas.get(j).getId()) {
					entrada = entradas.get(j);
					entradas.set(j, entradas.get(i));
					entradas.set(i, entrada);
				}
			}
		}
		return entradas;
		
	}

	
	public static String probarReglaNegocio(Paso pasoActual, Paso pasoRN) throws Exception {
		String archivo = "";
		ArrayList<Entrada> entradas = new ArrayList<Entrada>();
		ReferenciaParametro refParam;
		ReglaNegocio reglaNegocio;
		refParam = AnalizadorPasosBs.obtenerPrimerReferencia(pasoRN, TipoReferencia.REGLANEGOCIO);
		reglaNegocio = (ReglaNegocio) refParam.getElementoDestino();
		TipoReglaNegocioENUM tipoRN = TipoReglaNegocioEnum.getTipoReglaNegocio(reglaNegocio.getTipoReglaNegocio());
		
		if (tipoRN == TipoReglaNegocioENUM.UNICIDAD) {
			refParam = new ReferenciaParametroDAO().consultarReferenciaParametro(refParam.getId());
			archivo += GeneradorPruebasBs.peticionJDBC(pasoRN);
			archivo += GeneradorPruebasBs.iniciarControladorIf(
					pasoRN, ">");
			archivo += GeneradorPruebasBs.peticionHTTP(pasoActual, pasoRN, null, true);
			archivo += GeneradorPruebasBs.contenedorCSV(pasoActual, pasoRN, null, false);
			archivo += GeneradorPruebasBs.asercion(AnalizadorPasosBs.calcularPasoAlternativo(pasoRN));
			archivo += GeneradorPruebasBs.terminarControladorIf();			
			pasoIncierto = pasoRN;
			caminoIdealIncierto = true;

		} else {
			entradas.addAll(pasoActual.getTrayectoria().getCasoUso().getEntradas());	
			if (tipoRN == TipoReglaNegocioENUM.DATOCORRECTO) {
				for (Entrada entrada : entradas) {
					if (entrada.getAtributo() != null && !entrada.getAtributo().getTipoDato().getNombre().equals("Cadena") && !entrada.getAtributo().getTipoDato().getNombre().equals("Archivo") && !entrada.getAtributo().getTipoDato().getNombre().equals("Otro")) { 
						archivo += GeneradorPruebasBs.peticionHTTP(pasoActual, pasoRN, entrada, true);
						archivo += GeneradorPruebasBs.contenedorCSV(pasoActual,	pasoRN, entrada, false);
						archivo += GeneradorPruebasBs.asercion(AnalizadorPasosBs
						.calcularPasoAlternativo(pasoRN));
					}
				}
			} 
			if (tipoRN == TipoReglaNegocioENUM.LONGITUD) {
				for (Entrada entrada : entradas) {
					if (entrada.getAtributo() != null && (entrada.getAtributo().getTipoDato().getNombre().equals("Cadena") || entrada.getAtributo().getTipoDato().getNombre().equals("Entero") || entrada.getAtributo().getTipoDato().getNombre().equals("Flotante"))) { 
						archivo += GeneradorPruebasBs.peticionHTTP(pasoActual, pasoRN, entrada, true);
						archivo += GeneradorPruebasBs.contenedorCSV(pasoActual,	pasoRN, entrada, false);
						archivo += GeneradorPruebasBs.asercion(AnalizadorPasosBs
						.calcularPasoAlternativo(pasoRN));
					}
				}
			}			
			if (tipoRN == TipoReglaNegocioENUM.OBLIGATORIOS) {
				for (Entrada entrada : entradas) {
					if (entrada.getAtributo() != null && entrada.getAtributo().isObligatorio()) { 
						archivo += GeneradorPruebasBs.peticionHTTP(pasoActual, pasoRN, entrada, true);
						archivo += GeneradorPruebasBs.contenedorCSV(pasoActual,	pasoRN, entrada, false);
						archivo += GeneradorPruebasBs.asercion(AnalizadorPasosBs
						.calcularPasoAlternativo(pasoRN));
					}
				}
			}
		}
						
		return archivo;
	}	
	
	public static void crearArchivo(String nombre, String ruta,
			String contenido) throws IOException {
		
		File file = new File(ruta + nombre);
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		
		/*if(nombre.contains(".jmx")) {
			contenido = formatoXML(contenido);
		}*/
		
		writer.append(contenido);
		writer.close();
	}
	
	/*public static String formatoXML(String cadena) throws IOException {
		Document document = FileUtil.parseXmlFile(cadena);

        OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(document);
        
        return out.toString();
	}*/
	
	public static void generarCasosPrueba(CasoUso casoUso, String rutaPruebas) throws Exception {
		String archivo = "";
		ConfiguracionHttp confHTTP;
		ConfiguracionBaseDatos confJDBC;
		ArrayList<Paso> pasos = new ArrayList<Paso>();
		casoUsoTesting = rutaPruebas + casoUso.getId() + "";
		archivo += GeneradorPruebasBs.encabezado();
		archivo += GeneradorPruebasBs.planPruebas();
		archivo += GeneradorPruebasBs.grupoHilos(casoUso.getClave() + casoUso.getNumero());
		archivo += GeneradorPruebasBs.cookieManager();
		confHTTP = new ConfiguracionDAO().consultarConfiguracionHttpByCasoUso(casoUso);
		archivo += GeneradorPruebasBs.configuracionHTTP(confHTTP.getIp(), confHTTP.getPuerto().toString());
		confJDBC = new ConfiguracionDAO().consultarConfiguracionBaseDatosByCasoUso(casoUso);
		archivo += GeneradorPruebasBs.configuracionJDBC(confJDBC.getUrlBaseDatos(), confJDBC.getDriver(), confJDBC.getUsuario(), confJDBC.getContrasenia());
		archivo += GeneradorPruebasBs.estadisticas();
		archivo += prepararPrueba(casoUso);

		for (Trayectoria trayectoria : casoUso.getTrayectorias()) {
			if (!trayectoria.isAlternativa()) {
				pasos.addAll(trayectoria.getPasos());
				//pasos = AnalizadorPasosBs.ordenarPasos(trayectoria);
				for (Paso paso : trayectoria.getPasos()) {
					if (paso.getNumero() == 1) {
						archivo += generarPrueba(paso, pasos);
					}
				}
			}
		}
		
		archivo += GeneradorPruebasBs.cerrar();
		
		crearArchivo(casoUso.getClave() + casoUso.getNumero() + ".jmx", casoUsoTesting + "/", archivo);
	}


	public static String prepararPrueba(CasoUso casoUso) throws Exception {
		String archivo = "";
		TipoPaso tipo;
		ArrayList<Paso> pasos = new ArrayList<Paso>();
		List<CasoUso> casosUso = new ArrayList<CasoUso>();
		casosUso = CuBs.obtenerCaminoPrevioMasCorto(casoUso);
		
		for (CasoUso casoUsoi : casosUso) {
			for (Trayectoria trayectoria : casoUsoi.getTrayectorias()) {
				if (!trayectoria.isAlternativa()) {
					pasos.addAll(trayectoria.getPasos());
					for (Paso paso : AnalizadorPasosBs.ordenarPasos(trayectoria)) {
						tipo = AnalizadorPasosBs.calcularTipo(paso);
						if (tipo != null) {
							switch (tipo) {
							case actorOprimeBoton:
								if (paso.getNumero() == 1) {
									archivo += GeneradorPruebasBs.peticionHTTP(paso, false);
								} else {
									archivo += GeneradorPruebasBs.peticionHTTP(paso, true);
									archivo += GeneradorPruebasBs.contenedorCSV(paso, true);
								}
								break;
							case actorSoliciaSeleccionarRegistro:
								archivo += GeneradorPruebasBs.peticionHTTP(paso, true);
								archivo += GeneradorPruebasBs.contenedorCSV(paso, true);
								break;
							default:
								break;
						
							}
						}
					}
				}
			}
		}
		return archivo;
	}

	
	public static String generarPrueba(Paso pasoActual, ArrayList<Paso> pasos) throws Exception {
		String archivo = "";
		TipoPaso tipo;
		Paso siguiente;
		if (pasoActual == null) {
			return archivo;
		}
		siguiente = AnalizadorPasosBs.calcularSiguiente(pasoActual, pasos);
		tipo = AnalizadorPasosBs.calcularTipo(pasoActual);
		if (tipo == null) {
			pasos.remove(pasoActual);
			archivo += generarPrueba(siguiente, pasos);
			return archivo;
		}

		switch (tipo) {
		case actorOprimeBoton:
			if (pasoActual.getNumero() == 1) {
				if (siguiente != null
						&& AnalizadorPasosBs.calcularTipo(siguiente) == AnalizadorPasosBs.TipoPaso.sistemaValidaPrecondicion) {
					archivo += GeneradorPruebasBs.peticionJDBC(siguiente);
					archivo += GeneradorPruebasBs.iniciarControladorIf(
							siguiente, ">");
					pasos.remove(siguiente);
					archivo += generarPrueba(pasoActual, pasos);
					archivo += GeneradorPruebasBs.terminarControladorIf();

					archivo += GeneradorPruebasBs.iniciarControladorIf(
							siguiente, "==");
					archivo += GeneradorPruebasBs.peticionHTTP(pasoActual, true);
					archivo += GeneradorPruebasBs.asercion(AnalizadorPasosBs.calcularPasoAlternativo(siguiente));
					archivo += GeneradorPruebasBs.terminarControladorIf();
				} else {
					archivo += GeneradorPruebasBs.peticionHTTP(pasoActual, true);
					pasos.remove(pasoActual);
					archivo += generarPrueba(siguiente, pasos);
				}
			} else if (pasoActual.getNumero() > 1) {
				if (siguiente != null
						&& AnalizadorPasosBs.calcularTipo(siguiente) == AnalizadorPasosBs.TipoPaso.sistemaValidaReglaNegocio) {
					
					archivo += probarReglaNegocio(pasoActual, siguiente);
				
					pasos.remove(siguiente);
					archivo += generarPrueba(pasoActual, pasos);

				} else {
					if (!caminoIdealIncierto) {
						archivo += GeneradorPruebasBs.peticionHTTP(pasoActual, true);
						archivo += GeneradorPruebasBs.contenedorCSV(pasoActual,
							false);
					} else {
						if(pasoIncierto != null) {
							archivo += GeneradorPruebasBs.iniciarControladorIf(
									pasoIncierto, "==");
							archivo += GeneradorPruebasBs.peticionHTTP(pasoActual, pasoIncierto, null, true);
							archivo += GeneradorPruebasBs.contenedorCSV(pasoActual, pasoIncierto, null, false);
						}
						pasos.remove(pasoActual);
						archivo += generarPrueba(siguiente, pasos);
						archivo += GeneradorPruebasBs.terminarControladorIf();	
					}
					
				}
			}
			break;
		case sistemaMuestraMensaje:
			archivo += GeneradorPruebasBs.asercion(pasoActual);
			pasos.remove(pasoActual);
			archivo += generarPrueba(siguiente, pasos);

			break;
		case sistemaMuestraPantalla:
			archivo += GeneradorPruebasBs.asercion(pasoActual);
			pasos.remove(pasoActual);
			archivo += generarPrueba(siguiente, pasos);

			break;
		default:
			pasos.remove(pasoActual);
			archivo += generarPrueba(siguiente, pasos);
		}
		return archivo;
	}
	
	
	


	
}

