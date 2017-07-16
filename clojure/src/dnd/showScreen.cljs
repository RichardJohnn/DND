(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]))

(defn draw-block [term block]
  (let [{:keys [x y solid inhabitants] } block
        hasPlayer (boolean (seq inhabitants))
        color   (if hasPlayer 0 3)
        bgcolor (if hasPlayer 10 1)
        char    (if hasPlayer "@" ".")]
    (.color256    term color)
    (.bgColor256  term bgcolor)
    (.moveTo      term x y char)))

(defn doall* [s] (dorun (tree-seq seq? seq s)) s)

(defn show-screen [term level]
  (doall* (map (fn [row]
                (map #(draw-block term %) row))
              level)))

