(ns events-ring-server.core
  (:import [com.zaxxer.hikari HikariDataSource])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers
                                                  attach-scalar-transformers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [events-ring-server.database :refer [db-spec]]
            [events-ring-server.resolvers :refer [all-events
                                                  event-by-id
                                                  insert-event
                                                  delete-event
                                                  resolvers-map
                                                  transformers-map]]
            [muuntaja.core :as m]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :refer [->pool]]
            [next.jdbc.result-set :as rs]
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

(defn app [ds]
  (ring/ring-handler
   (ring/router
    [["/api"
      ["/events" {:get {:handler (fn [_]
                                   {:status 200
                                    :body (all-events ds)})}
                  :post {:handler (fn [{:keys [body-params]}]
                                    {:status 201
                                     :body (insert-event ds body-params)})}}]
      ["/events/:id" {:get {:handler (fn [{{:keys [id]} :path-params}]
                                       {:status 200
                                        :body (event-by-id ds id)})}
                      :delete {:handler (fn [{{:keys [id]} :path-params}]
                                          {:status 204
                                           :body (delete-event ds id)})}}]
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
  (with-open [ds (->pool HikariDataSource db-spec)]
    (let [ds-opts (jdbc/with-options ds {:builder-fn rs/as-unqualified-lower-maps
                                         :return-keys true})]
      (.close (jdbc/get-connection ds-opts))
      (jetty/run-jetty (app ds-opts)
                       {:port 3000
                        :join? true}))))
