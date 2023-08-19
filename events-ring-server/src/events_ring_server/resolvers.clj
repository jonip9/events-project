(ns events-ring-server.resolvers
  (:import [org.postgresql.util PGInterval]
  (:require [events-ring-server.database :refer [ds-opts]]
           [java.sql PreparedStatement]
           [java.time Duration])
            [next.jdbc :as jdbc]
            [next.jdbc.date-time]
            [next.jdbc.prepare :as p]
            [next.jdbc.result-set :as rs]))

(defn ->pg-interval
  "Takes a Duration instance and converts it into a PGInterval
   instance where the interval is created as a number of seconds."
  [^java.time.Duration duration]
  (doto (PGInterval.)
    (.setSeconds (.getSeconds duration))))

(extend-protocol p/SettableParameter
  ;; Convert durations to PGIntervals before inserting into db.
  java.time.Duration
  (set-parameter [^java.time.Duration v
                  ^PreparedStatement s
                  ^long i]
    (.setObject s i (->pg-interval v))))

(defn all-events [context args value]
  (jdbc/execute! ds-opts ["SELECT * FROM event"]))
(defn <-pg-interval
  "Takes a PGInterval instance and converts it into a Duration
   instance. Ignore sub-second units."
  [^org.postgresql.util.PGInterval interval]
  (-> Duration/ZERO
      (.plusSeconds (.getSeconds interval))
      (.plusMinutes (.getMinutes interval))
      (.plusHours (.getHours interval))
      (.plusDays (.getDays interval))))

(defn event-by-id [context args value]
  (jdbc/execute-one! ds-opts ["SELECT * FROM event WHERE event_id = ?" (:id args)]))
(extend-protocol rs/ReadableColumn
  ;; Convert PGIntervals back to durations.
  org.postgresql.util.PGInterval
  (read-column-by-label [^org.postgresql.util.PGInterval v _]
    (<-pg-interval v))
  (read-column-by-index [^org.postgresql.util.PGInterval v _2 _3]
    (<-pg-interval v)))

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
