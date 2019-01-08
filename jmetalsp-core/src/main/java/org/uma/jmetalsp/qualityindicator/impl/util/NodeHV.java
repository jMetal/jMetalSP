package org.uma.jmetalsp.qualityindicator.impl.util;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.uma.jmetal.solution.Solution;


public class NodeHV <S extends Solution<?>>  implements Serializable {

  private boolean ignore;
  private List<Double> area;
  private List<Double> volumen;
  private S cargo;
  private LinkedList<S> list;
  private int dimensions;


  public NodeHV(S cargo) {
    this.dimensions = cargo.getNumberOfObjectives();
    this.ignore = false;
    this.area = new ArrayList<>(dimensions);
    this.volumen = new ArrayList<>(dimensions);
    this.list = new LinkedList<>();
    this.cargo = cargo;
    this.list.add(cargo);
  }

  public boolean isIgnore() {
    return ignore;
  }

  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  public List<Double> getArea() {
    return area;
  }

  public void setArea(List<Double> area) {
    this.area = area;
  }

  public List<Double> getVolumen() {
    return volumen;
  }

  public void setVolumen(List<Double> volumen) {
    this.volumen = volumen;
  }

  public S getCargo() {
    return cargo;
  }

  public void setCargo(S cargo) {
    this.cargo = cargo;
  }

  public LinkedList<S> getList() {
    return list;
  }

  public void setList(LinkedList<S> list) {
    this.list = list;
  }

  public int getDimensions() {
    return dimensions;
  }

  public void setDimensions(int dimensions) {
    this.dimensions = dimensions;
  }
}
