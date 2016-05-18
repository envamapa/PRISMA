package mx.prisma.generadorPruebas.dao;

import java.util.List;

import mx.prisma.dao.GenericDAO;
import mx.prisma.editor.model.Entrada;
import mx.prisma.editor.model.ReglaNegocio;
import mx.prisma.generadorPruebas.model.ValorEntrada;

import org.hibernate.HibernateException;
import org.hibernate.Query;

public class ValorEntradaDAO extends GenericDAO {

	public void registrarValorEntrada(ValorEntrada valor) {
		try {
			session.beginTransaction();
			session.saveOrUpdate(valor);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
			throw he;
		}
		
	}

	public ValorEntrada consultarValorValido(Entrada entrada) {
		List<ValorEntrada> results = null;
		
		try {
			session.beginTransaction();
			Query query = session
					.createQuery("from ValorEntrada where entrada = :entrada AND valido = true");
			query.setParameter("entrada", entrada);

			results = query.list();
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
		}
		
		if(results == null) {
			return null;
		} else if (results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}

	public List<ValorEntrada> consultarValoresInvalidos(Entrada entrada) {
		List<ValorEntrada> results = null;
		
		try {
			session.beginTransaction();
			Query query = session
					.createQuery("from ValorEntrada where entrada = :entrada AND valido = false");
			query.setParameter("entrada", entrada);

			results = query.list();
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
		}
		
		if(results == null) {
			return null;
		} else if (results.isEmpty()) {
			return null;
		} else {
			return results;
		}
	}

	public List<ValorEntrada> consultarValores(Entrada entrada) {
		List<ValorEntrada> results = null;
		
		try {
			session.beginTransaction();
			Query query = session
					.createQuery("from ValorEntrada where entrada = :entrada");
			query.setParameter("entrada", entrada);

			results = query.list();
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
		}
		
		if(results == null) {
			return null;
		} else if (results.isEmpty()) {
			return null;
		} else {
			return results;
		}
	}

	public ValorEntrada consultarValor(Integer id) {
		ValorEntrada valor = null;
		try {
			session.beginTransaction();
			valor = (ValorEntrada) session.get(ValorEntrada.class, id);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
			throw he;
		}
		return valor;
	}

	public ValorEntrada consultarValorInvalido(ReglaNegocio reglaNegocio,
			Entrada entrada) {
		List<ValorEntrada> results = null;
		try {
			session.beginTransaction();
			Query query = session
					.createQuery("from ValorEntrada where reglaNegocio = :reglaNegocio AND entrada = :entrada AND valido = false");
			query.setParameter("reglaNegocio", reglaNegocio);
			query.setParameter("entrada", entrada);
			results = query.list();
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
		}
		
		if(results == null) {
			return null;
		} else if (results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}
}
