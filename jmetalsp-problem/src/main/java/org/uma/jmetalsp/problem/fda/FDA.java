package org.uma.jmetalsp.problem.fda;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.ObservedIntegerValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;

/**
 * Cristóbal Barba <cbarba@lcc.uma.es>
 */
public abstract class FDA
				extends AbstractDoubleProblem
				implements DynamicProblem<DoubleSolution, ObservedIntegerValue>, Serializable {
	protected double time;
	protected boolean theProblemHasBeenModified;
	protected Observable<ObservedIntegerValue> observable ;

  private int tauT=5;
  private int nT=10;

	public FDA (Observable<ObservedIntegerValue> observable) {
		this.observable = observable ;
		observable.register(this);
	}

	public FDA () {
		this(new DefaultObservable<>()) ;
	}

	@Override
	public void update(Observable<ObservedIntegerValue> observable, ObservedIntegerValue counter) {
    time = (1.0d/(double)nT) * Math.floor(counter.getValue()/(double)tauT) ;

		if(time==0.0){
			time=1.0;
		}

		theProblemHasBeenModified = true ;
	}


}
