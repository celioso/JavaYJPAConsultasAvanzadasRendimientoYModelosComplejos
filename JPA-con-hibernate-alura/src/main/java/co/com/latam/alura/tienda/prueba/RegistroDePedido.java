package co.com.latam.alura.tienda.prueba;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;

import co.com.latam.alura.tienda.dao.CategoriaDao;
import co.com.latam.alura.tienda.dao.ClienteDao;
import co.com.latam.alura.tienda.dao.PedidoDao;
import co.com.latam.alura.tienda.dao.ProductoDao;
import co.com.latam.alura.tienda.modelo.Categoria;
import co.com.latam.alura.tienda.modelo.Cliente;
import co.com.latam.alura.tienda.modelo.ItemsPedido;
import co.com.latam.alura.tienda.modelo.Pedido;
import co.com.latam.alura.tienda.modelo.Producto;
import co.com.latam.alura.tienda.utils.JPAUtils;
import co.com.latam.alura.tienda.vo.RelatorioDeVenta;


public class RegistroDePedido {

	public static void main(String[] args) throws FileNotFoundException {
		registrarProducto();
		
		EntityManager em = JPAUtils.getEntityManager();
		
		ProductoDao productoDao = new ProductoDao(em);
		Producto producto = productoDao.consultaPorId(1l);

		ClienteDao clienteDao = new ClienteDao(em);
		PedidoDao pedidoDao = new PedidoDao(em);

		Cliente cliente = new Cliente("Juan","k6757kjb");
		Pedido pedido = new Pedido(cliente);
		pedido.agregarItems(new ItemsPedido(5,producto,pedido));
		
		em.getTransaction().begin();

		clienteDao.guardar(cliente);
		pedidoDao.guardar(pedido);
		
		em.getTransaction().commit();
		
		BigDecimal valorTotal=pedidoDao.valorTotalVendido();
		System.out.println("Valor Total: "+ valorTotal);
		
		List<RelatorioDeVenta> relatorio = pedidoDao.relatorioDeVentasVO();
		
		relatorio.forEach(System.out::println);
		
	}

	private static void registrarProducto() {
		Categoria celulares = new Categoria("CELULARES");

		Producto celular = new Producto("Xiaomi Redmi", "Muy bueno", new BigDecimal("800"), celulares);

	    EntityManager em = JPAUtils.getEntityManager();
	    ProductoDao productoDao = new ProductoDao(em);
        CategoriaDao categoriaDao = new CategoriaDao(em);
        
	    em.getTransaction().begin();
	    
	    categoriaDao.guardar(celulares);
	    productoDao.guardar(celular);	
	    
	    em.getTransaction().commit();
	    em.close();
	}

}


