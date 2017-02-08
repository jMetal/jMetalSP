//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package org.uma.jmetalsp.problem.tsp;

import org.uma.jmetalsp.updatedata.repository.DoubleMatrixUpdateData;

import java.io.Serializable;

/**
 * Created by ajnebro on 19/4/16.
 */
public class MultiobjectiveTSPUpdateData implements DoubleMatrixUpdateData, Serializable {
  private int matrixID ;
  private int x ;
  private int y ;
  private double value ;

  public MultiobjectiveTSPUpdateData(int matrixID, int x, int y, double value) {
    this.matrixID = matrixID ;
    this.x = x ;
    this.y = y ;
    this.value = value ;
  }

  public int getMatrixID() {
    return matrixID;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public double getValue() {
    return value;
  }
}
