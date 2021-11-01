/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

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
        
        // Creamos el objeto de tipo query con una consulta predefinida en la clase entidad Provincia
        // la cual nos devuelve todos los registro de Provincia en la DB
        Query queryProvincias = em.createNamedQuery("Provincia.findAll");
        
        // Para ejecutar la consulta anterior debemos llamar al metodo getResultList del objeto query
        // el cual devuelve la consulta especificada en un objeto de tipo List al que se recomienda tipar
        List<Provincia> listProvincias = queryProvincias.getResultList();
        
    }
    
}
