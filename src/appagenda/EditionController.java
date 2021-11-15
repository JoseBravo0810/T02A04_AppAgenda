/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import resources.entities.Persona;
import resources.entities.Provincia;

/**
 * FXML Controller class
 *
 * @author jose
 */
public class EditionController implements Initializable {

    // Propiedad de la clase, donde se almacenará el contenedor de la vista principal (la de la lista), para poder hacerlo visible de nuevo al pulsar los botones 
    // Guardar o Cancelar en la vista de detalle. La propiedad puede ser un Pane (generico) o un AnchorPane (utilizado)
    private Pane rootAgendaView;
    
    // Propiedad con la tabla que se muestra en la vista de la lista (para actualizarla al insertar o editar datos
    private TableView tableViewPrevio;
    // Propiedad con la persona que se ha seleccionado (opcion editar)
    private Persona persona;
    // Propiedad para poder operar con la DB
    private EntityManager entityManager;
    // Propiedad que indica si es una persona nueva o una edicion
    private boolean nuevaPersona;
    
    // Variables para el estado civil
    public static final char CASADO='C';
    public static final char SOLTERO='S';
    public static final char VIUDO='V';
    
    // Variable para tener la ruta de la carpeta de fotos
    public static final String CARPETA_FOTOS = "/resources/Fotos";
    
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private TextField textFieldNumHijos;
    @FXML
    private TextField textFieldSalario;
    @FXML
    private CheckBox checkBoxJubilado;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private ToggleGroup estadoCivil;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private ImageView imageViewFoto;
    @FXML
    private AnchorPane rootEditionView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    // Metodo set para la propiedad donde tenemos la vista principal
    public void setRootAgendaView(Pane rootAgendaView)
    {
        this.rootAgendaView = rootAgendaView;
    }
    
    // Metodo para asignar el TableView con la lista a la propiedad tableViewPrevio
    public void setTableViewPrevio(TableView tableViewPrevio)
    {
        this.tableViewPrevio = tableViewPrevio;
    }
    
    //Metodo para asignar la persona seleccionada, el EntityManager e ndicar si es una nueva persona o si se esta editando una existente
    // Se inicia aqui la transaccion con la base de datos, con la finalidad de que si el usuario le da a Cancelar, se haga un rollback de los cambios introducidos
    public void setPersona(EntityManager entityManager, Persona persona, boolean nuevaPersona)
    {
        this.entityManager = entityManager;
        
        // Comenzamos la transaccion
        this.entityManager.getTransaction().begin();
        
        // Si no es una nueva persona se busca la persona seleccionada en el TableView, y sino se asigna la persona pasada como argumento
        if(!nuevaPersona)
        {
            this.persona = this.entityManager.find(Persona.class, persona.getId());
        }
        else
        {
            this.persona = persona;
        }
        
        this.nuevaPersona = nuevaPersona;
    }
    
    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        
        // Con el metodo getScene() conseguimos la escena que tiene el nodo root, es decir, la escena activa de la ventana
        // y con el metodo getRoot() estamos recuperando el nodo raiz del contenedor principal de la palicacion, el StackPane, que es el activo en el scene
        StackPane rootMain = (StackPane) rootEditionView.getScene().getRoot();
        
        boolean errorFormato = false;
        // Variable que contiene el numero de fila seleccionada
        int numFilaSeleccionada = 0;
        
        // Establecemos los datos que tendra la persona que vamos a editar-crear
        persona.setNombre(textFieldNombre.getText());
        persona.setApellidos(textFieldApellidos.getText());
        persona.setTelefono(textFieldTelefono.getText());
        persona.setEmail(textFieldEmail.getText());

        // Extreamos el numero de hijos del textfield
        if(!textFieldNumHijos.getText().isEmpty())
        {
            try
            {
                persona.setNumHijos(Short.valueOf(textFieldNumHijos.getText()));
            }
            catch(NumberFormatException ex)
            {
                // Indcamos que hay error en el formato
                errorFormato = true;
                
                // Alerta en pantalla
                Alert alert = new Alert(AlertType.INFORMATION, "Numero de hijos no valido");
                alert.showAndWait();
                textFieldNumHijos.requestFocus();
            }
        }
        
