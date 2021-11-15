/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import resources.entities.Provincia;

/**
 *
 * @author jose
 */
public class ConsultaProvincias {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Instanciamos la clase Entity Manager Factory, para crear un gestor de entidad a raiz del archivo persistence.xml
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        // Diche clase nos sirve para crear el objeto gestor de entidad que será sobre el que podremos ejecutar las operaciones contra la DB
        EntityManager em = emf.createEntityManager();
        
        /*
            Constultar los datos de todas las provincias y devolver el nombre de las provincias de la tabla
        */
        
        // Creamos el objeto de tipo query con una consulta predefinida en la clase entidad Provincia
        // la cual nos devuelve todos los registro de Provincia en la DB
        Query queryProvincias = em.createNamedQuery("Provincia.findAll");
        
        // Para ejecutar la consulta anterior debemos llamar al metodo getResultList del objeto query
        // el cual devuelve la consulta especificada en un objeto de tipo List al que se recomienda tipar
        List<Provincia> listProvincias = queryProvincias.getResultList();
        
        // Iteramos sobre la lista para obtener el nombre de todas als provincias
        System.out.println("Provincias: \n" + "------------");
        for(Provincia provincia: listProvincias)
        {
            System.out.println(provincia.getNombre());
        }
        System.out.println();
        
        /*
            Consultar la tabla provincia buscando una provincia concreta por nombre. Buscaremos Cadiz
        */
        
        // Creamos el objeto consulta que sera el que lance la consulta a la DB
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        // Asignamos el parametro requerido por la consulta predefinida que se esta realizando
        queryProvinciaCadiz.setParameter("nombre", "Cadiz");
        // Ejecutamos la consulta y recogemos el resultado en un objeto List tipado a provincias
        List<Provincia> listProvinciasCadiz = queryProvinciaCadiz.getResultList();
        
        // Recorremos el resultado devuelto (aunque solo hay uno siempre se almacena en un List) y mostramos el Id y el nombre de la provincia
        for(Provincia provinciaCadiz: listProvinciasCadiz)
        {
            System.out.println(provinciaCadiz.getId() + ":");
            System.out.println(provinciaCadiz.getNombre());
        }
        System.out.println();
        
        /*
            Uso del metodo: <T> T find(Class<T> EntityClass, Object primaryKey), para encontrar un registro sabiendo su clave primaria
        */
        
        // Buscamos la provincia con ID = 2, y almacenamos el registro en el objeto provinciaId2
        Provincia provinciaId2 = em.find(Provincia.class, 2);
        // Si se ha encontrado, es decir, si provinciaId2 no es NULL
        if(provinciaId2 != null)
        {
            System.out.println(provinciaId2.getId() + ":");
            System.out.println(provinciaId2.getNombre());
        }
        else
        {
            // Si no se ha encontrado, mensaje de error
            System.out.println("No hay ninguna provincia con ID = 2");
        }
        System.out.println();
        
        /*
            Modificacion de objetos (registros, ya que cada registro es un objeto en java)
                - Modificar el codigo de Cadiz de null a CA.
        
                Metodos no implementados
                - Query.getFirstResult() -> Devuelve el primer resultado de la consulta cargada en el objeto query
                - Query.getMaxResult() -> Devuelve el numero de resultados que ha obtenido la consulta
        */
        
    /*
        // Estas lineas estan comentadas debido a que ya tenemos un objeto Query con la provincia de Cadiz cargado en él de pasos anteriores, no es necesario cargarlo de nuevo
        // Creamos objeto query para realizar la consulta, dicho objeto query utiliza la consulta predefinida para buscar por nombre en los registros de provincia
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        // Asignamos el parametro requerido por la consulta predefinida, el nombre
        queryProvinciaCadiz.setParameter("nombre", "Cadiz");
        
        // Las siguientes lineas se comentan debido a que implementamos el metodo getSingleResult() para probarlo aprovechando que la consulta solo devuelve un registro, la provincia de cadiz
        // Cargamos en una lista los resultados de la consulta
        List<Provincia> listProvinciasCadiz = queryProvinciaCadiz.getResultList();
        // Iniciamos transaccion
        em.getTransaction().begin();
        // Recorremos la lista para asignar el codigo al parametro codigo del objeto devuelto en la consulta y realizar el merge para actualizar el objeto en la DB
        for(Provincia provinciaCadiz: listProvinciasCadiz)
        {
            provinciaCadiz.setCodigo("CA");
            em.merge(provinciaCadiz);
        }
        // Finalizamos la transaccion
        em.getTransaction().commit();
    */
    /* 
        // Ejecutamos el metodo getSingleResult() sobre el query que tenia cargada la provincia de cadiz (pasos previos). Es necesario hacer un casting debido a que el metodo devuelve una vble de tipo Object
        Provincia cadiz = (Provincia) queryProvinciaCadiz.getSingleResult();
        
        // Comenzamos la transaccion
        em.getTransaction().begin();
        // Modificamos el codigo de cadiz en la DB
        cadiz.setCodigo("CA");
        // Actualizamos el registro de cadiz en la DB
        em.merge(cadiz);
        // Finalizamos la transaccion para que persistan los datos (se guarde el codigo de Cadiz)
        em.getTransaction().commit();
    */
        /*
            Eliminacion de un determinado objeto (registro en la DB)
    Lo comentamos porque se considera un metodo inseguro, y esque siempre, cada vez que se ejecute la aplicacion, este metodo cargara su main, el cual elimina el registro 15 de la tabla provincia, 
    error grave si es en una aplicacion real. Las demas tambien se consideran inseguras, y es que cualquiera con acceso a esta clase podria acceder a la base de datos, pero esta es la que causa
    , a mi parecer, un conflicto grave para la aplicacion
        */
    /*
        // Buscamos la provincia con id 15 (no existe)
        Provincia provinciaId15 = em.find(Provincia.class, 15);
        // Comenzamos transaccion
        em.getTransaction().begin();
        // Si encuentra el registro con id 15
        if(provinciaId15 != null)
        {
            // Elimina el registro
            em.remove(provinciaId15);
        }
        else
        {
            // Si no, mensaje de error
            System.out.println("No hay ninguna provincia con ID = 15");
        }
        System.out.println();
        // Finalizamos la transaccion
        em.getTransaction().commit();
    */
        
        // Cerramos la conexion con la base de datos
        em.close();
        emf.close();
        try
        {
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        }
        catch(SQLException sqlx){}
    
    }
    
}
