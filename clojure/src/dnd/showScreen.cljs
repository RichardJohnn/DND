(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]
            [dnd.level :refer [width height]]
            ))

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
(defn show-screen [term level character]
  (dorun (map #(draw-block term %) (flatten level)))

  (.color256    term 7)
  (.bgColor256  term 0)
  (.moveTo term (+ 2 width) 0 (str "HP: " (:hp character)))
  (.moveTo term 0 (inc height) "got some text down below\n\n")

  )

