package org.mule.extension.nrepl_connector.internal;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import java.io.InputStream;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Config;
    
import clojure.java.api.Clojure;
import clojure.lang.IFn;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mule.extension.nrepl_connector.internal.Transport;

@Alias("listener")
@EmitsResponse
@MediaType(value = ANY, strict = false)

public class NreplListener extends Source<InputStream, HttpRequestAttributes> {

    private final Logger LOGGER = LoggerFactory.getLogger(NreplConnectionProvider.class);

    @Config
    private NreplConfiguration config;
    
    @DisplayName("Transport")
    @Parameter
    public Transport  protocol = Transport.HTTP;

    public Transport getTransport() {
	return protocol;
    }

    public void onStart(SourceCallback<InputStream, HttpRequestAttributes> sourceCallBack) {
	int port = config.getPort();
	
	LOGGER.info("onStart");
	
	IFn require = Clojure.var("clojure.core", "require");
	require.invoke(Clojure.read("mule-nrepl-connector.core"));
	IFn onStart = Clojure.var("mule-nrepl-connector.core", "on-start");
	IFn keyword = Clojure.var("clojure.core", "keyword");
	HashMap opts = new HashMap<clojure.lang.Keyword, Object>();
	opts.put(keyword.invoke("port"), port);
	if (Transport.CIDER == protocol) {
	    opts.put(keyword.invoke("cider"), true);
	} else if (Transport.TTY == protocol) {
	    opts.put(keyword.invoke("tty"), true);
	    
	}
	opts.put(keyword.invoke("callback"), sourceCallBack);
	onStart.invoke(opts);
    }

    @OnSuccess
    public void onSuccess(@Content String responseBody,
                          @Optional String responseStatusCode,
                          SourceCallbackContext callbackContext) {
	IFn require = Clojure.var("clojure.core", "require");
	require.invoke(Clojure.read("mule-nrepl-connector.core"));
	IFn nrepl = Clojure.var("mule-nrepl-connector.core", "on-success");
	nrepl.invoke(responseBody, responseStatusCode, callbackContext);	
	
    }
    
    public void onStop() {
	LOGGER.info("onStop");
	IFn require = Clojure.var("clojure.core", "require");
	require.invoke(Clojure.read("mule-nrepl-connector.core"));
	IFn nrepl = Clojure.var("mule-nrepl-connector.core", "on-stop");
	nrepl.invoke();

    }

  @OnTerminate
  public void onTerminate() {}

}
