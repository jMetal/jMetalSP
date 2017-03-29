package org.uma.jmetalsp.updatedata.impl;

import org.uma.jmetalsp.updatedata.TimeObservedData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultTimeObservedData implements TimeObservedData {
  private double time ;

  public DefaultTimeObservedData(double time) {
    this.time = time ;
  }

  @Override
  public double getTime() {
    return time;
  }
}
