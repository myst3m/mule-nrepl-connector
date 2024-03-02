(ns mule-nrepl-connector.core
 (:require
  [clojure.string :as str]
  [clojure.java.io :as io]
  [clojure.tools.cli :refer (parse-opts)]
  [silvur.datetime :refer (datetime datetime*)]
  [silvur.util :as su]
  [reitit
   [core :as r]
   [ring :as ring]
   [openapi :as openapi]]
  [reitit.coercion.spec :as spec]
  [reitit.ring.coercion :as coercion]
  [reitit.http :as rh]
  [reitit.ring :as ring]
  [reitit.swagger-ui :as swagger-ui]
  [org.httpkit.server :as server]
  [muuntaja.core :as m]
  [reitit.interceptor.sieppari :as sieppari]
  [reitit.http.interceptors.parameters :as params]
  [reitit.http.interceptors.muuntaja :as muuntaja]
  [taoensso.timbre :as log]
  [org.httpkit.server :as hs]))

(def route ["" {:muuntaja m/instance}
            ["/ping" {:get (fn [req] {:status 200 :body "pong"})}]
            ["/nrepl" {:post su/nrepl-handler}]])

(def app (rh/ring-handler
          (rh/router route)
          (ring/routes
           (ring/create-default-handler))
          {:interceptors [(muuntaja.interceptor/format-interceptor)
                          (muuntaja.interceptor/params-interceptor)]
           :executor sieppari/executor}))


(defn on-start [args]
  (log/debug "Hello on-start" (into {} args))
  (su/nrepl-start args))

(defn on-success [& args]
  (log/debug "Hello on-success" (into {} args)))

(defn on-stop []
  (println "Hello on-stop"))
