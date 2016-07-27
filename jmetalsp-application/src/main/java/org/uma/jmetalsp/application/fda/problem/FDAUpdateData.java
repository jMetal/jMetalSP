package  org.uma.jmetalsp.application.fda.problem;


import java.io.Serializable;
import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * Created by cris on 19/07/2016.
 */
public class FDAUpdateData implements UpdateData, Serializable {
  private double time;
  public  FDAUpdateData(int n){
    int tauT=5;
    int nT=10;
    time= 1/nT * Math.ceil(n/tauT);
  }
  public double getTime() {
    return time;
  }
}
