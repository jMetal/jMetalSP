package org.uma.jmetalsp.examples.streamingdatasource;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.ListObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class reads double values from the keyboard and returns them as a list
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleStreamingDataSourceFromKeyboard implements
				StreamingDataSource<ListObservedData<Double>,
								Observable<ListObservedData<Double>>> {
	private Observable<ListObservedData<Double>> observable;

	/**
   *
   * @param observable
   */
	public SimpleStreamingDataSourceFromKeyboard(Observable<ListObservedData<Double>> observable) {
		this.observable = observable ;
	}

	@Override
	public void run() {
		while (true) {
		  System.out.println("Introduce the new reference point:") ;

      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)) ;
      String values ;

      try {
        values = reader.readLine() ;
        List<String> doubleValuesAsStrings = Arrays.asList(values.split(" ")) ;

        System.out.println("RRRRREEADDDD: -" + values + "- ") ;

        for (String v  : doubleValuesAsStrings) {
          System.out.println(" - " + v) ;
        }
        /*
        List<Double> doubleValues = doubleValuesAsStrings
                .stream()
                .map(value -> Double.parseDouble(value))
                .collect(Collectors.toList());

        System.out.println("RRRRREEADDDD") ;
        doubleValues
                .stream()
                .forEach(System.out::println);

*/
        //observable.setChanged();
        //observable.notifyObservers(new ListObservedData<>(doubleValues));

      } catch (IOException e) {
        e.printStackTrace();
      }
		}
	}
}
