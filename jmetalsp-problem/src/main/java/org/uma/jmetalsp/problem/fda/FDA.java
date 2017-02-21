package org.uma.jmetalsp.problem.fda;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.perception.Observable;
import org.uma.jmetalsp.updatedata.TimeUpdateData;

import java.io.Serializable;

/**
 * Cristóbal Barba <cbarba@lcc.uma.es>
 */
public abstract class FDA extends AbstractDoubleProblem implements DynamicProblem<DoubleSolution, TimeUpdateData>, Serializable {
	protected double time;
	protected boolean theProblemHasBeenModified;
	protected Observable<TimeUpdateData> observable ;

	public FDA (Observable<TimeUpdateData> observable) {
		this.observable = observable ;
		observable.register(this);
	}

	@Override
	public void update(Observable<?> observable, Object data) {
		System.out.println("Update on FDA invoked") ;
		time=((TimeUpdateData)data).getTime();
		if(time==0.0){
			time=1.0;
		}

		theProblemHasBeenModified = true ;
	}
}
