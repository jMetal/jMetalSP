package  org.uma.jmetalsp.application.fda.problem;


import java.io.Serializable;
import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class FDAUpdateData implements UpdateData, Serializable {
  private double time=1.0d;
  public FDAUpdateData(int n){
    int tauT=5;
    int nT=10;
    time= (double)((double)1.0/nT * Math.ceil(n/tauT));
  }
  public double getTime() {
    return time;
  }
}
