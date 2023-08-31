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

### Haga lo que hicimos en aula: consultas avanzadas

En esta aula vamos a realizar consultas utilizando recursos de JPA que nos permiten realizar consultas con funciones de agregación tipo `SUM()`, `AVG()`, `MIN()`,... antes de proceder debemos agregar unos últimos detalles en la clase pedido y ítem pedido que nos permiten obtener el valor total.

- Primero vamos a asignar el nombre de la columna con la anotación @Column(name=”columna”).

```java
@Column(name="valor_total")
private BigDecimal valorTotal=new BigDecimal(0);
```

- Vamos a agregar un nuevo método que nos permite relacionar itemPedido con Pedido, así como Pedido con ItemPedido. Además de las anotaciones tenemos que asignar el valor correcto en nuestro relacionamiento.

```java
public void agregarItems(ItemsPedido item) {
    item.setPedido(this);
    this.items.add(item);
this.valorTotal= this.valorTotal.add(item.getValor());
}
```

- En la clase itemPedido agregamos un método que nos permite calcular el valor total a partir del precio y de la cantidad.

```java
public BigDecimal getValor() {
    return this.precioUnitario.multiply(new BigDecimal(this.cantidad));
}
```

- Por último en el DAO vamos agregar nuestros métodos de consulta. Vamos a utilizar las funciones de agregación para consultar el valor total en la base de datos con la función SUM().
```java
public BigDecimal valorTotalVendido() {
    String jpql= "SELECT SUM(p.valorTotal) FROM Pedido p";
    return em.createQuery(jpql,BigDecimal.class).getSingleResult();
}
```

- Otro tipo de consulta es cuando queremos obtener en nuestra consulta atributos de múltiples entidades en una única consulta e incluso funciones de agregación para eso tenemos dos(2) métodos un crearemos un método que retorna una lista de Objetos para formar el relatorio.

```java
public List<Object[]> relatorioDeVentas(){
    String jpql="SELECT producto.nombre, "
            + "SUM(item.cantidad), "
            + "MAX(pedido.fecha) "
            + "FROM Pedido pedido "
            + "JOIN pedido.items item "
            + "JOIN item.producto producto "
            + "GROUP BY producto.nombre "
            + "ORDER BY item.cantidad DESC";
    return em.createQuery(jpql,Object[].class).getResultList();
}
```

- Y la forma más recomendada es mediante la construcción de una clase VO (value object) es una clase que nos permite enviar información dentro de nuestra aplicación.
```java
public class RelatorioDeVenta {

    private String nombreDelProducto;
    private Long CantidadDeProducto;
    private LocalDate FechaDeUltimaVenta;

    public RelatorioDeVenta(String nombreDelProducto, Long cantidadDeProducto, LocalDate fechaDeUltimaVenta) {
        this.nombreDelProducto = nombreDelProducto;
        CantidadDeProducto = cantidadDeProducto;
        FechaDeUltimaVenta = fechaDeUltimaVenta;
    }

    public String getNombreDelProducto() {
        return nombreDelProducto;
    }

    public void setNombreDelProducto(String nombreDelProducto) {
        this.nombreDelProducto = nombreDelProducto;
    }

…

public List<RelatorioDeVenta> relatorioDeVentasVO(){
    String jpql="SELECT new com.latam.alura.tienda.vo.RelatorioDeVenta(producto.nombre, "
            + "SUM(item.cantidad), "
            + "MAX(pedido.fecha)) "
            + "FROM Pedido pedido "
            + "JOIN pedido.items item "
            + "JOIN item.producto producto "
            + "GROUP BY producto.nombre "
            + "ORDER BY item.cantidad DESC";
    return em.createQuery(jpql,RelatorioDeVenta.class).getResultList();
}
```

- En una clase de prueba vamos a conseguir visualizar el resultado de las consultas.
**Método 1)**

```java
BigDecimal valorTotal=pedidoDao.valorTotalVendido();
System.out.println("Valor Total: "+ valorTotal);

List<Object[]> relatorio = pedidoDao.relatorioDeVentasVO();
for(Object[] obj:relatorio){
System.out.println(obj[0]);
System.out.println(obj[1]);
System.out.println(obj[2]);
}
```
**Método 2)**

```java
List<RelatorioDeVenta> relatorio = pedidoDao.relatorioDeVentasVO();
relatorio.forEach(System.out::println);
```

- Para organizar las consultas podemos utilizar un recurso de JPA @NamedQueries que nos permite colocar una determinada consulta dentro de la entidad para indicar que esa consulta tiene un uso particular.
```java
@Entity
@Table(name="productos")
@NamedQuery(name="Producto.consultarPrecioPorNombre", query="SELECT P.precio FROM Producto AS P WHERE P.nombre=:nombre")
public class Producto{...
```

