package org.uma.jmetalsp.streamingdatasource.repository;

import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.updatedata.repository.TimeUpdateData;

/**
 * Created by khaosdev on 2/8/17.
 */
public class StreamingTimeDataSource implements StreamingDataSource<TimeUpdateData> {
	private DynamicProblem<?, TimeUpdateData> problem ;
	private double timeInterval;

	@Override
	public void setProblem(DynamicProblem<?, TimeUpdateData> problem) {
		this.problem = problem ;
		timeInterval = 1d ;
	}

	/**
	 * Interval in milliseconds
	 * @param interval
	 */
	public void setTimeInterval(double interval) {
		this.timeInterval = interval ;
	}

	@Override
	public void start() {

	}
}
