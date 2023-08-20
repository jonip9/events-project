(ns events-ring-server.database
  (:require [hikari-cp.core :refer [make-datasource]]
            [honey.sql :as sql]
            [honey.sql.helpers :refer [select from where insert-into delete-from values]]
            [next.jdbc :as jdbc]))

(def db-spec {:adapter "postgresql"
              :username "joni"
              :password "password312"
              :database-name "my_database"
              :server-name "localhost"
              :port-number 5432})

(defonce datasrc (delay (make-datasource db-spec)))

(defn all-events []
  (jdbc/execute! @datasrc (-> (select :*)
                              (from :event)
                              sql/format)))

(defn event-by-id [id]
  (jdbc/execute-one! @datasrc (-> (select :*)
                                  (from :event)
                                  (where [:= :event_id (parse-uuid id)])
                                  sql/format)))

(defn parse-times [event-m]
  (reduce-kv (fn [m k v]
               (cond (or (= k :dtstamp)
                         (= k :dtstart)
                         (= k :dtend)) (assoc m k (java.time.Instant/parse v))
                     (= k :duration) (assoc m k (java.time.Duration/parse v))
                     :else m))
             event-m
             event-m))

(defn insert-event [args]
  (jdbc/execute-one! @datasrc
                     (-> (insert-into :event)
                         (values [(parse-times args)])
                         sql/format)))

(defn delete-event [id]
  (jdbc/execute-one! @datasrc (-> (delete-from :event)
                                  (where [:= :event_id (parse-uuid id)])
                                  sql/format)))
