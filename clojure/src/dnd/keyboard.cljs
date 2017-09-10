(ns dnd.keyboard
  (:require
    [cljs.nodejs :as nodejs]))

(def emitter
  (let [events (nodejs/require "events") ]
    (new events.EventEmitter)))

(def throttle
  (.-throttle (nodejs/require "lodash")))

(def emit
  (.bind (.-emit emitter) emitter))

(defn keyHandler [level character key matches data]
  (case key
    "UP"    (emit "move"  level character  0 -1)
    "DOWN"  (emit "move"  level character  0  1)
    "LEFT"  (emit "move"  level character -1  0)
    "RIGHT" (emit "move"  level character  1  0)
    "g"     (emit "get"   level character)
    "d"     (emit "drop"  level character)
    "ESCAPE" (.exit js/process)
    "default"))

(defn HandleCharacterKeys [term level character]
  (let [handler (partial keyHandler level character)]
    (.on term "key" (throttle handler 100))))

(defn RemoveHandleCharacterKeys [term]
 (.off term "key" keyHandler))
