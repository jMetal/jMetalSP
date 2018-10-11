package org.uma.jmetalsp.observeddata;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.ObservedData;

import java.util.ArrayList;
import java.util.List;

/**
 * Data returned by algorithms to be sent to observers, in the form of a map structure
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ObservedDoubleSolutionList implements ObservedData<List<ObservedDoubleSolution>> {
	private List<ObservedDoubleSolution> solutionList;

	public ObservedDoubleSolutionList() {

  }

	public ObservedDoubleSolutionList(List<DoubleSolution> solutionList) {
		this.solutionList = new ArrayList<>();

    for (DoubleSolution solution:solutionList) {
     this.solutionList.add(new ObservedDoubleSolution(solution));
    }
	}

  @Override
  public String toJson() {
    return JsonWriter.objectToJson(this) ;
  }

  @Override
  public ObservedDoubleSolutionList fromJson(String jsonString) {
    return (ObservedDoubleSolutionList)JsonReader.jsonToJava(jsonString);
  }



    @Override
    public List<ObservedDoubleSolution> getData() {
        return this.solutionList;
    }
}
