(ns dnd.keyboard
  (:require [cljs.nodejs :as js]))

(defn print-ret [& args]
  (do
    (println args)
    args))

(def events (js/require "events"))
(def emitter (new events.EventEmitter))
(def emit (.-emit emitter))

(defn keyHandler [key matches data]
  (case key
    "UP"    (emit "up" )
    "DOWN"  (emit "down")
    "LEFT"  (emit "left")
    "RIGHT" (emit "right")
    "g"     (emit "get")
    "d"     (emit "drop")
    "ESCAPE" (.exit js/process)
    "default"))

(defn HandleCharacterKeys [term character]
  (.on term "key" keyHandler))

(defn RemoveHandleCharacterKeys [term]
 (.off term "key" keyHandler))
