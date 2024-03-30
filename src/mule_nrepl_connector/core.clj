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
  [muuntaja.core :as m]
  [reitit.interceptor.sieppari :as sieppari]
  [reitit.http.interceptors.parameters :as params]
  [reitit.http.interceptors.muuntaja :as muuntaja]
  [taoensso.timbre :as log]
  [org.httpkit.server :as hs]
  [org.httpkit.client :as http]
  [clojure.reflect :as cr]
  [clojure.pprint ::as pp]
  [clojure.java.shell :refer [sh]]
  [silvur.util :refer [edn->json]])
 
 (:import [org.mule.runtime.extension.api.runtime.operation Result]
          [org.mule.runtime.api.metadata MediaType]
          [org.mule.runtime.extension.api.runtime.source SourceCallbackContext]
          [java.io Serializable]))

(defn get-run-flow
  ([]
   (let [{:keys [callback]} su/*options*]
     (fn [{:keys [body] :as req}]
       (try
         (let [ctx (.createContext callback)
               data (.getBytes body)
               result (.. (Result/builder)
                          (output data)
                          (length (count data))
                          (mediaType MediaType/APPLICATION_JSON)
                          ;;(attributesMediaType MediaType/APPLICATION_JSON)
                          (build))]
           (.handle callback result ctx)
           {:status 200})
         (catch Exception e {:status 200 :body (pr-str (println e))}))))))

(def route ["" {:muuntaja m/instance}
            ["/api" {:post (get-run-flow)}]
            ["/ping" {:get (fn [req] {:status 200 :body "pong"})}]
            ["/request" {:get (fn [req]
                                {:status 200 :body (-> (dissoc req :reitit.core/match)
                                                       (dissoc :reitit.core/router))})}]
            ["/nrepl" {:post su/nrepl-handler}]])

(def app (rh/ring-handler
          (rh/router route)
          (ring/routes
           (ring/create-default-handler))
          {:interceptors [(muuntaja.interceptor/format-interceptor)
                          (muuntaja.interceptor/params-interceptor)]
           :executor sieppari/executor}))




(defn on-start [args]
  (log/info "Hello on-start" (into {} args))
  (su/nrepl-start args))

(defn on-success [& args]
  (log/info "Hello on-success" args))

(defn on-stop []
  (println "Hello on-stop")
  (su/nrepl-stop))
