(ns events-ring-server.resolvers
  (:import [org.postgresql.util PGInterval]
           [java.sql PreparedStatement]
           [java.time Duration])
  (:require [events-ring-server.database :refer [all-events
                                                 event-by-id
                                                 insert-event]]
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

(defn <-pg-interval
  "Takes a PGInterval instance and converts it into a Duration
   instance. Ignore sub-second units."
  [^org.postgresql.util.PGInterval interval]
  (-> Duration/ZERO
      (.plusSeconds (.getSeconds interval))
      (.plusMinutes (.getMinutes interval))
      (.plusHours (.getHours interval))
      (.plusDays (.getDays interval))))

(extend-protocol rs/ReadableColumn
  ;; Convert PGIntervals back to durations.
  org.postgresql.util.PGInterval
  (read-column-by-label [^org.postgresql.util.PGInterval v _]
    (<-pg-interval v))
  (read-column-by-index [^org.postgresql.util.PGInterval v _2 _3]
    (<-pg-interval v)))

(def resolvers-map {:query/all-events all-events
                    :query/event-by-id event-by-id
                    :mutation/insert-event insert-event})

(def transformers-map
  {:uuid-parser #(parse-uuid %)
   :uuid-serializer #(str %)
   :date-parser #(java.time.Instant/parse %)
   :date-serializer #(eval %)
   :interval-parser #(java.time.Duration/parse %)
   :interval-serializer #(eval %)})
