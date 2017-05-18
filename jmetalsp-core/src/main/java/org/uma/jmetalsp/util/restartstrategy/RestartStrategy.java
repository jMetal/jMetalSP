package org.uma.jmetalsp.util.restartstrategy;

/**
 * Interface describing restart strategies to apply when changes (in the problem or the reference point) have been
 * detected
 *
 * @author Antonio J. Nebro
 */
public interface RestartStrategy {
  void restart() ;
}
