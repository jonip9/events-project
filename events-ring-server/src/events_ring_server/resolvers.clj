(ns events-ring-server.resolvers
  (:require [events-ring-server.database :refer [ds-opts]]
            [next.jdbc :as jdbc]))

(defn all-events [context args value]
  (jdbc/execute! ds-opts ["SELECT * FROM event"]))

(defn event-by-id [context args value]
  (jdbc/execute-one! ds-opts ["SELECT * FROM event WHERE id = ?" (:id args)]))

(def resolvers-map {:query/all-events all-events
                    :query/event-by-id event-by-id})

(def transformers-map
  {:uuid-parser #(java.util.UUID/fromString %)
   :uuid-serializer #(str %)})
