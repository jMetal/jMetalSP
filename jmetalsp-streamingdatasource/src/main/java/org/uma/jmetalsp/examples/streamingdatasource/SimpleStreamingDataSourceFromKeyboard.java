package org.uma.jmetalsp.examples.streamingdatasource;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This class reads double values from the keyboard and returns them as a list
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleStreamingDataSourceFromKeyboard implements
        StreamingDataSource<ObservedValue<List<Double>>> {
  private Observable<ObservedValue<List<Double>>> observable;

  /**
   * @param observable
   */
  public SimpleStreamingDataSourceFromKeyboard(Observable<ObservedValue<List<Double>>> observable) {
    this.observable = observable;
  }

  public SimpleStreamingDataSourceFromKeyboard() {
    this(new DefaultObservable<>());
  }


  @Override
  public void run() {
    Scanner scanner = new Scanner(System.in);

    double v1 ;
    double v2 ;

    while (true) {
      System.out.println("Introduce the new reference point(between commas):");
      String s = scanner.nextLine() ;
      Scanner sl= new Scanner(s);
      sl.useDelimiter(",");
      try {
        v1 = Double.parseDouble(sl.next());
        v2 = Double.parseDouble(sl.next());
      }catch (Exception e){//any problem
        v1=0;
        v2=0;
      }
      System.out.println("REF POINT: " + v1 + ", " + v2) ;

      observable.setChanged();
      List<Double> values = Arrays.asList(v1, v2) ;
      observable.notifyObservers(new ObservedValue<>(values));
    }
  }

  @Override
  public Observable<ObservedValue<List<Double>>> getObservable() {
    return observable;
  }
}
