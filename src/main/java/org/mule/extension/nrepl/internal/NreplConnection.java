package org.mule.extension.nrepl_connector.internal;


/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class NreplConnection {

  private final String id;

  public NreplConnection(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

    
  public void invalidate() {
    // do something to invalidate this connection!
  }
}
