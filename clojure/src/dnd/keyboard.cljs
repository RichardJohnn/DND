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

(defn keyHandler [term level character key matches data]
  (case key
    "UP"    (emit "move"  term level character  0 -1)
    "DOWN"  (emit "move"  term level character  0  1)
    "LEFT"  (emit "move"  term level character -1  0)
    "RIGHT" (emit "move"  term level character  1  0)
    "g"     (emit "get"   term level character)
    "d"     (emit "drop"  term level character)
    "a"     (emit "attack" term level character)
    "i"     (emit "inventory" term level character)
    "ESCAPE" (.exit js/process)
    "default"))

(defn HandleCharacterKeys [term level character]
  (let [handler (partial keyHandler term level character)]
    (.on term "key" (throttle handler 100))))

(defn RemoveHandleCharacterKeys [term]
 (.off term "key" keyHandler))
