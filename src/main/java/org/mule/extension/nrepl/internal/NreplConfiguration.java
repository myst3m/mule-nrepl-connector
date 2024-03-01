package org.mule.extension.nrepl_connector.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(NreplOperations.class)
@ConnectionProviders(NreplConnectionProvider.class)
@Sources({NreplListener.class})

public class NreplConfiguration {


    @Parameter
    private String configId;

    @DisplayName("Listen Port")
    @Parameter
    @Optional(defaultValue = "8081")
    private int port;
    
    public String getConfigId(){
	return configId;
    }

    public int getPort() {
	return port;
    }

}
