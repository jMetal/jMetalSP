package org.uma.jmetalsp.problem.fda;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.khaos.perception.core.Observable;

import java.io.Serializable;

/**
 * Cristóbal Barba <cbarba@lcc.uma.es>
 */
public abstract class FDA extends AbstractDoubleProblem implements DynamicProblem<DoubleSolution, FDAUpdateData>, Serializable {
	protected double time;
	protected boolean theProblemHasBeenModified;
	protected Observable<FDAUpdateData> observer ;

	public FDA (Observable<FDAUpdateData> observable) {
		this.observer = observable ;
		observable.register(this);
	}

	@Override
	public void update(Observable<FDAUpdateData> observable, FDAUpdateData fdaUpdateData) {
		time=fdaUpdateData.getTime();
		if(time==0.0){
			time=1.0;
		}

		theProblemHasBeenModified = true ;
	}

}
