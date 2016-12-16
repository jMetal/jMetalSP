package org.uma.jmetalsp.problem.fda;


import java.io.Serializable;
import java.util.logging.Logger;

import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class FDAUpdateData implements UpdateData, Serializable {
  private double time=1.0d;
  public FDAUpdateData(int n){
    int tauT=5;
    int nT=10;
    time= (1.0d/(double)nT) * Math.floor((double)n/(double)tauT);
  }
  public double getTime() {
    return time;
  }
}
