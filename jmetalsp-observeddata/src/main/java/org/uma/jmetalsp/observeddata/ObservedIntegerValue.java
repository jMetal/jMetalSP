package org.uma.jmetalsp.observeddata;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.uma.jmetalsp.ObservedData;

/**
 * Class implementing a the {@link ObservedData} interface.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ObservedIntegerValue implements ObservedData {
  private Integer value ;

  public ObservedIntegerValue() {
  }

  public ObservedIntegerValue(Integer value) {
    this.value = value ;
  }

  public Integer getValue() {
    return value;
  }

  @Override
  public String toJson() {
    return JsonWriter.objectToJson(this);
  }

  @Override
  public ObservedIntegerValue fromJson(String jsonString) {
    return (ObservedIntegerValue) JsonReader.jsonToJava(jsonString);
  }

  @Override
  public String getPathAVROFile() {
    return null;
  }

  @Override
  public String toString() {
    return value.toString() ;
  }
}

