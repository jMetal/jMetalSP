package  org.uma.jmetalsp.application.fda.problem;


import java.io.Serializable;
import java.util.logging.Logger;

import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * Created by cris on 19/07/2016.
 */
public class FDAUpdateData implements UpdateData, Serializable {
  private double time=1.0d;
  public FDAUpdateData(int n){
    Logger.getGlobal().info("FDA1---FDAUpdateData ---------n contador--------> "+n);
    int tauT=5;
    int nT=10;
    time= (double)((double)1.0/nT * Math.ceil(n/tauT));
    Logger.getGlobal().info("FDA1---FDAUpdateData ----------time-------> "+time);
  }
  public double getTime() {
    return time;
  }
}
