package org.uma.jmetalsp.examples.streamingdatasource;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.ListObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * This class reads double values from the keyboard and returns them as a list
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleStreamingDataSourceFromKeyboard implements
        StreamingDataSource<ListObservedData<Double>,
                Observable<ListObservedData<Double>>> {
  private Observable<ListObservedData<Double>> observable;

  /**
   * @param observable
   */
  public SimpleStreamingDataSourceFromKeyboard(Observable<ListObservedData<Double>> observable) {
    this.observable = observable;
  }

  @Override
  public void run() {
    Scanner scanner = new Scanner(System.in);
    double v1 = 0.2 ;
    double v2 = 0.2 ;

    while (true) {
      System.out.println("Introduce the new reference point:");

      String s = scanner.nextLine() ;

      System.out.println("REF POINT: " + v1 + ", " + v2) ;

      observable.setChanged();
      List<Double> values = Arrays.asList(v1, v2) ;
      observable.notifyObservers(new ListObservedData<>(values));

      System.out.println("Introduce the new reference point 2:");

      s = scanner.nextLine() ;

      v1 = 0.3 ;
      v2 = 0.6 ;

      System.out.println("REF POINT 2: " + v1 + ", " + v2) ;

      observable.setChanged();
      values = Arrays.asList(v1, v2) ;
      observable.notifyObservers(new ListObservedData<>(values));

      System.out.println("Introduce the new reference point 3:");

      s = scanner.nextLine() ;

      v1 = 0.4 ;
      v2 = 0.4 ;

      System.out.println("REF POINT 3: " + v1 + ", " + v2) ;

      observable.setChanged();
      values = Arrays.asList(v1, v2) ;
      observable.notifyObservers(new ListObservedData<>(values));

      System.out.println("Introduce the new reference point 4:");

      s = scanner.nextLine() ;

      v1 = 0.6 ;
      v2 = 0.6 ;

      System.out.println("REF POINT 5: " + v1 + ", " + v2) ;

      observable.setChanged();
      values = Arrays.asList(v1, v2) ;
      observable.notifyObservers(new ListObservedData<>(values));

      /*
      String line = scanner.nextLine();
      System.out.println("LINE: " + line);

      List<Double> values = Arrays.asList(line.split(" "))
              .stream()
              .map(value -> Double.valueOf(value))
              .collect(Collectors.toList());

      System.out.println("VALUES: " + line);
      values
              .stream()
              .forEach(System.out::println);
              */
    }
  }
}
