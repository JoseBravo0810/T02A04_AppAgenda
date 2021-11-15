/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import resources.entities.Persona;

/**
 * FXML Controller class
 *
 * @author jose
 */
public class AgendaViewController implements Initializable {

    // Creamos el objeto EntityManager para intereactuar con la DB (modelo).
    private EntityManager entityManager;
    
    // Creamos la variable persona donde se almacenara la persona seleccionada de a lista para poder editar sus datos
    private Persona personaSeleccionada;
    
    // Variables autogeneradas con Make Controller sobre el archivo FXML
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona, String> columnNombre;
    @FXML
    private TableColumn<Persona, String> columnApellidos;
    @FXML
    private TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private Button botonGuardar;
    @FXML
    private AnchorPane rootAgendaView;
    
    // Creamos un metodo para establecer el objeto EntityMAnager
    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
    
    /**
     * Al iniciar la plicacion enlazamos las propiedades de la clase persona con las variables autogeneradas al realizar el MakeController sobre el fichero FXML
     * para poder operar sobre ellas en la aplicacion
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    /*
        Si hacemos como en los demas, nos devuelve un objeto de tipo provincia y eso devuelve el toString de provincia.
        Con esta expresion lambda establecemos como valor de la celda el valor del atributo nombre del objeto Provincia relaconado con la persona asociada, es decir,
        hacemos que se meuestre el Nombre de la provincia a la que pertenezca la persona
    */
        columnProvincia.setCellValueFactory(
            cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            
            if(cellData.getValue().getProvincia() != null)
            {
                property.setValue(cellData.getValue().getProvincia().getNombre());
            }
            
            return property;
        });
        
        // Añadimos un listener que se ejecutará cuando seleccionemos una persona distinta de la lista
        // como es un escuchador (Listener), aunque el metodo initialize se ejecute solo al crear la tabla este fragmento de codigo se ejecutará
        // cada vez que se produzca el evento que esta registrando el listener, que es un cambio en la seleccion de a persona en la lista
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                // El valor newValue contiene la nueva persona seleccionada, la cual se almacena en la propiedad de la clase personaSeleccionada
                personaSeleccionada = newValue;
                
                // Si se selecciona una persona (persona seleccionada distinto de NULL)
                if(personaSeleccionada != null)
                {
                    // Cambiamos los TextField para mostrar el nombre y apellido de la persona
                    textFieldNombre.setText(personaSeleccionada.getNombre());
                    textFieldApellidos.setText(personaSeleccionada.getApellidos());
                }
                else
                {
                    // Si no se ha seleccionado ninguna persona (no hay seleccion), los campos se establecen vacios
                    textFieldNombre.setText("");
                    textFieldApellidos.setText("");
                }
        });
    }    
    
    // Creamos una clase para cargar todas las personas dentro de la base de datos de la agenda en la tabla
    public void cargarTodasPersonas(){
        // Creamos un objeto Query para realizar la consulta predefinida findAll y asi obtener un objeto List con todas laspersonas de la DB
        Query queryPersonaFindAll = entityManager.createNamedQuery("Persona.findAll");
        // Creamos un objeto List para almacenar las personas devueltas por la consulta
        List<Persona> listPersona = queryPersonaFindAll.getResultList();
        // Establecemos ese objeto List como un objeto ObservableArray que seran los elementos contenidos en la vista de la agenda
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona));
    }

    // MEtodo que se ejecuta al hacer click en el boton Guardar (es su metodo asociado en el FXML). Autogenerado con Make Controller
    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        
        // Primero comprobamos que haya una persona seleccionada, ya que de lo contrario este boton no debe hacer nada
        if(personaSeleccionada != null)
        {
            // Le establecemos a persona seleccionada el nombre y el apellido que se encuentre en los TextField
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());
            
            // Iniciamos la transaccion con la base de Datos para modificar a la persona de fomra permanente
            entityManager.getTransaction().begin();
            // Actualizamos el registro de la persona seleccionada con los datos nuevos.
            entityManager.merge(personaSeleccionada);
            // Finalizamos la transaccion para que persistan los datos
            entityManager.getTransaction().commit();
            
            // Por ultimo actualizamos la Tabla con la lista de personas (actualizamos la vista) para que aparezcan los nuevos valores guardados
            // Variable para saber el numero de fila de la persona seleccionada
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            // Establecemos como elemento de la fila seleccionada los nuevos valores
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);
            
            // Si no ponemos nada mas, el foco de la ventana se quedará en el boton ya que es el ultimo componente que hemos seleccionado
            // vamos a devolver el foco a la fila seleccionada y modificada por si el usuario quiere moverse por la agenda usando el teclado
            
            // Primero extraemos la posicion en la tabla de la persona seleccionada como una TablePosition
            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null);
            // El metodo getFocusModel() devuelve el FocusModel del TAbleView (el objeto que tiene el foco), y lo establece con el metodo
            // focus(pos) al elemento en el indice proporcionado con pos
            tableViewAgenda.getFocusModel().focus(pos);
            // Ahora le pedimos antecesor mas elevado en la jerarquia que le de el foco de la ventana al tableView, y como el foco del tableView lo acabamos
            // de establecer en el objeto seleccionado, será este el que aparezca marcado
            tableViewAgenda.requestFocus();
        }
        
    }

    // Metodo que se ejecutara al darle click a nuevo
    @FXML
    private void onActionButtonNuevo(ActionEvent event) {
        
        try
        {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/Edition.fxml"));
            // Cargamos en un contenedor Parent la vista cargada
            Parent rootDetalleView = fxmlLoader.load();
            
            // Instanciamos el controlador de la vista detalle. Necesitamos que esta linea se ubique tras al carga en el loader de la vista detalle
            // ya que de lo contrario se produciria un error
            EditionController editionController = (EditionController) fxmlLoader.getController();
            
            // Hacemos uso del metodo setRootAgendaView() para pasarle al controlador de la vista detalle el nodo root (contenedor que contiene los elementos) de la vista de la lista
            // la cual tenemos almacenada en la propiedad de esta clase rootAgendaView (el identificador asignado al AnchorPane en SceneBuilder)
            editionController.setRootAgendaView(rootAgendaView);
            
            // Oculatmos la vista actual, AgendaView, vista de la lista
            rootAgendaView.setVisible(false);
            
            // Añadimos la vista de detalle al StackPAne principal para que sea la que ahora esta visible
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
            // Pasamos el objeto TableView con el listado de personas al controlador de edicion a traves del metodo setTableViewPrevio
            editionController.setTableViewPrevio(tableViewAgenda);
            
            // Pasamos el EntityManager, la persona, e indicamos que es una nueva persona
            // Como es una nueva persona, debemos crear una nueva persona
            personaSeleccionada = new Persona();
            // Pasamos los datos al controlador de edicion a traves del metodo setPersona
            editionController.setPersona(entityManager, personaSeleccionada, true);
            
            editionController.mostrarDatos();
        }
        catch (IOException iox)
        {
            // El metodo getLogger pide un objeto de tipo Class, por ello he modificado para que en lugar de devolver un String con el nombre de la clase
            // me devuelva la clase
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, iox);
        }
        
    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) {
        
        // Añadimos comprobacion de que haya una persona seleccionada, ya que sino error
        if(personaSeleccionada != null)
        {
            try
            {
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/Edition.fxml"));
            // Cargamos en un contenedor Parent la vista cargada
            Parent rootDetalleView = fxmlLoader.load();
            
            // Instanciamos el controlador de la vista detalle. Necesitamos que esta linea se ubique tras al carga en el loader de la vista detalle
            // ya que de lo contrario se produciria un error
            EditionController editionController = (EditionController) fxmlLoader.getController();
            
            // Hacemos uso del metodo setRootAgendaView() para pasarle al controlador de la vista detalle el nodo root (contenedor que contiene los elementos) de la vista de la lista
            // la cual tenemos almacenada en la propiedad de esta clase rootAgendaView (el identificador asignado al AnchorPane en SceneBuilder)
            editionController.setRootAgendaView(rootAgendaView);
            
            // Oculatmos la vista actual, AgendaView, vista de la lista
            rootAgendaView.setVisible(false);
            
            // Añadimos la vista de detalle al StackPAne principal para que sea la que ahora esta visible
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
            // Pasamos el objeto TableView con el listado de personas al controlador de edicion a traves del metodo setTableViewPrevio
            editionController.setTableViewPrevio(tableViewAgenda);
            
            // Pasamos el EntityManager, la persona, e indicamos que es la edicion de una persona ya existente
            // Como es una persona ya existente y editaremos la persona que se seleccione de la lista, personaSeleccionada sera la seleccion de la lista
            // Pasamos los datos al controlador de edicion a traves del metodo setPersona
            editionController.setPersona(entityManager, personaSeleccionada, false);
            
            editionController.mostrarDatos();
            }
            catch (IOException iox)
            {
                // El metodo getLogger pide un objeto de tipo Class, por ello he modificado para que en lugar de devolver un String con el nombre de la clase
                // me devuelva la clase
                Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, iox);
            }
        }
        else
        {
            Alert alert = new Alert(AlertType.INFORMATION,"Debe seleccionar una persona antes editarla.");
            alert.showAndWait();
        }
        
        
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        
        // Creamos mensaje de alerta para confirmacion
        Alert alert = new Alert(AlertType.CONFIRMATION);
        // Establecemos el titulo de la ventana
        alert.setTitle("Confirmar");
        // Establecemos el texto del encabezado, la pregunta
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        // Establecemos el cuerpo del mensaje, la persona a borrar
        alert.setContentText(personaSeleccionada.getNombre() + " "
                            + personaSeleccionada.getApellidos());
        // Comprobamos el boton pulsado por la persona
        Optional<ButtonType> result = alert.showAndWait();
        // Si presiona el boton de confirmar
        if(result.get() == ButtonType.OK)
        {
            // Si lo acepta iniciamos transaccion
            entityManager.getTransaction().begin();
            // Nos aseguramos de que la persona exista en la DB antes de realizar el remove
            // ya que si es una persona nueva no estara en laDB
            entityManager.merge(personaSeleccionada);
            // Eliminamos a la persona seleccionada
            entityManager.remove(personaSeleccionada);
            // Finalizamos la transaccion con la base de datos
            entityManager.getTransaction().commit();
            
            // Eliminamos a la persona seleccionada de la tabla
            tableViewAgenda.getItems().remove(personaSeleccionada);
            // No le damos el foco a ningun objeto
            tableViewAgenda.getFocusModel().focus(null);
            // Pedimos el foco a la tabla para poder movernosentre los contactos con el las flechas de direccion
            tableViewAgenda.requestFocus();
        }
        else // Si pulsa el boton de cancelar
        {
            // Codigo visto antes para devolver el foco al objeto del a tabla para que no se quede ene l boton suprimir
            int numFilaSeleccionada=
            tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda,
                numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
        
    }
}
