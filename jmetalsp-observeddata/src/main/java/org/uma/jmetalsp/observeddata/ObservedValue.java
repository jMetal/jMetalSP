package org.uma.jmetalsp.observeddata;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.uma.jmetalsp.ObservedData;

/**
 * Class implementing a the {@link ObservedData} interface.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ObservedValue<T> implements ObservedData<T> {
  private T value;
  private String pathAVROFile;
  public ObservedValue() {
  }

  public ObservedValue(T value) {
    this.value = value;
  }
  public ObservedValue(T value, String pathAVROFile)
  {
    this.value = value;
    this.pathAVROFile = pathAVROFile;
  }

  public T getValue() {
    return value;
  }

  @Override
  public String toJson() {
    return JsonWriter.objectToJson(this);
  }

  @Override
  public ObservedValue<T> fromJson(String jsonString) {
    return (ObservedValue<T>) JsonReader.jsonToJava(jsonString);
  }



  @Override
  public T getData() {
    return this.value;
  }
}

