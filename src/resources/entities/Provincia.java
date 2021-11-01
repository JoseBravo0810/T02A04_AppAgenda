/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author jose
 */
// Clase entidad
@Entity
// Tabla en la DB donde se almacenan los datos de provincia1
@Table(name = "PROVINCIA")
// Consultas predefinidas
@NamedQueries({
    @NamedQuery(name = "Provincia.findAll", query = "SELECT p FROM Provincia p"),
    @NamedQuery(name = "Provincia.findById", query = "SELECT p FROM Provincia p WHERE p.id = :id"),
    @NamedQuery(name = "Provincia.findByCodigo", query = "SELECT p FROM Provincia p WHERE p.codigo = :codigo"),
    @NamedQuery(name = "Provincia.findByNombre", query = "SELECT p FROM Provincia p WHERE p.nombre = :nombre")})

// Clase Provincia (serializable, se puede volcar a fichero, o, como en este caso, en una DB)
public class Provincia implements Serializable {

    // Atributos de provincia
    private static final long serialVersionUID = 1L;
    @Id
    // El id es autogenerado, y se sigue como estrategia que este atributo sea la identidad del objeto (no habra dos con el mismo ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // No admite valores nulos
    @Basic(optional = false)
    // La columna a la que hace referencia en la DB es ID.
    @Column(name = "ID")
    // Declaracion de a variable
    private Integer id;
    @Column(name = "CODIGO")
    private String codigo;
    @Basic(optional = false)
    @Column(name = "NOMBRE")
    private String nombre;
    // Uno a muchos (1:n), una provincia1 puede tener muchas personas, el tratamiento a aplicar si se elimina
    // una provincia es en cascada, es decir, que al eliminarse una provincia, se eliminaran todas las personas
    // de dicha provincia1 para no incumplir la restriccion de clave foranea
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "provincia")
        // Una coleccion con todas las personas que pertenecen a una provincia
    private Collection<Persona> personaCollection;

    /*
        Constructores
    */
    
    public Provincia() {
    }

    public Provincia(Integer id) {
        this.id = id;
    }

    public Provincia(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    /*
        Getters y Setters
    */
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Collection<Persona> getPersonaCollection() {
        return personaCollection;
    }

    public void setPersonaCollection(Collection<Persona> personaCollection) {
        this.personaCollection = personaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    // Metodo para comparar provincias
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Provincia)) {
            return false;
        }
        Provincia other = (Provincia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    // Metodo toString() de provincias
    @Override
    public String toString() {
        return "resources.entities.Provincia[ id=" + id + " ]";
    }
    
}
