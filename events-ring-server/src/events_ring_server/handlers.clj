(ns events-ring-server.handlers
  (:require [events-ring-server.database :as db]))

(defn events-get [_]
  {:status 200
   :body (db/list-events)})

(defn events-post [{:keys [body-params]}]
  {:status 201
   :body (db/insert-event body-params)})

(defn events-get-by-id [{{:keys [id]} :path-params}]
  {:status 200
   :body (db/find-event-by-id id)})

(defn events-delete-by-id [{{:keys [id]} :path-params}]
  {:status 204
   :body (db/delete-event id)})
