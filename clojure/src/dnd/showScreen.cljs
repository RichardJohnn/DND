(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]
            [dnd.level :refer [width height]]
            [dnd.color :refer [COLOR color-to-vec]]
            ))

(def bresenham (nodejs/require "bresenham-js"))

(defn end-points [character]
  (let [_width (inc width)
        _height (inc height)]
    (vec (case (:direction character)
           "n" (map #(vec [% 0])      (range 0 _width))
           "s" (map #(vec [% height]) (range 0 _width))
           "w" (map #(vec [0 %])      (range 0 _height))
           "e" (map #(vec [width %])  (range 0 _height))))))

(defn draw-x [term coords]
  (.color256    term 7)
  (.bgColor256  term 0)
  (let [[x y] coords]
     (.moveTo term x y "X")))

(defn draw-line [term line]
  (run!
    (partial draw-x term) line))

(defn fov [term level character]
  (let [{:keys [x y direction]} character
        ends (end-points character)
        lines (map #(let [dx (first %)
                          dy (second %)
                          start #js[(inc x) (inc y)]
                          end   #js[dx dy]]
                      (js->clj (bresenham start end))) ends)]
    (run!
      (partial draw-line term) lines)))

(defn draw-block [term block]
  (let [{:keys [x y solid inhabitants color] } block
        has-inhabitant (boolean (seq inhabitants))
        night true
        fgcolor (or (:color (first inhabitants)) 0)
        bgcolor (.rgb COLOR color)
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

  (fov term level character)

  (.color256    term 7)
  (.bgColor256  term 0)
  (.moveTo term (+ 2 width) 1 (str "HP: " (:hp character)))
  (.moveTo term (+ 2 width) 2 (str "INV: " (count (:inventory character))))
  (.moveTo term 0 (inc height) "got some text down below\n\n")

  )

