package co.com.latam.alura.tienda.dao;

import javax.persistence.EntityManager;

import co.com.latam.alura.tienda.prueba.LoadRecords;

import co.com.latam.alura.tienda.modelo.Categoria;

public class CategoriaDao {
	
	private EntityManager em;

	public CategoriaDao(EntityManager em) {
		this.em = em;
	}
	
	public void guardar(Categoria categoria) {
		this.em.persist(categoria);
	}
	
	public void actualizar(LoadRecords categoria) {
		this.em.merge(categoria);
	}
	
	public void remover(LoadRecords categoria) {
		categoria=this.em.merge(categoria);
		this.em.remove(categoria);
	}
	
	public LoadRecords consultaPorNombre(String nombre){
		String jpql =" SELECT C FROM Categoria AS C WHERE C.nombre=:nombre ";
		return em.createQuery(jpql,LoadRecords.class).setParameter("nombre", nombre).getSingleResult();
	}

}