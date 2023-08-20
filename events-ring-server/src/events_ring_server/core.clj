(ns events-ring-server.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers
                                                  attach-scalar-transformers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [events-ring-server.extensions]
            [events-ring-server.handlers :refer [events-get
                                                 events-post
                                                 events-get-by-id
                                                 events-delete-by-id]]
            [events-ring-server.resolvers :refer [resolvers-map
                                                  transformers-map]]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as cm]
            [reitit.middleware :as middleware]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.malli]
            [reitit.ring.middleware.dev :as dev]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.adapter.jetty :as jetty]))

(def my-schema
  (-> "my-schema.edn"
      io/resource
      slurp
      edn/read-string
      (attach-scalar-transformers transformers-map)
      (attach-resolvers resolvers-map)
      schema/compile))

(def app
  (ring/ring-handler
   (ring/router
    [["/api"
      ["/events" {:get {:handler events-get}
                  :post {:handler events-post}}]
      ["/events/:id" {:get {:handler events-get-by-id}
                      :delete {:handler events-delete-by-id}}]
      ["/graphql" {:post {:parameters {:body [:map
                                              [:query string?]]}
                          :handler (fn [{{{:keys [query]} :body} :parameters}]
                                     {:status 200
                                      :body (execute my-schema query nil nil)})}}]]]
    {:data {:coercion cm/coercion
            :muuntaja m/instance
            :middleware [parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-response-middleware
                         coercion/coerce-request-middleware]}
     ::middleware/transform dev/print-request-diffs})
   (ring/create-default-handler)))

(defn -main [& args]
  (jetty/run-jetty app
                   {:port 3000
                    :join? true}))
