package org.uma.jmetalsp.examples.dynamicnsgaii;

import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingFDAUpdateData implements StreamingDataSource<FDAUpdateData> {
	private DynamicProblem<?, FDAUpdateData> problem ;

	public StreamingFDAUpdateData(DynamicProblem<?, FDAUpdateData> problem) {
		this.problem = problem ;
	}

	@Override
	public void start() {
		int counter = 0 ;
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			problem.update(new FDAUpdateData(counter));
			counter ++ ;
		}

	}
}
