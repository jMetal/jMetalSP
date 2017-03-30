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
				implements DynamicProblem<DoubleSolution, SingleObservedData<Double>>, Serializable {
	protected double time;
	protected boolean theProblemHasBeenModified;
	protected Observable<SingleObservedData<Double>> observable ;

	public FDA (Observable<SingleObservedData<Double>> observable) {
		this.observable = observable ;
		observable.register(this);
	}

	@Override
	public void update(Observable<SingleObservedData<Double>> observable, SingleObservedData<Double> data) {
		System.out.println("Update on FDA invoked") ;
		time=((SingleObservedData<Double>)data).getValue();
		if(time==0.0){
			time=1.0;
		}

		theProblemHasBeenModified = true ;
	}
}
