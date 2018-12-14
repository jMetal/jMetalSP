package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;

/**
 * Cristóbal Barba <cbarba@lcc.uma.es>
 */
public abstract class DF
				extends AbstractDoubleProblem
				implements DynamicProblem<DoubleSolution, ObservedValue<Integer>>, Serializable {
	protected double time;
	protected boolean theProblemHasBeenModified;
	protected Observable<ObservedValue<Integer>> observable ;
	private int T0=50;
  private int tauT=10;
  private int nT=10;

	public DF(Observable<ObservedValue<Integer>> observable) {
		this.observable = observable ;
		this.time = 1.0;
		observable.register(this);
	}

	public DF() {
		this(new DefaultObservable<>()) ;
	}

	@Override
	public void update(Observable<ObservedValue<Integer>> observable, ObservedValue<Integer> counter) {
		double tauTmp = Math.max((double)counter.getValue()+tauT-(T0+1),0.0);
        time = (1.0d/(double)nT) * Math.floor(tauTmp/(double)tauT) ;
		theProblemHasBeenModified = true ;
	}

	public double helperSum(DoubleSolution solution, int start, int end, double y){
		double result=0;
		for (int i=start;i<end;i++){
			result+=Math.pow(solution.getVariableValue(i)-y,2);
		}
		return result;
	}


}
