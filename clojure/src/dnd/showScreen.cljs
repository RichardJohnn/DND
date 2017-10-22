(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]
            [dnd.level :refer [width height]]
            [dnd.color :refer [COLOR color-to-vec]]
            ))

(def bresenham (nodejs/require "bresenham-js"))

(defn end-points [character]
  (let [_width  (inc width)
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

(defn shorten-line [level line]
  (def shortened-line (take-while #(let [[x y] %
                     x (min (dec width)  (max 0 (dec x)))
                     y (min (dec height) (max 0 (dec y)))
                     current-block (get-in level [x y])]
                 (not (current-block :solid))
                 ) line))
  (if (apply not= (map count [shortened-line line]))
    (concat shortened-line (list (nth line (count shortened-line))))
    line))

(defn draw-block [term block]
  (let [{:keys [x y solid inhabitants color]} block
        has-inhabitant (boolean (seq inhabitants))
        fgcolor (or (:color (first inhabitants)) 0)
        bgcolor (.rgb COLOR color)
        ;night true
        ;bgcolor (if night (.darken bgcolor .9) bgcolor)
        bgcolor (color-to-vec bgcolor)
        char    (if has-inhabitant
                  (:char (first inhabitants))
                  (if solid "▒" " "))]
    (.color256      term fgcolor)
    (apply (.-bgColorRgb term) bgcolor)
    (.moveTo        term x y char)))

(defn fov [character]
  (let [{:keys [x y direction]} character
        [x y] (map inc [x y])
        ends (end-points character)
        lines (map #(let [dx (first %)
                          dy (second %)
                          start #js[x y]
                          end   #js[dx dy]]
                      (js->clj (bresenham start end))) ends)]
    lines))

(defn viewable-coords [character level]
  (let [fov-blocks (fov character)
        viewable-coords (map (partial shorten-line level) fov-blocks)]
    (apply concat viewable-coords)))

(defn dimmed-blocks [level coords]
  (let [flattened-level (flatten level)
        block-not-in-coords (fn [block]
                              (let [{x :x y :y} block]
                                (not-any? #(= [x y] %) coords)
                                ))
        dim-block #(assoc % :visible false :color #js [0 0 0])
        lit-block #(assoc % :visible true)
        ]
    (map #(if (block-not-in-coords %) (dim-block %) (lit-block %)) flattened-level)
    ))

(defn show-screen [term level character]
  (defn callback [error] (when error (println error)))

  (def new-blocks
    (->> level
        (viewable-coords character)
        (dimmed-blocks level)
        (run! #(draw-block term %))
        ))

  (.color256    term 7)
  (.bgColor256  term 0)
  (.moveTo term (+ 2 width) 1 (str "HP: " (:hp character)))
  (.moveTo term (+ 2 width) 2 (str "INV: " (count (:inventory character))))
  (.moveTo term 0 (inc height) "got some text down below\n\n")
  (.moveTo term 0 (+ 2 height))
  (.moveTo term 0 (+ 15 height))

  )

