(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]
            [dnd.level :refer [width height]]
            ))

(def COLOR
  (nodejs/require "color"))

(defn color-to-vec [kolor]
  (-> kolor (.rgb) (.array) (js->clj)))

(defn draw-block [term block]
  (let [{:keys [x y solid inhabitants color] } block
        has-inhabitant (boolean (seq inhabitants))
        night true
        fgcolor (or (:color (first inhabitants)) 0)
        bgcolor (or color (if solid
                            (COLOR "white")
                            (COLOR "pink")
                            ))
        bgcolor (if night (.darken bgcolor .9) bgcolor)
        bgcolor (color-to-vec bgcolor)
        char    (if has-inhabitant
                  (:char (first inhabitants))
                  (if solid "▒" " "))]
    (.color256      term fgcolor)
    (apply (.-bgColorRgb term) bgcolor)
    (.moveTo        term x y char)))

(defn show-screen [term level character]
  (dorun (map #(draw-block term %) (flatten level)))

  (.color256    term 7)
  (.bgColor256  term 0)
  (.moveTo term (+ 2 width) 0 (str "HP: " (:hp character)))
  (.moveTo term 0 (inc height) "got some text down below\n\n")

  )

