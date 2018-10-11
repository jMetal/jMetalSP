package org.uma.jmetalsp.problem.tsp;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.File;

/**
 * Class representing the value of a TSP distance/time/cost matrix
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TSPMatrixData extends SpecificRecordBase implements SpecificRecord {
  private int x ;
  private int y ;
  private double value ;
  Object matrixIdentifier ;

  public TSPMatrixData(){

  }
  public TSPMatrixData(Object id, int x, int y, double value) {
    this.matrixIdentifier = id ;
    this.x = x ;
    this.y = y ;
    this.value = value ;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public double getValue() {
    return value;
  }

  public Object getMatrixIdentifier() {
    return matrixIdentifier;
  }

  @Override
  public Schema getSchema() {
    File file = new File("avsc/TSPMatrixData.avsc");
    Schema schema = null;
    try{
      schema=new Schema.Parser().parse(file);
    }catch (Exception ex){

    }
    return schema;
  }

  @Override
  public Object get(int i) {
    Object result=null;
    switch (i){
      case 0: result = matrixIdentifier; break;
      case 1: result = x; break;
      case 2: result = y; break;
      case 3: result = value; break;

    }
    return result;
  }

  @Override
  public void put(int i, Object o) {
    switch (i){
      case 0: matrixIdentifier=o; break;
      case 1: x=(int)o; break;
      case 2: y=(int)o; break;
      case 3: value=(double)o; break;

    }

  }

  @Override
  public String toString() {
    return matrixIdentifier+ ","+x+","+y+","+value;
  }
}
