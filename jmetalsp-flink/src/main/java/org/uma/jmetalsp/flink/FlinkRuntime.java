package org.uma.jmetalsp.flink;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.StreamingRuntime;

import java.util.List;

public class FlinkRuntime implements StreamingRuntime {
    private StreamExecutionEnvironment environment;
    public FlinkRuntime() {
        environment = StreamExecutionEnvironment.getExecutionEnvironment();
    }
    @Override
    public void startStreamingDataSources(List<StreamingDataSource<?>> streamingDataSourceList) {
        for (StreamingDataSource<?> streamingDataSource : streamingDataSourceList) {
            ((FlinkStreamingDataSource<?>)streamingDataSource).setExecutionEnvironment(environment);
            streamingDataSource.run();
        }

        try {
            environment.execute("FlinkRuntime");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
