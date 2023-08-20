(ns events-ring-server.handlers
  (:require [events-ring-server.database :refer [all-events
                                                 event-by-id
                                                 insert-event
                                                 delete-event]]))

(defn events-get [_]
  {:status 200
   :body (all-events)})

(defn events-post [{:keys [body-params]}]
  {:status 201
   :body (insert-event body-params)})

(defn events-get-by-id [{{:keys [id]} :path-params}]
  {:status 200
   :body (event-by-id id)})

(defn events-delete-by-id [{{:keys [id]} :path-params}]
  {:status 204
   :body (delete-event id)})
