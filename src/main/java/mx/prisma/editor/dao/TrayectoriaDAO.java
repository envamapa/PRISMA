package mx.prisma.editor.dao;


import mx.prisma.dao.GenericDAO;
import mx.prisma.editor.model.Actualizacion;
import mx.prisma.editor.model.Trayectoria;

import org.hibernate.HibernateException;

public class TrayectoriaDAO extends GenericDAO {

	public TrayectoriaDAO() {
		super();
	}


	public void registrarTrayectoria(Trayectoria trayectoria) {
		try {
			session.beginTransaction();
			session.save(trayectoria);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
			throw he;
		}
	}
	
//	public void modificarTrayectoria(Trayectoria trayectoria, Actualizacion actualizacion) {
//		try {
//			session.beginTransaction();			
// 			session.saveOrUpdate(trayectoria);
//			session.save(actualizacion);
//			session.getTransaction().commit();
//		} catch (HibernateException he) {
//			he.printStackTrace();
//			session.getTransaction().rollback();
//			throw he;
//		}
//	}
	
	public void modificarTrayectoria(Trayectoria trayectoria) {
		try {
			session.beginTransaction();			
 			session.saveOrUpdate(trayectoria);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
			throw he;
		}
	}
	
	public Trayectoria consultarTrayectoria(int id) throws HibernateException {		
		Trayectoria trayectoria = null;
		try {
			session.beginTransaction();
			trayectoria = (Trayectoria)session.get(Trayectoria.class, id);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
		}
		
		return trayectoria;

	}
	
	public void eliminarTrayectoria(Trayectoria trayectoria) {

		try {
			session.beginTransaction();
			session.delete(trayectoria);
			session.getTransaction().commit();
		} catch (HibernateException he) {
			he.printStackTrace();
			session.getTransaction().rollback();
			throw he;
		}
	}
}
