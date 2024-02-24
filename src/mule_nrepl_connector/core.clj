(ns mule-nrepl-connector.core
 (:gen-class)
 (:refer-clojure :exclude [error-handler])
 (:require
  [clojure.string :as str]
  [clojure.java.io :as io]
  [clojure.tools.cli :refer (parse-opts)]
  [silvur.datetime :refer (datetime datetime*)]
  [silvur.util :refer (json->edn edn->json)]
  [reitit
   [core :as r]
   [ring :as ring]
   [openapi :as openapi]]
  [reitit.coercion.spec :as spec]
  [reitit.ring.coercion :as coercion]
  [reitit.swagger-ui :as swagger-ui]
  [org.httpkit.server :as server]
  [muuntaja.core :as m]
  [mulify
   [core :as c :refer [defapi]]
   [http :as http]
   [os :as os]
   [vm :as vm]
   [ee :as ee]
   [db :as db]
   [tls :as tls]
   [dataweave :as dw]
   [batch :as batch]
   [apikit :as ak]
   [apikit :as ak-odata]
   [jms :as jms]
   [wsc :as wsc]
   [anypoint-mq :as amq]
   [api-gateway :as gw]
   [edifact :as edi]
   [generic]
   [utils :as utils]
   [file :as f]]))

   (def app (ring/ring-handler
          (ring/router
           [["/openapi.json" {:handler (openapi/create-openapi-handler)
                              :no-doc true}]
            ["/api"
             ["/accounts/{id}"
              {:get {:summary "Get account"
                     :parameters {:path {:id string?}}
                     :responses {200 {:body {:id string?}}}
                     :handler (fn [req] {:status 200})}
               :post {:summary "Create account"
                      :parameters {:body {:type string?}}
                      :responses {201 {:body {:id string?
                                              :type string?}}}
                     :handler (fn [req] {:status 200})}}]]]
           {:data {:coercion reitit.coercion.spec/coercion
                   :muuntaja m/instance
                   :middleware [coercion/coerce-request-middleware]}})
          (ring/routes
           (swagger-ui/create-swagger-ui-handler {:path "/"})
           (ring/create-default-handler))))

;; (app {:request-method :get :uri "/openapi.json"})


(def apikit-config {:port "8081", :id "1", :class 'mulify.apikit})
(def mysql-config {:id "2"
                   :class 'mulify.db
                   :type :mysql :host "localhost" :port "3306" :user "user" :password "password"
                   :database "x1"})

(c/set-global-configs! apikit-config)

(defapi nrepl-connector []
  (ak/+get "/account/(account-id)"
           (c/logger* :INFO "hello")))

(comment (utils/transpile (nrepl-connector) :f "nrepl-connector.xml" :pom? true :a "nrepl-connector"))

;; You need to create src/main/resources/api/nrepl-connector.raml

