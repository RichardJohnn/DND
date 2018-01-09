(ns dnd.db
  (:require [cljs.nodejs :as nodejs]
            [ promesa.core :as p
             :refer [await]
             :rename {await <!}
             :refer-macros [alet]]
            [ promesa.async-cljs :refer-macros [async] ]
            ))

(def promisify (.-promisifyAll (nodejs/require "bluebird")))

(def mongoose (nodejs/require "mongoose"))

(def mongo (-> (nodejs/require "mongodb")
               (aget "MongoClient")
               promisify))

(def url "mongodb://localhost/test")

(.connect mongoose url)
;(.connect mongo url)

(defonce Level
  (.model mongoose "levels"
          #js {:name js/String}))
          ;#js {:strict false}))

(defn save [level]
  (async
    (->
      (await (.connect mongo url))
      (.db "test")
      (.collection "levels")
      (.insert #js {:blocks (clj->js level)}))))

(defn save-level [level]
  (let [level (Level. (clj->js level))
        promise (.save level)]
    (.then promise (fn [& args]
                     (println args)))))

