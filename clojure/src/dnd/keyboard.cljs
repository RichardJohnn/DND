(ns dnd.keyboard
  (:require [cljs.nodejs :as nodejs]))

(def events (nodejs/require "events"))
(def emitter (new events.EventEmitter))
(defn emit [& args] (.emit emitter args))

(defn keyHandler [level character key matches data]
  (println key)
  (case key
    "UP"    (emit "up" level character)
    "DOWN"  (emit "down" level character)
    "LEFT"  (emit "left" level character)
    "RIGHT" (emit "right" level character)
    "g"     (emit "get" level character)
    "d"     (emit "drop" level character)
    "ESCAPE" (.exit js/process)
    "default"))

(defn HandleCharacterKeys [term level character]
  (let [handler (partial keyHandler level character)]
    (.on term "key" handler)))

(defn RemoveHandleCharacterKeys [term]
 (.off term "key" keyHandler))
