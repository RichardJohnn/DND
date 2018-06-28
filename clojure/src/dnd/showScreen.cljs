(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]
            [dnd.level :refer [width height]]
            [dnd.color :refer [COLOR color-to-vec]]
            ))

(def window-width width)
(def window-height height)

(defonce terminal (nodejs/require "terminal-kit"))

(defonce bresenham (nodejs/require "bresenham-js"))

(defn end-points [character]
  (let [{:keys [x y]} character
        _width  (inc window-width)
        _height (inc window-height)
        zw (range 0 _width)
        zh (range 0 _height)
        ziy (range 0 y)
        zix (range 0 x)
        ++yh (range (inc y) window-height)
        ++xw (range (inc x) window-width)
        opposite-wall (vec (case (:direction character)
           "n" (map #(vec [% 0])      zw)
           "s" (map #(vec [% window-height]) zw)
           "w" (map #(vec [0 %])      zh)
           "e" (map #(vec [window-width %])  zh)))
        adjenct-wall (vec (case (:direction character)
           "n" (map #(vec [0 %])      ziy)
           "s" (map #(vec [0 %])      ++yh)
           "w" (map #(vec [% 0])      zix)
           "e" (map #(vec [% 0])      ++xw)))
        adjenct-wall2 (vec (case (:direction character)
           "n" (map #(vec [window-width %])  ziy)
           "s" (map #(vec [window-width %])  ++yh)
           "w" (map #(vec [% window-height]) zix)
           "e" (map #(vec [% window-height]) ++xw)))]
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
                     x (min (dec window-width)  (max 0 x))
                     y (min (dec window-height) (max 0 y))
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
        char (if-not visible
               " "
               (if has-inhabitant
                 (:char (first inhabitants))
                 " ")
               )]
    (.put buffer
          #js {:x x :y y
               :attr #js {:r r :g g :b b :bgR bgR :bgG bgG :bgB bgB}}
          char)))

(defn draw-block [term block]
  (let [{:keys [x y solid inhabitants color visible]} block
        has-inhabitant (boolean (seq inhabitants))
        [fgR fgG fgB] (or (:color (first inhabitants)) [0 0 0])
        bgcolor (.rgb COLOR color)
        [bgR bgG bgB] (color-to-vec bgcolor)
        char    (if has-inhabitant
                  (:char (first inhabitants))
                  (if solid "▒" " "))]
    (.colorRgb term fgR fgG fgB)
    (.bgColorRgb term bgR bgG bgB)
    (.moveTo term x y char)))

(defn fov [character]
  (let [{:keys [x y direction view-distance]} character
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


(defn show-map-direct [term level character]
  (let [get-in-level (partial get-in level)]
    (.clear term)
    (->> level
         (viewable-coords character)
         (map get-in-level)
         (run! #(draw-block term %)))))

(defn show-map-with-buffer [term level character]
  (let [
        buffer ((.. terminal -ScreenBufferHD -create)
                #js {:dst    term
                     :width  window-width
                     :height window-height
                     })
        get-in-level (partial get-in level)]

    (->> level
         (viewable-coords character)
         (map get-in-level)
         (run! #(put-block buffer %)))

    (.draw buffer #js {:delta true})))


(defn show-screen [term level character]

  (if (-> (.-support term) (aget "trueColor"))
    (show-map-with-buffer term level character)
    (show-map-direct term level character))

  (doto term
    (.color256    7)
    (.bgColor256  0)
    (.moveTo (+ 2 window-width) 1 (str "HP: " (:hp character)))
    (.moveTo (+ 2 window-width) 2 (str "INV: " (count (:inventory character))))
    (.moveTo (+ 2 window-width) 3 (str "trueColor: " (.. term -support -trueColor)))
    ;(.moveTo 0 (inc height) "got some text down below\n\n")
    (.moveTo 0 (+ 10 window-height))))

;(.moveTo term 0 0
;(.drawImage term "" #js {:shrink #js {:width 100 :height 150}}))


(defn inventory-selected [term character error response]
  (assoc character :can-move true)
  (.moveTo term 0 (inc window-height)
           (if error
             "oh crap!"
             (str "you stare at " (.-selectedText response)))
           )
  character)

(defn show-inventory [term level character]
  (let [descriptions (->> character :inventory (map :colorful-description))
        inventory-selected (partial inventory-selected term character)]
    (when-not (empty? descriptions)
      (assoc character :can-move false)
      (.gridMenu term (clj->js descriptions) inventory-selected))
    [level character]))

