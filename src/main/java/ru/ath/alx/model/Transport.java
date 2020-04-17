package ru.ath.alx.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "transport", schema = "restproxdb")
public class Transport {
    private int id;
    private String wlnid;
    private String wlnnm;
    private String vehicletype;
    private String grossvehicleweight;
    private String registrationplate;
    private String vin;
    private String brand;
    private String model;
    private String prodyear;
    private String color;
    private String enginemodel;
    private String primaryfueltype;
    private String enginepower;
    private String atinvnom;
    private String atinstalldate;
    private String atwheelformula;
    private String atdepartment;
    private String atautocol;
    private String atlocation;
    private String atbase;
    private String atres;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "wlnid", nullable = false, length = 10)
    public String getWlnid() {
        return wlnid;
    }

    public void setWlnid(String wlnid) {
        this.wlnid = wlnid;
    }

    @Basic
    @Column(name = "wlnnm", nullable = false, length = 100)
    public String getWlnnm() {
        return wlnnm;
    }

    public void setWlnnm(String wlnnm) {
        this.wlnnm = wlnnm;
    }

    @Basic
    @Column(name = "vehicletype", nullable = false, length = 100)
    public String getVehicletype() {
        return vehicletype;
    }

    public void setVehicletype(String vehicletype) {
        this.vehicletype = vehicletype;
    }

    @Basic
    @Column(name = "grossvehicleweight", nullable = false, length = 100)
    public String getGrossvehicleweight() {
        return grossvehicleweight;
    }

    public void setGrossvehicleweight(String grossvehicleweight) {
        this.grossvehicleweight = grossvehicleweight;
    }

    @Basic
    @Column(name = "registrationplate", nullable = false, length = 100)
    public String getRegistrationplate() {
        return registrationplate;
    }

    public void setRegistrationplate(String registrationplate) {
        this.registrationplate = registrationplate;
    }

    @Basic
    @Column(name = "vin", nullable = false, length = 100)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Basic
    @Column(name = "brand", nullable = false, length = 100)
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Basic
    @Column(name = "model", nullable = false, length = 100)
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Basic
    @Column(name = "prodyear", nullable = false, length = 100)
    public String getProdyear() {
        return prodyear;
    }

    public void setProdyear(String prodyear) {
        this.prodyear = prodyear;
    }

    @Basic
    @Column(name = "color", nullable = false, length = 100)
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Basic
    @Column(name = "enginemodel", nullable = false, length = 100)
    public String getEnginemodel() {
        return enginemodel;
    }

    public void setEnginemodel(String enginemodel) {
        this.enginemodel = enginemodel;
    }

    @Basic
    @Column(name = "primaryfueltype", nullable = false, length = 100)
    public String getPrimaryfueltype() {
        return primaryfueltype;
    }

    public void setPrimaryfueltype(String primaryfueltype) {
        this.primaryfueltype = primaryfueltype;
    }

    @Basic
    @Column(name = "enginepower", nullable = false, length = 100)
    public String getEnginepower() {
        return enginepower;
    }

    public void setEnginepower(String enginepower) {
        this.enginepower = enginepower;
    }

    @Basic
    @Column(name = "atinvnom", nullable = false, length = 10)
    public String getAtinvnom() {
        return atinvnom;
    }

    public void setAtinvnom(String atinvnom) {
        this.atinvnom = atinvnom;
    }

    @Basic
    @Column(name = "atinstalldate", nullable = false, length = 100)
    public String getAtinstalldate() {
        return atinstalldate;
    }

    public void setAtinstalldate(String atinstalldate) {
        this.atinstalldate = atinstalldate;
    }

    @Basic
    @Column(name = "atwheelformula", nullable = false, length = 100)
    public String getAtwheelformula() {
        return atwheelformula;
    }

    public void setAtwheelformula(String atwheelformula) {
        this.atwheelformula = atwheelformula;
    }

    @Basic
    @Column(name = "atdepartment", nullable = false, length = 100)
    public String getAtdepartment() {
        return atdepartment;
    }

    public void setAtdepartment(String atdepartment) {
        this.atdepartment = atdepartment;
    }

    @Basic
    @Column(name = "atautocol", nullable = false, length = 100)
    public String getAtautocol() {
        return atautocol;
    }

    public void setAtautocol(String atautocol) {
        this.atautocol = atautocol;
    }

    @Basic
    @Column(name = "atlocation", nullable = false, length = 100)
    public String getAtlocation() {
        return atlocation;
    }

    public void setAtlocation(String atlocation) {
        this.atlocation = atlocation;
    }

    @Basic
    @Column(name = "atbase", nullable = false, length = 100)
    public String getAtbase() {
        return atbase;
    }

    public void setAtbase(String atbase) {
        this.atbase = atbase;
    }

    @Basic
    @Column(name = "atres", nullable = false, length = 100)
    public String getAtres() {
        return atres;
    }

    public void setAtres(String atres) {
        this.atres = atres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transport that = (Transport) o;
        return id == that.id &&
                Objects.equals(wlnid, that.wlnid) &&
                Objects.equals(wlnnm, that.wlnnm) &&
                Objects.equals(vehicletype, that.vehicletype) &&
                Objects.equals(grossvehicleweight, that.grossvehicleweight) &&
                Objects.equals(registrationplate, that.registrationplate) &&
                Objects.equals(vin, that.vin) &&
                Objects.equals(brand, that.brand) &&
                Objects.equals(model, that.model) &&
                Objects.equals(prodyear, that.prodyear) &&
                Objects.equals(color, that.color) &&
                Objects.equals(enginemodel, that.enginemodel) &&
                Objects.equals(primaryfueltype, that.primaryfueltype) &&
                Objects.equals(enginepower, that.enginepower) &&
                Objects.equals(atinvnom, that.atinvnom) &&
                Objects.equals(atinstalldate, that.atinstalldate) &&
                Objects.equals(atwheelformula, that.atwheelformula) &&
                Objects.equals(atdepartment, that.atdepartment) &&
                Objects.equals(atautocol, that.atautocol) &&
                Objects.equals(atlocation, that.atlocation) &&
                Objects.equals(atbase, that.atbase) &&
                Objects.equals(atres, that.atres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wlnid, wlnnm, vehicletype, grossvehicleweight, registrationplate, vin, brand, model, prodyear, color, enginemodel, primaryfueltype, enginepower, atinvnom, atinstalldate, atwheelformula, atdepartment, atautocol, atlocation, atbase, atres);
    }
}
