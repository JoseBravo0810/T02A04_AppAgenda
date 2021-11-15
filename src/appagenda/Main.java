/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author jose
 */
public class Main extends Application {
    
    // Objetos EntityManager para mantener la conexion con la Base de Datos, el modelo de la aplicacion
    private EntityManagerFactory emf;
    private EntityManager em;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
               
        // Declaramos un StackPane que sera el nodo raiz principal, el nodo donde se conmutara la vista entre los dos fichero FXML
        // Es de tipo StackPane debido a que este tipo de contenedor permite apilar paneles o vistas una sobre otra para poder conmutar entre las dos vistas que estamos trabajando
        StackPane rootMain = new StackPane();
        
        // Cargamos el fichero fxml, obtenemos una referencia al fichero (tenemos en cuenta que el fichero raiz del proyecto es la carpeta src). Si usamos ruta relativa deberiamos poner ../resources/fxml/AgendaView.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/AgendaView.fxml"));
        // Creamos una variable Parent que sera el nodo raiz del escenario de nuestra aplicacion. Este nodo raiz sera la carga del archivo FXML para generar automaticamente la vista
        // Esto necesita de la captura de la posible excepcion de entrada-salida (input-output exception) al cargar el fichero FXML
    //  Parent root = fxmlLoader.load();
        /* Cambio, comentamos la linea de arriba, ahora es un Pane ya que de Pane descienden los demas contenedores y podremos usar cualquiera de ellos como elemento principal de la vista */
        Pane rootAgendaView = fxmlLoader.load();
        // Añadimos este Pane al StackPane que es el nodo raiz de la escena
        rootMain.getChildren().add(rootAgendaView);
        
        // Creamos la conexion con la base de datos, creando los objetos EntityManager y EntityManagerFactory para cargar el archicvo persistence.xml
        emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        em = emf.createEntityManager();
        
        // Cargamos la clase controladora que sera la que controle las acciones de la ventana y realiza el vinculo entre modelo y vista en el modelo MVC
        AgendaViewController agendaViewController = (AgendaViewController) fxmlLoader.getController();
        // Establecemos el EntityManager del controlador que acabamos de crear
        agendaViewController = (AgendaViewController) fxmlLoader.getController();
        
        // Cargamos el modelo en la clase del controlador, para que pueda tener acceso a los datos conenidos en la BD
        agendaViewController.setEntityManager(em);
        
        // Llamamos al metodo cargarTodasPersonas() para que la vista de la aplicacion sea la tabla con las personas contenida en la DB
        agendaViewController.cargarTodasPersonas();
        
        
        /* 
            Esto, como siempre, es la carga del nodo raiz en la escena para crearla, establece titulo a la ventana, establece la escena como la escena principal y muestra el escenario
        */
        Scene scene = new Scene(rootMain);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Cuando se cierre la aplicacion se ejecutara el codigo que este en este metodo que estamos sobreescribiendolo (el original solo llama al metodo de cierre del padre (no hace nada)
    @Override
    public void stop() throws Exception{
        // Cerramos la conexion con la base de datos
        em.close();
        emf.close();
        try
        {
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        }
        catch(SQLException sqlx){}
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
