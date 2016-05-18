package mx.prisma.editor.model;

/*
 * Sergio Ramírez Camacho 17/06/2015
 */

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ValidatorType;


@Entity
@Table(name = "Trayectoria", catalog = "PRISMA", uniqueConstraints = @UniqueConstraint(columnNames = {
		"clave", "CasoUsoElementoid" }))
public class Trayectoria implements java.io.Serializable, Comparable<Trayectoria> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String clave;
	private boolean alternativa;
	private String condicion;
	private CasoUso casoUso;
	private boolean finCasoUso;
	private Set<Paso> pasos = new HashSet<Paso>(0);
	

	public Trayectoria() {
	}

	public Trayectoria(String clave, boolean alternativa, CasoUso casoUso,
			boolean finCasoUso) {
		this.clave = clave;
		this.alternativa = alternativa;
		this.casoUso = casoUso;
		this.finCasoUso = finCasoUso;
	}

	public Trayectoria(String clave, boolean alternativa, String condicion,
			CasoUso casoUso, boolean finCasoUso) {
		this.clave = clave;
		this.alternativa = alternativa;
		this.condicion = condicion;
		this.casoUso = casoUso;
		this.finCasoUso = finCasoUso;
	}

	@RequiredFieldValidator(type = ValidatorType.FIELD, message = "%{getText('MSG4')}", shortCircuit = true)
	@RegexFieldValidator(type = ValidatorType.FIELD, message = "%{getText('MSG5',{'un', 'número'})}", regex = "[0-9]*", shortCircuit = true)
	@IntRangeFieldValidator(message = "%{getText('MSG14',{'El', 'identificador', '0', '2147483647'})}", shortCircuit = true, min = "0", max = "2147483647")//Pendiente 4294967295
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@RequiredStringValidator(type = ValidatorType.FIELD, message = "%{getText('MSG4')}", shortCircuit= true)
	@StringLengthFieldValidator(message = "%{getText('MSG6',{'5', 'caracteres'})}", trim = true, maxLength = "5", shortCircuit= true)
	@Column(name = "clave", nullable = false)
	public String getClave() {
		return this.clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	@Column(name = "alternativa", nullable = false)
	public boolean isAlternativa() {
		return this.alternativa;
	}

	public void setAlternativa(boolean alternativa) {
		this.alternativa = alternativa;
	}

	
	@Column(name = "condicion")
	public String getCondicion() {
		return this.condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CasoUsoElementoid", referencedColumnName = "Elementoid")
	public CasoUso getCasoUso() {
		return casoUso;
	}

	public void setCasoUso(CasoUso casoUso) {
		this.casoUso = casoUso;
	}

	@Column(name = "finCasoUso", nullable = false)
	public boolean isFinCasoUso() {
		return this.finCasoUso;
	}

	public void setFinCasoUso(boolean finCasoUso) {
		this.finCasoUso = finCasoUso;
	}
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "trayectoria", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("numero")
	public Set<Paso> getPasos() {
		return pasos;
	}

	public void setPasos(Set<Paso> pasos) {
		this.pasos = pasos;
	}

	public int compareTo(Trayectoria o) {
		return this.clave.compareTo(o.getClave());
	}

}