        // Extraemos y comprobamos salrio
        if (!textFieldSalario.getText().isEmpty())
        {
            try 
            {
                persona.setSalario(BigDecimal.valueOf(Double.valueOf(textFieldSalario.getText()).doubleValue()));
            }
            catch(NumberFormatException ex) 
            {
                // Error en formato
                errorFormato = true;
                // Alerta
                Alert alert = new Alert(AlertType.INFORMATION, "Salario no válido");
                alert.showAndWait();
                // Devplvemos foco al campo
                textFieldSalario.requestFocus();
            }
        }
        
        // Extraemos y comprobamos jubilado
        persona.setJubilado(checkBoxJubilado.isSelected());
        
        // Extraemos y comprobamos estado civil
        if (radioButtonCasado.isSelected())
        {
            persona.setEstadoCivil(CASADO);
        } 
        else if(radioButtonSoltero.isSelected())
        {
            persona.setEstadoCivil(SOLTERO);
        } 
        else if(radioButtonViudo.isSelected())
        {
        persona.setEstadoCivil(VIUDO);
        }
        
        // Extraemos y comprobamos fecha de nacimiento como ya vimos para establecerla en el campo (la conversion)
        if (datePickerFechaNacimiento.getValue() != null)
        {
            LocalDate localDate = datePickerFechaNacimiento.getValue();
            ZonedDateTime zonedDateTime =
            localDate.atStartOfDay(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            persona.setFechaNacimiento(date);
        } 
        else 
        {
            persona.setFechaNacimiento(null);
        }
        
        // Comprobamos tabla relacionada e insertamos los datos de la persona
        if (comboBoxProvincia.getValue() != null)
        {
            persona.setProvincia(comboBoxProvincia.getValue());
        } 
        else 
        {
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar una provincia");
            alert.showAndWait();
            errorFormato = true;
        }
        
        if(!errorFormato)
        {
            try
            {
                // Si es una nueva persona
                if(nuevaPersona)
                {
                    entityManager.persist(persona);
                }
                else
                {
                    entityManager.merge(persona);
                }

                entityManager.getTransaction().commit();

                // Si es una nueva persona
                if(nuevaPersona)
                {
                    // Añadimos la persona a la lista de la tabla
                    tableViewPrevio.getItems().add(persona);
                    // El numero de fila seleccionada será el numero de items que habia en la lista anterior (en indice)
                    numFilaSeleccionada = tableViewPrevio.getItems().size() - 1;
                    // Seleccionamos ese elemento en la lista
                    tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                    // Si es necesario, se hara scroll hasta llegar al nuevo
                    tableViewPrevio.scrollTo(numFilaSeleccionada);
                }
                else // Si es una edicion
                {
                    // Seleccionamos la posicion donde se encontraba la persona seleccionada (ultima seleccion)
                    numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
                    // Se establece la persona (actualiza) en la tabla
                    tableViewPrevio.getItems().set(numFilaSeleccionada, persona);
                }
                
                // Instanciamos la posicion de la persona editada o nueva
                TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
                // Le damos el foco a esa persona
                tableViewPrevio.getFocusModel().focus(pos);
                // Peimos el foco
                tableViewPrevio.requestFocus();

                // Eliminamos el nodo raiz de la escena
                rootMain.getChildren().remove(rootEditionView);

                // Hacemos visible la vista de la lista (el nodo de la lista)
                rootAgendaView.setVisible(true);
            }
            catch(RollbackException ex)
            {
                // Establecemos una alerta de tipo informativa
                Alert alert = new Alert(AlertType.INFORMATION);
                // Establecemos el texto de la alerta
                alert.setHeaderText("No se han podido guardar los cambios. " +
                            "Compruebe que los datos cumplen los requisitos");
                
                // Devolvemos el mensaje de la excepcion
                alert.setContentText(ex.getLocalizedMessage());
                // Mostramos la alerta y esperamos
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        
        // Con el metodo getScene() conseguimos la escena que tiene el nodo root, es decir, la escena activa de la ventana
        // y con el metodo getRoot() estamos recuperando el nodo raiz del contenedor principal de la palicacion, el StackPane, que es el activo en el scene
        StackPane rootMain = (StackPane) rootEditionView.getScene().getRoot();
        
        // Deshacemos la transaccion iniciada en setPersona
        entityManager.getTransaction().rollback();
        
        // Instanciamos el numero de fila seleccionada y le asignamos la seleccion que tenia el tableViewPrevio que le invoco
        int numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
        // Instanciamos la posicion en la tabla con el indice de la tabla seleccionada en la anterior vista
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
        
        
        // Eliminamos el nodo raiz de la escena
        rootMain.getChildren().remove(rootEditionView);
        
        // Hacemos visible la vista de la lista (el nodo de la lista)
        rootAgendaView.setVisible(true);
    }
    
    public void mostrarDatos()
    {
        // Mostramos los datos de la persona almacenados en la base de datos (accesibles a traves de los metodos get del EntityManager
        // Nombre
        textFieldNombre.setText(persona.getNombre());
        // Apellidos
        textFieldApellidos.setText(persona.getApellidos());
        // Telefono
        textFieldTelefono.setText(persona.getTelefono());
        // Email
        textFieldEmail.setText(persona.getEmail());
        
        
        
        // Numero de hijos
        if(persona.getNumHijos() != null)
        {
            // Establecemos el numero de hijos, como el metodo get nos devuelve un short, lo convertimos a cadena de texto
            textFieldNumHijos.setText(persona.getNumHijos().toString());
        }
        
        // Salario
        if(persona.getSalario() != null)
        {
            textFieldSalario.setText(persona.getSalario().toString());
        }
        
        // Jubilado
        if(persona.getJubilado() != null)
        {
            checkBoxJubilado.setSelected(persona.getJubilado());
        }
        
        // Estado civil
        if(persona.getEstadoCivil() != null)
        {
            switch(persona.getEstadoCivil())
            {
                case CASADO:
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO:
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO:
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }
        
        // Fecha de nacimiento
        if(persona.getFechaNacimiento() != null)
        {
            // Instanciamos una fecha que sera la fecha de nacimiento de la persona
            Date date = persona.getFechaNacimiento();
            // Instanciamos un instante que sera la fecha de nacimiento convertida a instante
            Instant instant = date.toInstant();
            // Instanciamos una ZonedDateTime que sera la fecha que contiene el instante en la zona que por defecto tiene establecida el sistema
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            // Se instancia un LocalDate a traves de la fecha con zona horaria
            LocalDate localDate = zdt.toLocalDate();
            // Se establece el LocalDate como el varol del selector de fecha de nacimiento
            datePickerFechaNacimiento.setValue(localDate);
        }
            
        // Provincia, objeto relacionado en la DB
        // Creamos una consulta para buscar todas las provincias
        Query queryProvinciaFindAll = entityManager.createNamedQuery("Provincia.findAll");
        // Creamos una lista para almacenar las provincias
        List listProvincia = queryProvinciaFindAll.getResultList();
        // Asignamos el valor del combo box
        comboBoxProvincia.setItems(FXCollections.observableArrayList(listProvincia));
        // Si la persona tiene provincia asignada, se establece la provincia
        if(persona.getProvincia() != null)
        {
            comboBoxProvincia.setValue(persona.getProvincia());
        }
            
        //Indicamos el String que queremos que aparezca COD-NOMBRE, es decir aprovechamos el metodo updateItem para reformatear la salida que tendria el objeto provincia
        // Para la visual en el combobox
        comboBoxProvincia.setCellFactory(
        (ListView<Provincia> provincia1) -> new ListCell<Provincia>(){
            @Override
            protected void updateItem(Provincia provincia, boolean empty){
                super.updateItem(provincia, empty);
                if(provincia1 == null || empty)
                {
                    setText("");
                }
                else
                {
                    setText(provincia.getCodigo() + "-" + provincia.getNombre());
                }
            }
        });
        
        // Esta es la forma en la que se vera el objeto con la lista del combobox cerrada
        comboBoxProvincia.setConverter(new StringConverter<Provincia>(){
            @Override
            public String toString(Provincia provincia) {
            
                if(provincia == null)
                {
                    return null;
                }
                else
                {
                    return provincia.getCodigo() + "-" + provincia.getNombre();
                }
            }

            @Override
            public Provincia fromString(String string) {
                return null;
            }
        });
        
        // Si la persona tiene foto
        if(persona.getFoto() != null)
        {
            // Creamos una cadena con el nombre de la foto
            String imageFileName = persona.getFoto();
            // Instanciamos un fichero con la ruta donde deberia encontrarse la imagen
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            // Si el fichero existe
            if(file.exists())
            {
                // Se instancia una imagen que sera una nueva imagen cargada desde el fichero
                Image image = new Image(file.toURI().toString());
                // Establecemos la imagen del ImageView con la imagen cargada
                imageViewFoto.setImage(image);
            }
            else
            {
                // Si no existe, mensaje de lerta (informativo)
                Alert alert = new Alert(AlertType.INFORMATION, "No se encuentra la imagen en " + file.toURI().toString());
                alert.showAndWait();
            }
        }
        
    }

    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
        // Abrimos el directorio de las fotos
        File carpetaFotos = new File(CARPETA_FOTOS);

        // Si no existe el fichero
        if (!carpetaFotos.exists())
        {
            // Lo crea
            carpetaFotos.mkdir();
        }

        // Instanciaos un filechooser para seleccionar imagen
        FileChooser fileChooser = new FileChooser();
        // Añadimos titulo al filechooser
        fileChooser.setTitle("Seleccionar imagen");
        // Filtramos por extension
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg", "*.png"), new FileChooser.ExtensionFilter("Todos los archivos","*.*")); 
        // Recogemos el fichero que nos devuelve el filechooser
        File file = fileChooser.showOpenDialog(rootEditionView.getScene().getWindow());
        
        // Si no es null
        if (file != null)
        {
            try 
            {
                // Copia la ruta
                Files.copy(file.toPath(),new File(CARPETA_FOTOS+ "/"+file.getName()).toPath());

                // Se la estalece a la persona
                persona.setFoto(file.getName());
                // Creamos la imagen
                Image image = new Image(file.toURI().toString());
                // Establecemos la imagen para verla
                imageViewFoto.setImage(image);
            } 
            catch(FileAlreadyExistsException ex)
            {
                Alert alert = new Alert(AlertType.WARNING,"Nombre de archivo duplicado");
                alert.showAndWait();
            } 
            catch(IOException ex)
            {
                Alert alert = new Alert(AlertType.WARNING,"No se ha podido guardar la imagen");
                alert.showAndWait();
            }
}
    }

    @FXML
    private void onActionSuprimirFoto(ActionEvent event) {
        // Creamos alert para avisar de la eliminacion
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen,\n"+ "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija la opción deseada:");
        
        // Creamos tres botones para mostrar en el alert
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        
        // Añadimos los botones a la alerta
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener, buttonTypeCancel);
        
        // Esperamos que el usuario clicke un boton
        Optional<ButtonType> result = alert.showAndWait();

        // Si el boton es el de confimacion de la eliminacion, elimina la persona y la foto
        if (result.get() == buttonTypeEliminar)
        {
            // Recogemos la foto de la persona
            String imageFileName = persona.getFoto();
            // La abrimos como un fichero
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);

            // Si existe
            if (file.exists()) 
            {
                // La eliminamos
                file.delete();
            }
            // Establecemos el campo foto de persona a null
            persona.setFoto(null);
            // Y quitamos la imagen del image viewer
            imageViewFoto.setImage(null);
        } 
        // Si el boton ha sido mantener, le quita la foto a la persona pero no la elimina
        else if(result.get() == buttonTypeMantener) 
        {
            // Establecemos la foto de la persona y el image viewer a null
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }
    }
}
