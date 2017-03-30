package org.uma.jmetalsp.problem.fda;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.perception.Observable;

import java.io.Serializable;

/**
 * Cristóbal Barba <cbarba@lcc.uma.es>
 */
public abstract class FDA
				extends AbstractDoubleProblem
				implements DynamicProblem<DoubleSolution, SingleObservedData<Integer>>, Serializable {
	protected double time;
	protected boolean theProblemHasBeenModified;
	protected Observable<SingleObservedData<Integer>> observable ;

  private int tauT=5;
  private int nT=10;

	public FDA (Observable<SingleObservedData<Integer>> observable) {
		this.observable = observable ;
		observable.register(this);
	}

	@Override
	public void update(Observable<SingleObservedData<Integer>> observable, SingleObservedData<Integer> counter) {
		System.out.println("Update on FDA invoked") ;

    time = (1.0d/(double)nT) * Math.floor(counter.getValue()/(double)tauT) ;

		if(time==0.0){
			time=1.0;
		}

		theProblemHasBeenModified = true ;
	}
}
