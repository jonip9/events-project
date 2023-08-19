(ns events-ring-server.database
  (:require [hikari-cp.core :refer [make-datasource]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ds (make-datasource {:adapter "postgresql"
                          :username "joni"
                          :password "password312"
                          :database-name "my_database"
                          :server-name "localhost"
                          :port-number 5432}))

(def ds-opts (jdbc/with-options ds {:builder-fn rs/as-unqualified-lower-maps}))
(def db-spec {:dbtype "postgres"
              :dbname "my_database"
              :username "joni"
              :password "password312"})
