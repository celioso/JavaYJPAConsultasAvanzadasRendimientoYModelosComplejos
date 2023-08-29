# Java y JPA consultas avanzadas, rendimiento y modelos complejos

## Proyecto anterior

¿Comenzando en esta etapa? Aquí puedes descargar los archivos del proyecto.

[Descargue los archivos en Github](https://github.com/alura-cursos/JPA-con-hibernate-Alura-/tree/stage-final "Descargue los archivos en Github") o haga clic [aquí](https://github.com/alura-cursos/JPA-con-hibernate-Alura-/archive/refs/heads/stage-final.zip "aquí") para descargarlos directamente.

## Preparando el ambiente

Para comenzar esta segunda parte del curso necesitamos descargar la versión del curso anterior que podemos encontrar en el siguiente [click aqui](https://github.com/alura-cursos/JPA-con-hibernate-Alura-/tree/stage-final "click aqui").

![imagen de github](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag1.JPG "imagen de github")

- En la imagen superior podemos ver como se encuentra el repositorio vamos a descargar el archivo .zip.

![zip](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag2.png "zip")

- Luego vamos a extraer este archivo en la carpeta donde se encuentra el resto de las informaciones de jpa para este curso.

![workspace](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag3.JPG "workspace")

- Luego de haber descargado la información del curso anterior vamos a abrir la IDE de eclipse, seleccionar en la pestaña de nuevo abrir un proyecto existente, seleccionamos el archivo del directorio y finalizar.

![openproyect](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag4.png "openproyect")

![import proyect](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag5.JPG "import proyect")

## Configurando H2 database

En la parte anterior almacenamos la información en la base de datos H2 en memoria y vimos las querys para cada construcción para esto no precisamos descargar el software de H2.

En esta parte del curso vamos a ver las tablas y los registros almacenados, luego de haber ejecutado nuestra API de tienda_alura.

- Para esto necesitamos descargar el software de H2 click aquí en windows seleccionamos windows installer e instalamos el software.

![h2](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag6.JPG "h2")

![](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag7.JPG)

- Para configurar la url de la base de datos vamos a ejecutar el software de h2 y en el panel de inicio, vamos a hacer click derecho sobre el icono de la base de datos seleccionamos crear una nueva base de datos.
1.
![H2 console](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag8.png "H2 console")

2.
![](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag9.png)

![](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag10.JPG)

- En la ruta de la base de datos el último nombre va a ser el nombre de la base de datos y el restante las carpetas del directorio.

- Luego vamos a ejecutar la aplicación de h2 y modificar los valores con los de la base de datos que acabamos de crear (url,usuario,password) y seleccionamos connect.

![](https://caelum-online-public.s3.amazonaws.com/1991-java-y-jpa/imag11.JPG)

### Haga lo que hicimos en aula: más sobre relacionamientos

En esta primera aula realizamos una modificación en el archivo persistence.xml donde configuramos las propiedades que nos dan acceso a la base de datos. En el curso anterior habíamos configurado la base de datos en la memoria, en esta parte vamos a almacenar los datos en un archivo local que nos va a permitir observar las modificaciones que estamos realizando.

```java
<persistence-unit name="tienda" transaction-type="RESOURCE_LOCAL">
    <properties>
        <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
        <property name="javax.persistence.jdbc.url" value="jdbc:h2:C:\Users\Public\Alura\jpa\database"/>
        <property name="javax.persistence.jdbc.user" value="sa"/>
        <property name="javax.persistence.jdbc.password" value="1234"/>

        <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>            
        <property name="hibernate.show_sql" value="true"/>
        <property name="hibernate.format_sql" value="true"/>
        <property name="hibernate.hbm2ddl.auto" value="create-drop"/>

    </properties>
```

- Vamos a crear dos nuevas entidades que nos van a permitir ampliar nuestro modelo inicial donde registramos un producto por categoría, ahora podremos registrar los pedidos que realizan diversos clientes de estos productos, por lo tanto vamos a crear la entidad Pedido y la entidad cliente que estarán relacionados con la anotación ManyToOnedonde un cliente tendrá muchos pedidos similar a la entidad Producto con Cliente.

```java
@Entity
@Table(name="pedidos")
public class Pedido {
        @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha=LocalDate.now();
    @Column(name="valor_total")
    private BigDecimal valorTotal;

    @ManyToOne
    private Cliente cliente;

@OneToMany(mappedBy="pedido", cascade=CascadeType.ALL)
private List<ItemsPedido> items=new ArrayList<>();

public void agregarItems(ItemsPedido item) {
        item.setPedido(this);
        this.items.add(item);
    }

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
    }
    public Pedido() {}
    //getters / setters
}
@Entity
@Table(name="clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String dni;

    public Cliente() {}

    public Cliente(String nombre, String dni) {
        this.nombre=nombre;
this.dni=dni;
    }

    //getters / setters
}
```

Para conseguir relacionar los pedidos de los clientes a los productos podemos crear un atributo con la entidad `@ManyToMany` y jpa crea automaticamente una nueva entidad, en nuestro caso tenemos que crear una nueva entidad y realizar un relacionamento bidireccional para que esa entidad intermedia contenga atributos auxiliares.

```java
@Entity
@Table(name="items_pedido")
public class ItemsPedido {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private int cantidad;
    private BigDecimal precioUnitario;

    @ManyToOne
    private Producto producto;

    @ManyToOne
    private Pedido pedido;

    public ItemsPedido() {}

    public ItemsPedido(int cantidad, Producto producto, Pedido pedido) {
        this.cantidad = cantidad;
        this.producto = producto;
        this.pedido = pedido;
        this.precioUnitario=producto.getPrecio();
    }

}
```

- Como vamos a guardar nuevos registros debemos crear clases DAO para estas nuevas entidades excepto para ItemPedido donde JPA reconoce que es un relacionamiento bidireccional que conecta un grupo de entidades con otro.

```java
public class PedidoDao {

    private EntityManager em;

    public PedidoDao(EntityManager em) {
        this.em = em;
    }
public void guardar(Pedido pedido) {
        this.em.persist(pedido);
    }

…

```

```java
public class ClienteDao {
    private EntityManager em;

    public ClienteDao(EntityManager em) {
        this.em = em;
    }

    public void guardar(Cliente cliente) {
        this.em.persist(cliente);
    }
…
```

```java
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

    }
…}
```

### Lo que aprendimos

En esta aula, aprendiste:

- Mapeo de nuevas entidades en la aplicación según el modelado de la base de datos.
- Mapeo de una relación con cardinalidad de muchos a muchos.
- Mapeo de una relación bidireccional.
- Cómo persistir entidades que tienen relaciones bidireccionales.

### Proyecto del aula anterior


¿Comenzando en esta etapa? Aquí puedes descargar los archivos del proyecto que hemos avanzado hasta el aula anterior.

[Descargue los archivos en Github](https://github.com/alura-cursos/JPA-con-hibernate-Alura-II/tree/stage_1 "Descargue los archivos en Github") o haga clic [aquí](https://github.com/alura-cursos/JPA-con-hibernate-Alura-II/archive/refs/heads/stage_1.zip "aquí") para descargarlos directamente.

************