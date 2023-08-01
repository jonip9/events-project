(ns events-ring-server.resolvers
  (:import [org.postgresql.util PGInterval]
           [java.sql PreparedStatement])
  (:require [events-ring-server.database :refer [ds-opts]]
            [next.jdbc :as jdbc]
            [next.jdbc.date-time]
            [next.jdbc.prepare :as p]))

(defn ->pg-interval
  [^java.time.Duration duration]
  (doto (PGInterval.)
    (.setValue (.toString duration))))

(extend-protocol p/SettableParameter
  java.time.Duration
  (set-parameter [^java.time.Duration v
                  ^PreparedStatement s
                  ^long i]
    (.setObject s i (->pg-interval v))))

(defn all-events [context args value]
  (jdbc/execute! ds-opts ["SELECT * FROM event"]))

(defn event-by-id [context args value]
  (jdbc/execute-one! ds-opts ["SELECT * FROM event WHERE event_id = ?" (:id args)]))

(defn insert-event [context args value]
  (let [{summary :summary
         descr :descr
         dtstamp :dtstamp
         dtstart :dtstart
         dtend :dtend
         duration :duration} args]
    (jdbc/execute-one! ds-opts
                       ["INSERT INTO event (summary, descr, dtstamp, dtstart, dtend, duration) VALUES (?, ?, ?, ?, ?, ?)"
                        summary descr dtstamp dtstart dtend duration])))

(def resolvers-map {:query/all-events all-events
                    :query/event-by-id event-by-id
                    :mutation/insert-event insert-event})

(def transformers-map
  {:uuid-parser #(clojure.core/parse-uuid %)
   :uuid-serializer #(str %)
   :date-parser #(java.time.LocalDateTime/parse %)
   :date-serializer #(str %)
   :interval-parser #(java.time.Duration/parse %)
   :interval-serializer #(str %)})
