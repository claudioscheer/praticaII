/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.gdt.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "csbff_tipo_beneficio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CsbffTipoBeneficio.findAll", query = "SELECT c FROM CsbffTipoBeneficio c")})
public class CsbffTipoBeneficio implements Serializable {
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "tipo_beneficio_codigo")
    private BigDecimal tipoBeneficioCodigo;
    @Basic(optional = false)
    @Column(name = "tipo_beneficio_nome")
    private String tipoBeneficioNome;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoBeneficioCodigo")
    private List<CsbffBeneficios> csbffBeneficiosList;

    public CsbffTipoBeneficio() {
    }

    public CsbffTipoBeneficio(BigDecimal tipoBeneficioCodigo) {
        this.tipoBeneficioCodigo = tipoBeneficioCodigo;
    }

    public CsbffTipoBeneficio(BigDecimal tipoBeneficioCodigo, String tipoBeneficioNome) {
        this.tipoBeneficioCodigo = tipoBeneficioCodigo;
        this.tipoBeneficioNome = tipoBeneficioNome;
    }

    public BigDecimal getTipoBeneficioCodigo() {
        return tipoBeneficioCodigo;
    }

    public void setTipoBeneficioCodigo(BigDecimal tipoBeneficioCodigo) {
        this.tipoBeneficioCodigo = tipoBeneficioCodigo;
    }

    public String getTipoBeneficioNome() {
        return tipoBeneficioNome;
    }

    public void setTipoBeneficioNome(String tipoBeneficioNome) {
        this.tipoBeneficioNome = tipoBeneficioNome;
    }

    @XmlTransient
    @JsonIgnore
    public List<CsbffBeneficios> getCsbffBeneficiosList() {
        return csbffBeneficiosList;
    }

    public void setCsbffBeneficiosList(List<CsbffBeneficios> csbffBeneficiosList) {
        this.csbffBeneficiosList = csbffBeneficiosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tipoBeneficioCodigo != null ? tipoBeneficioCodigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CsbffTipoBeneficio)) {
            return false;
        }
        CsbffTipoBeneficio other = (CsbffTipoBeneficio) object;
        if ((this.tipoBeneficioCodigo == null && other.tipoBeneficioCodigo != null) || (this.tipoBeneficioCodigo != null && !this.tipoBeneficioCodigo.equals(other.tipoBeneficioCodigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.org.gdt.modelNew.CsbffTipoBeneficio[ tipoBeneficioCodigo=" + tipoBeneficioCodigo + " ]";
    }
    
}