package org.uma.jmetalsp.updatedata.impl;

import org.uma.jmetalsp.updatedata.MatrixUpdateData;
import org.uma.jmetalsp.updatedata.TimeUpdateData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultTimeUpdateData implements TimeUpdateData{
  private double time ;

  public DefaultTimeUpdateData(double time) {
    this.time = time ;
  }

  @Override
  public double getTime() {
    return time;
  }
}
