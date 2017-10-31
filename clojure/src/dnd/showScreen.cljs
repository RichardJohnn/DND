(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]
            [dnd.level :refer [width height]]
            [dnd.color :refer [COLOR color-to-vec]]
            ))

(defonce terminal (nodejs/require "terminal-kit"))

(def bresenham (nodejs/require "bresenham-js"))

(defn end-points [character]
  (let [{:keys [x y]} character
        _width  (inc width)
        _height (inc height)
        zw (range 0 _width)
        zh (range 0 _height)
        ziy (range 0 (inc y))
        zix (range 0 (inc x))
        ++yh (range (+ 2 y) height)
        ++xw (range (+ 2 x) width)
        opposite-wall (vec (case (:direction character)
           "n" (map #(vec [% 0])      zw)
           "s" (map #(vec [% height]) zw)
           "w" (map #(vec [0 %])      zh)
           "e" (map #(vec [width %])  zh)))
        adjenct-wall (vec (case (:direction character)
           "n" (map #(vec [0 %])      ziy)
           "s" (map #(vec [0 %])      ++yh)
           "w" (map #(vec [% 0])      zix)
           "e" (map #(vec [% 0])      ++xw)))
        adjenct-wall2 (vec (case (:direction character)
           "n" (map #(vec [width %])  ziy)
           "s" (map #(vec [width %])  ++yh)
           "w" (map #(vec [% height]) zix)
           "e" (map #(vec [% height]) ++xw)))]
    (concat opposite-wall adjenct-wall adjenct-wall2)
    ))

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
  (if (apply not= (map last [shortened-line line]))
    (concat shortened-line (list (nth line (count shortened-line))))
    line))

(defn put-block [buffer block]
  (let [{:keys [x y solid inhabitants color visible]} block
        has-inhabitant (boolean (seq inhabitants))
        fgcolor (or (:color (first inhabitants)) [0 0 0])
        [r g b] fgcolor
        bgcolor (color-to-vec (.rgb COLOR color))
        [bgR bgG bgB] bgcolor
        char    (if-not visible
                  " "
                  (if has-inhabitant
                    (:char (first inhabitants))
                    (if solid "▒" " ")))]
    (.put buffer
          #js {:x x :y y
               :attr #js {:r r :g g :b b :bgR bgR :bgG bgG :bgB bgB}}
          char)))


(defn draw-block [term block]
  (let [{:keys [x y solid inhabitants color visible]} block
        has-inhabitant (boolean (seq inhabitants))
        fgcolor (or (:color (first inhabitants)) [0 0 0])
        bgcolor (.rgb COLOR color)
        ;night true
        ;bgcolor (if night (.darken bgcolor .9) bgcolor)
        bgcolor (color-to-vec bgcolor)
        char    (if-not visible
                  " "
                  (if has-inhabitant
                   (:char (first inhabitants))
                   (if solid "▒" " ")))]
    (apply (.-colorRgb term) fgcolor)
    (apply (.-bgColorRgb term) bgcolor)
    (.moveTo        term x y char)))

(defn fov [character]
  (let [{:keys [x y direction view-distance]} character
        [x y] (map inc [x y])
        ends (end-points character)
        lines (map #(let [dx (first %)
                          dy (second %)
                          start #js[x y]
                          end   #js[dx dy]]
                      (take view-distance (js->clj (bresenham start end))))
                   ends)]
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
  (defonce buffer ((.. terminal -ScreenBufferHD -create)
                   #js {
                        :dst term
                        :x 0
                        :y 0
                        :width width
                        :height height
                        }))

  (->> level
       (viewable-coords character)
       (dimmed-blocks level)
       (run! #(put-block buffer %)))

  (.draw buffer #js {:delta true})

  (.color256    term 7)
  (.bgColor256  term 0)
  (.moveTo term (+ 2 width) 1 (str "HP: " (:hp character)))
  (.moveTo term (+ 2 width) 2 (str "INV: " (count (:inventory character))))
  (.moveTo term 0 (inc height) "got some text down below\n\n")
  (.moveTo term 0 (+ 10 height))

  )

(defn inventory-selected [error response]
  (if error
    (println "oh crap")
    (println "you stare at" (.-selectedText response))
    ))

(defn show-inventory [term character]
  (let [descriptions (->> character :inventory (map :colorful-description))]
    (when-not (empty? descriptions)
      (.gridMenu term (clj->js descriptions) inventory-selected))
    ))

