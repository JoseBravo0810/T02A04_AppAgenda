/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import java.sql.DriverManager;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import resources.entities.Provincia;

/**
 *
 * @author jose
 */
public class AppAgenda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        
        // Constructor de la provincia Cadiz
        Provincia provinciaCadiz = new Provincia(1, "Cadiz");
        
        // Constructor de la provincia de Sevilla
        Provincia provinciaSevilla = new Provincia();
        
        // Metodo para establecer nombre a sevilla
        provinciaSevilla.setNombre("Sevilla");
        
    /* Lo comentamos para que no se inserten los objetos cada vez que ejecutemos la aplicacion
        // Iniciamos la transaccion
        em.getTransaction().begin();
        // Almacenamos los objetos en la base de datos
        em.persist(provinciaCadiz);
        em.persist(provinciaSevilla);
        // Volcamos los cambios a la DB
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
