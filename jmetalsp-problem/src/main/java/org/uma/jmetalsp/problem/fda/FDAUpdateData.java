package org.uma.jmetalsp.problem.fda;


import java.io.Serializable;
import java.util.logging.Logger;

import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class FDAUpdateData implements UpdateData, Serializable {
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
  public double getTime() {
    return time;
  }
}