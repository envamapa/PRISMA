package mx.prisma.generadorPruebas.bs;

import java.util.HashSet;
import java.util.Set;

import mx.prisma.editor.model.Entrada;
import mx.prisma.editor.model.ReglaNegocio;
import mx.prisma.generadorPruebas.dao.ValorEntradaDAO;
import mx.prisma.generadorPruebas.model.ValorEntrada;

public class ValorEntradaBs {
	public static void registrarValorEntrada(ValorEntrada valor) throws Exception{
		new ValorEntradaDAO().registrarValorEntrada(valor);
	}

	public static Set<ValorEntrada> consultarValores(Entrada entrada) {
		Set<ValorEntrada> valores = new HashSet<ValorEntrada>(new ValorEntradaDAO().consultarValores(entrada));
		return valores;
	}
	
	public static ValorEntrada consultarValorValido(Entrada entrada) {
		ValorEntrada valor = new ValorEntradaDAO().consultarValorValido(entrada);
		return valor;
	}

	public static ValorEntrada consultarValor(Integer id) {
		ValorEntrada valor = new ValorEntradaDAO().consultarValor(id);
		return valor;
	}

	public static ValorEntrada consultarValorInvalido(
			ReglaNegocio reglaNegocio, Entrada entrada) {
		return new ValorEntradaDAO().consultarValorInvalido(reglaNegocio, entrada);
	}

	
}
