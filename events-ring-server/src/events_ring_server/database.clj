(ns events-ring-server.database
  (:require [hikari-cp.core :refer [make-datasource]]))

(def db-spec {:adapter "postgresql"
              :username "joni"
              :password "password312"
              :database-name "my_database"
              :server-name "localhost"
              :port-number 5432})

(defonce datasrc (delay (make-datasource db-spec)))
