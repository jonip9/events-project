(ns events-ring-server.resolvers
  (:require [events-ring-server.database :refer [all-events
                                                 event-by-id
                                                 insert-event]]))

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
