(ns events-ring-server.database
  (:require [hikari-cp.core :refer [make-datasource]]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def db-spec {:adapter "postgresql"
              :username "joni"
              :password "password312"
              :database-name "my_database"
              :server-name "localhost"
              :port-number 5432})

(defonce datasrc (delay (make-datasource db-spec)))

(defn wrap-execute [f query]
  (with-open [conn (jdbc/get-connection @datasrc)]
    (let [conn-opts (jdbc/with-options conn {:builder-fn rs/as-unqualified-lower-maps
                                             :return-keys true})]
      (f conn-opts query))))

(defn list-events []
  (wrap-execute jdbc/execute! (-> (h/select :*)
                                  (h/from :event)
                                  sql/format)))

(defn find-event-by-id [id]
  (wrap-execute jdbc/execute-one! (-> (h/select :*)
                                      (h/from :event)
                                      (h/where [:= :event_id (parse-uuid id)])
                                      sql/format)))

(defn parse-times [event-m]
  (reduce-kv (fn [m k v]
               (cond (or (= k :dtstamp)
                         (= k :dtstart)
                         (= k :dtend)) (assoc m k (java.time.Instant/parse v))
                     (= k :recur) (assoc m k (org.postgresql.util.PGInterval. (:years v)
                                                                              (:months v)
                                                                              (:days v)
                                                                              (:hours v)
                                                                              (:minutes v)
                                                                              0.0))
                     :else m))
             event-m
             event-m))

(defn insert-event [data]
  (wrap-execute jdbc/execute-one! (-> (h/insert-into :event)
                                      (h/values [(parse-times data)])
                                      sql/format)))

(defn delete-event [id]
  (wrap-execute jdbc/execute-one! (-> (h/delete-from :event)
                                      (h/where [:= :event_id (parse-uuid id)])
                                      sql/format)))

(defn update-event [id data]
  (wrap-execute jdbc/execute-one! (-> (h/update :event)
                                      (h/set (parse-times data))
                                      (h/where [:= :event_id (parse-uuid id)])
                                      sql/format)))
