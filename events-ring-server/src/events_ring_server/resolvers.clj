(ns events-ring-server.resolvers
  (:require [events-ring-server.database :as db]))

(def resolvers-map {:query/all-events db/list-events
                    :query/event-by-id db/find-event-by-id
                    :mutation/insert-event db/insert-event})

(def transformers-map
  {:uuid-parser #(parse-uuid %)
   :uuid-serializer #(str %)
   :date-parser #(java.time.Instant/parse %)
   :date-serializer #(eval %)
   :interval-parser #(java.time.Duration/parse %)
   :interval-serializer #(eval %)})
