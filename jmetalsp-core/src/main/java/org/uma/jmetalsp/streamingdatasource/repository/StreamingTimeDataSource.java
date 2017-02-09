package org.uma.jmetalsp.streamingdatasource.repository;

import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.updatedata.repository.TimeUpdateData;

/**
 * Created by khaosdev on 2/8/17.
 */
public interface StreamingTimeDataSource extends StreamingDataSource<TimeUpdateData> {

	/**
	 * Interval in milliseconds
	 */
	public double getTimeInterval() ;

}
