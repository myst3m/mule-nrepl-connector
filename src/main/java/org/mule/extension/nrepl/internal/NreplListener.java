package org.mule.extension.nrepl_connector.internal;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import java.io.InputStream;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.connection.ConnectionProvider;


import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Config;
    
import clojure.java.api.Clojure;
import clojure.lang.IFn;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Alias("listener")
@EmitsResponse
@MediaType(value = ANY, strict = false)

public class NreplListener extends Source<InputStream, HttpRequestAttributes> {

    private final Logger LOGGER = LoggerFactory.getLogger(NreplConnectionProvider.class);

    @Config
    private NreplConfiguration config;
    
    public void onStart(SourceCallback<InputStream, HttpRequestAttributes> sourceCallBack) {
	int port = config.getPort();
	
	LOGGER.info("onStart");
	
	IFn require = Clojure.var("clojure.core", "require");
	require.invoke(Clojure.read("silvur.util"));
	IFn nrepl = Clojure.var("silvur.util", "nrepl-start");
	IFn keyword = Clojure.var("clojure.core", "keyword");
	HashMap opts = new HashMap<clojure.lang.Keyword, Integer>();
	opts.put(keyword.invoke("port"), port);
	nrepl.invoke(opts);

    }
    public void onStop() {
    }
}
