(ns events-ring-server.database
  (:require [hikari-cp.core :refer [make-datasource]]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [next.jdbc :as jdbc]))

(def db-spec {:adapter "postgresql"
              :username "joni"
              :password "password312"
              :database-name "my_database"
              :server-name "localhost"
              :port-number 5432})

(defonce datasrc (delay (make-datasource db-spec)))

(defn list-events []
  (jdbc/execute! @datasrc (-> (h/select :*)
                              (h/from :event)
                              sql/format)))

(defn find-event-by-id [id]
  (jdbc/execute-one! @datasrc (-> (h/select :*)
                                  (h/from :event)
                                  (h/where [:= :event_id (parse-uuid id)])
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

(defn insert-event [data]
  (jdbc/execute-one! @datasrc (-> (h/insert-into :event)
                                  (h/values [(parse-times data)])
                                  sql/format)))

(defn delete-event [id]
  (jdbc/execute-one! @datasrc (-> (h/delete-from :event)
                                  (h/where [:= :event_id (parse-uuid id)])
                                  sql/format)))

                                  sql/format)))
