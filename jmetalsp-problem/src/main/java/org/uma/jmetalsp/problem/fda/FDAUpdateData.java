package org.uma.jmetalsp.problem.fda;


import org.uma.jmetalsp.updatedata.TimeUpdateData;

import java.io.Serializable;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class FDAUpdateData implements TimeUpdateData, Serializable {
  private double time=1.0d;
  private int tauT=5;
  private int nT=10;

  public FDAUpdateData(int n){
    time= (1.0d/(double)nT) * Math.floor((double)n/(double)tauT);
  }

  public FDAUpdateData(int tauT,int nT,int n){
    this.tauT=tauT;
    this.nT=nT;
    time= (1.0d/(double)nT) * Math.floor((double)n/(double)tauT);
  }

  @Override
  public double getTime() {
    return time;
  }

  @Override
  public double getTimeInterval() {
    // TODO
    return 0;
  }
}