- Aun cuando se encuentra indicada en la entidad el método continúa en la clase DAO y la consulta se encuentra en la entidad.
```java
public BigDecimal consultarPrecioPorNombreDeProducto(String nombre) {
    return em.createNamedQuery("Producto.consultarPrecioPorNombre",BigDecimal.class).setParameter("nombre", nombre).getSingleResult();
}
```

### Lo que aprendimos

En esta aula, aprendiste:

- Realización de consultas utilizando funciones de agregación como min, max, avg y sum;
- Cómo escribir consultas de informes usando JPQL;
- Cómo usar el recurso seleccionado nuevo en consultas JPQL;
- Realización de consultas mediante consultas con nombre.

### Proyecto del aula anterior

¿Comenzando en esta etapa? Aquí puedes descargar los archivos del proyecto que hemos avanzado hasta el aula anterior.

[Descargue los archivos en Github](https://github.com/alura-cursos/JPA-con-hibernate-Alura-II/tree/stage_2 "Descargue los archivos en Github") o haga clic [aquí](https://github.com/alura-cursos/JPA-con-hibernate-Alura-II/archive/refs/heads/stage_2.zip "aquí") para descargarlos directamente.


### PHaga lo que hicimos en aula: performance de consultas

Al construir nuestra aplicación mediante el uso de recursos que realizan operaciones que no se encuentran explicitas debemos estudiar la documentación para entender cual es la ciencia detrás del framework o recurso.

Cuando realizamos consultas con la anotación `@ManyToOne` o `@OneToOne` detrás de escena JPA aplica una estrategia de cargamento de información llamada Eager o Anticipada o Proactiva realizando JOINS entre tablas. Pero no acaba allí, ya que si esa entidad que tiene el JOIN, tiene otras entidades dentro de sus atributos marcados con la anotación finalizando ToOne, también serán cargadas dentro de la consulta. Esto puede saturar la memoria y afectar seriamente la velocidad de carga, ya para corregir debemos utilizar el parámetro de carga FerchType.LAZY en todas las anotaciones ToOne que le indica a JPA solo cargar la entidad si es solicitada.

```java
public class Producto{
…    
    @ManyToOne(fetch=FetchType.LAZY)
    private Categoria categoria;
```

```java
public class Pedido {
    …
    @ManyToOne(fetch=FetchType.LAZY)
    private Cliente cliente;
```

```java
public class ItemsPedido {
    …
    @ManyToOne(fetch=FetchType.LAZY)
    private Producto producto;

    @ManyToOne(fetch=FetchType.LAZY)
    private Pedido pedido;
```

- Al realizar esta corrección se presenta un posible inconveniente donde nos encontremos con la necesidad de utilizar ese atributo de entidad. Pero para ese momento ya el EntityManager se puede encontrar cerrado, por lo que tenemos que planear nuestras consultas previniendo, el uso de esa entidad aún cuando se encuentre cerrado el EntityManager.

```java
public Pedido consultarPedidoConCliente(Long id) {
    String jpql="SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.id=:id";
    return em.createQuery(jpql,Pedido.class).setParameter("id", id).getSingleResult();
}

public class PruebaDeDesempenho {
    public static void main(String[] args) throws FileNotFoundException {
//        LoadRecords.cargarRegistros();
        EntityManager em = JPAUtils.getEntityManager();
        PedidoDao pedidoDao = new PedidoDao(em);
        Pedido pedidoConCliente = pedidoDao.consultarPedidoConCliente(2l);
        em.close();

//        System.out.println(pedido.getFecha());
//        System.out.println(pedido.getItems().size());
        System.out.println(pedidoConCliente.getCliente().getNombre());
    }
}
```

### Lo que aprendimos

En esta aula, aprendiste:

- Cómo funcionan las estrategias EAGER y LAZY, en consultas de entidades que tienen relaciones;
- Por qué JPA podría lanzar LazyInitializationException en ciertas situaciones;
- Buenas prácticas en la carga de entidades con relaciones;
- Cómo realizar consultas programadas con la función de búsqueda de combinación.

#### Proyecto del aula anterior

¿Comenzando en esta etapa? Aquí puedes descargar los archivos del proyecto que hemos avanzado hasta el aula anterior.

[Descargue los archivos en Github](https://github.com/alura-cursos/JPA-con-hibernate-Alura-II/tree/stage_3 "Descargue los archivos en Github") o haga clic [aquí](https://github.com/alura-cursos/JPA-con-hibernate-Alura-II/archive/refs/heads/stage_3.zip "aquí") para descargarlos directamente.

### Haga lo que hicimos en aula: Criteria API

Cuando queremos realizar consultas con múltiples parámetros nos encontramos con el problemas que todos ellos deben ser obligatorios, de lo contrario consultaria elementos nulos en tabla. Para evitar este error tenemos que usar parámetros dinámicos que nos permiten realizar consultas con múltiples parámetros y en caso de que alguno de estos sea nulo la consulta simplemente ignorará este parámetro y realizará la consulta con los parámetros existentes. Y en caso de no existir ningún parámetro, realizará la consulta de todos los elementos en la tabla.

```java
public List<Producto> consultarPorParametros(String nombre, BigDecimal precio,LocalDate fecha){
    StringBuilder jpql=new StringBuilder("SELECT p FROM Producto p WHERE 1=1 ");

    if(nombre!=null && !nombre.trim().isEmpty()) {
        jpql.append("AND p.nombre=:nombre ");
    }
    if(precio!=null && !precio.equals(new BigDecimal(0))) {
        jpql.append("AND p.precio=:precio ");
    }
    if(fecha!=null) {
        jpql.append("AND p.fechaDeRegistro=:fecha");
    }
    TypedQuery<Producto> query = em.createQuery(jpql.toString(),Producto.class);
    if(nombre!=null && !nombre.trim().isEmpty()) {
        query.setParameter("nombre", nombre);
    }
    if(precio!=null && !precio.equals(new BigDecimal(0))) {
        query.setParameter("precio", precio);
    }
    if(fecha!=null) {
        query.setParameter("fechaDeRegistro", fecha);
    }
    return query.getResultList();        
}
```

- Adicionalmente podemos realizar la misma consulta dinámica utilizando la API de Criteria que es un poco más compleja y recomendamos documentarse sobre ella pero simplifica la cantidad de condiciones en nuestra aplicación.

```java
public List<Producto> consultarPorParametrosConAPICriteria(String nombre, BigDecimal precio,LocalDate fecha){
    CriteriaBuilder builder = em.getCriteriaBuilder();
    CriteriaQuery<Producto> query = builder.createQuery(Producto.class);
    Root<Producto> from = query.from(Producto.class);

    Predicate filtro = builder.and();
    if(nombre!=null && !nombre.trim().isEmpty()) {
        filtro=builder.and(filtro,builder.equal(from.get("nombre"), nombre));
    }
    if(precio!=null && !precio.equals(new BigDecimal(0))) {
        filtro=builder.and(filtro,builder.equal(from.get("precio"), precio));
    }
    if(fecha!=null) {
        filtro=builder.and(filtro,builder.equal(from.get("fechaDeRegistro"), fecha));
    }
    query=query.where(filtro);
    return em.createQuery(query).getResultList();
}
public class PruebaDeParametros {
    public static void main(String[] args) {
        cargarBancoDeDatos();

        EntityManager em = JPAUtils.getEntityManager();
        ProductoDao productoDao =new ProductoDao(em);

        List<Producto> resultado = productoDao.consultarPorParametros("FIFA", new BigDecimal(10000), null);

        System.out.println(resultado.get(0).getDescripcion());

EntityManager em = JPAUtils.getEntityManager();
        ProductoDao productoDao =new ProductoDao(em);

        List<Producto> resultado = productoDao.consultarPorParametrosConAPICriteria("X", null, null);

        System.out.println(resultado.get(0).getDescripcion());

    }

    private static void cargarBancoDeDatos() {
        Categoria celulares = new Categoria("CELULARES");
        Categoria videoJuegos = new Categoria("VIDEO_JUEGOS");
        Categoria electronicos = new Categoria("ELECTRONICOS");

        Producto celular = new Producto("X", "producto nuevo", new BigDecimal(10000), celulares);
        Producto videoJuego = new Producto("FIFA", "2000", new BigDecimal(10000), videoJuegos);
        Producto memoria = new Producto("memoria ram", "30 GB", new BigDecimal(10000), electronicos);

        EntityManager em = JPAUtils.getEntityManager();
        ProductoDao productoDao = new ProductoDao(em);
        CategoriaDao categoriaDao = new CategoriaDao(em);

        em.getTransaction().begin();

        categoriaDao.guardar(celulares);
        categoriaDao.guardar(videoJuegos);
        categoriaDao.guardar(electronicos);

        productoDao.guardar(celular);
        productoDao.guardar(videoJuego);
        productoDao.guardar(memoria);

        em.getTransaction().commit();
        em.close();
    }
}
```

### Lo que aprendimos

En esta aula, aprendiste:

- Cómo realizar consultas JPQL con parámetros opcionales;
- Cómo funciona la API de criterios JPA;
- Cómo realizar una consulta con parámetros opcionales a través de Criteria API.

### Proyecto del aula anterior

¿Comenzando en esta etapa? Aquí puedes descargar los archivos del proyecto que hemos avanzado hasta el aula anterior.

[Descargue los archivos en Github](https://github.com/alura-cursos/JPA-con-hibernate-Alura-II/tree/stage_4 "Descargue los archivos en Github") o haga clic [aquí](https://github.com/alura-cursos/JPA-con-hibernate-Alura-II/archive/refs/heads/stage_4.zip "aquí") para descargarlos directamente.