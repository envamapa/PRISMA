package mx.prisma.generadorPruebas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import mx.prisma.editor.model.CasoUso;

@Entity
@Table(name = "ConfiguracionTrayectorias", catalog = "PRISMA")
public class ConfiguracionTrayectorias implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String condicion;
	private CasoUso casoUso;

	public ConfiguracionTrayectorias() {
	}

	public ConfiguracionTrayectorias(String condicion, CasoUso casoUso) {
		this.condicion = condicion;
		this.casoUso = casoUso;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "condicion", nullable = false, length = 500)
	public String getCondicion() {
		return this.condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CasoUsoElementoid", referencedColumnName ="Elementoid", nullable = false)
	public CasoUso getCasoUso() {
		return this.casoUso;
	}

	public void setCasoUso(CasoUso casoUso) {
		this.casoUso = casoUso;
	}

}
