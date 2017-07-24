(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]))

(defn draw-block [term block]

  (let [{:keys [x y solid inhabitants color] } block
        has-inhabitant (boolean (seq inhabitants))
        fgcolor (or (:color (first inhabitants)) 0)
        bgcolor (or color (if solid 0 10))
        char    (if has-inhabitant
                  (:char (first inhabitants))
                  (if solid "▒" " "))]
    (.color256    term fgcolor)
    (.bgColor256  term bgcolor)
    (.moveTo      term x y char)))

(defn show-screen [term level]
  (dorun (map #(draw-block term %) (flatten level))))

