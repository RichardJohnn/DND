(ns dnd.db
  (:require [cljs.nodejs :as nodejs]
            [ promesa.core :as p
             :refer [await then]
             :rename {await <!}
             :refer-macros [alet]]
            [ promesa.async-cljs :refer-macros [async] ]
            [ com.rpl.specter :as s
             :refer [ALL]
             :refer-macros [transform] ]
            ))

(def promisify (.-promisifyAll (nodejs/require "bluebird")))

(def mongo (-> (nodejs/require "mongodb")
               (aget "MongoClient")
               promisify))

(def url "mongodb://localhost/test")

(defn levels []
  (async
    (->
     (await (.connect mongo url))
     (.db "test")
     (.collection "levels"))))

(defn save [level]
  (async
    (->
      (await (levels))
      (.findOneAndUpdate #js {} #js {"$set" #js {:blocks (clj->js @level)}}))))

(defn load []
  (async
    (-> (await (levels))
      (.findOne #js {})
      await
      (js->clj :keywordize-keys true)
      :blocks
      p/promise)))


