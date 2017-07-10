(ns dnd.keyboard
  (:require [cljs.nodejs :as nodejs]))

(def emitter
  (let [events (nodejs/require "events") ]
    (new events.EventEmitter)))

(def emit
  (.bind (.-emit emitter) emitter))

(defn keyHandler [level character key matches data]
  (case key
    "UP"    (emit "up"    level character)
    "DOWN"  (emit "down"  level character)
    "LEFT"  (emit "left"  level character)
    "RIGHT" (emit "right" level character)
    "g"     (emit "get"   level character)
    "d"     (emit "drop"  level character)
    "ESCAPE" (.exit js/process)
    "default"))

(defn HandleCharacterKeys [term level character]
  (let [handler (partial keyHandler level character)]
    (.on term "key" handler)))

(defn RemoveHandleCharacterKeys [term]
 (.off term "key" keyHandler))
