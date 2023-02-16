(ns events-ring-server.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [events-ring-server.resolvers :refer [resolvers-map]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [api-defaults
                                              wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body
                                          wrap-json-response]]
            [ring.util.response :refer [content-type
                                        response]]))

(def my-schema
  (-> "my-schema.edn"
      io/resource
      slurp
      edn/read-string
      (attach-resolvers resolvers-map)
      schema/compile))

(defroutes my-routes
  (GET "/" [:as request]
       (response {:foo (str request)}))
  (POST "/graphql" [:as request]
        (response (let [query (get-in request [:body :query])]
                    (execute my-schema query nil nil))))
  (context "/events" [:as request]
           (GET "/" []
                (response {:events "TEMP"}))
           (context "/:id" [id]
                    (let-routes [event {}]
                      (GET "/" []
                           (response {:result event}))))))

(def app
  (-> my-routes
      wrap-json-response
      (wrap-json-body {:keywords? true})
      (wrap-defaults api-defaults)))

(defn -main [& args]
  (jetty/run-jetty app
                   {:port 3000
                    :join? true}))
