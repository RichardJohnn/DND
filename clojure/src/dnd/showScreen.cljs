(ns dnd.showScreen
  (:require [cljs.nodejs :as nodejs]
            [dnd.level :refer [width height]]
            [dnd.util :refer [has-some]]
            [dnd.color :refer [COLOR color-to-vec]]
            ))

(def window-width 50)
(def window-height 25)

(defonce terminal (nodejs/require "terminal-kit"))
(defonce bresenham (nodejs/require "bresenham-js"))

(defn window-limits [character]
  (let [{:keys [x y]} character
        x-lo (-> x (/ window-width) js/Math.floor (* window-width))
        x-hi (+ x-lo window-width)
        y-lo (-> y (/ window-height) js/Math.floor (* window-height))
        y-hi (+ y-lo window-height)]
    [x-lo x-hi y-lo y-hi]))

(defn end-points [character]
  (let [{:keys [x y direction]} character
        [x-lo x-hi y-lo y-hi] (window-limits character)
        zw (range x-lo x-hi)
        zh (range y-lo y-hi)
        opposite-wall (vec (case direction
           "n" (map #(vec [% y-lo]) zw)
           "s" (map #(vec [% y-hi]) zw)
           "w" (map #(vec [x-lo %]) zh)
           "e" (map #(vec [x-hi %]) zh)))
        ;_ (println opposite-wall)
        ;ziy (range 0 y)
        ;zix (range 0 x)
        ;++yh (range (inc y) window-height)
        ;++xw (range (inc x) window-width)
        ;adjenct-wall (vec (case direction
           ;"n" (map #(vec [0 %])      ziy)
           ;"s" (map #(vec [0 %])      ++yh)
           ;"w" (map #(vec [% 0])      zix)
           ;"e" (map #(vec [% 0])      ++xw)))
        ;adjenct-wall2 (vec (case direction
           ;"n" (map #(vec [window-width %])  ziy)
           ;"s" (map #(vec [window-width %])  ++yh)
           ;"w" (map #(vec [% window-height]) zix)
           ;"e" (map #(vec [% window-height]) ++xw)))
        ]
    (concat opposite-wall
            ;adjenct-wall
            ;adjenct-wall2
            )))

(defn draw-x [term coords]
  (.color256    term 7)
  (.bgColor256  term 0)
  (let [[x y] coords]
     (.moveTo term x y "X")))

(defn draw-line [term line]
  (run!
    (partial draw-x term) line))

(defn put-block [character buffer block]
  (let [{:keys [x y inhabitants color visible]} block
        x (rem x window-width)
        y (rem y window-height)
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
  (let [{:keys [x y inhabitants color visible]} block
        has-inhabitant (boolean (seq inhabitants))
        [fgR fgG fgB] (or (:color (first inhabitants)) [0 0 0])
        bgcolor (.rgb COLOR color)
        [bgR bgG bgB] (color-to-vec bgcolor)
        char    (if has-inhabitant
                  (:char (first inhabitants))
                  " ")]
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

(defn shorten-line [level line]
  (def shortened-line (take-while #(let [[x y] %
                                         current-block (get-in level [x y])
                                         solid  (has-some current-block :solid)]
                                     (not solid))
                                  line))
  (if (apply not= (map last [shortened-line line]))
    (concat shortened-line (list (nth line (count shortened-line))))
    line))

(defn viewable-coords [character level]
  (->> (fov character)
       (map (partial shorten-line level))
       (apply concat)))

(defn show-map-direct [term level character]
  (let [get-in-level (partial get-in level)]
    (.clear term)
    (->> level
         (viewable-coords character)
         (map get-in-level)
         (run! #(draw-block term %)))))

(defn prnr [term args]
  (.moveTo term 0 (inc window-width))
  (prn args)
  args)

(defn show-map-with-buffer [term level character]
  (let [buffer ((.. terminal -ScreenBufferHD -create)
                #js {:dst    term
                     :width  window-width
                     :height window-height
                     })
        get-in-level (partial get-in level)]

    (->> level
         (viewable-coords character)
         ;(prnr term)
         (map get-in-level)
         (run! #(put-block character buffer %))
         )

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
    (.moveTo (+ 2 window-width) 4 (str "x: " (:x character) " y: " (:y character)))
    (.moveTo 0 (+ 10 window-height))))

;(.moveTo term 0 0
;(.drawImage term "" #js {:shrink #js {:width 100 :height 150}}))


(defn inventory-selected [term character error response]
  (assoc character :can-move true)
  (.moveTo term 0 (inc window-height)
           (if error
             "oh crap!"
             (str "you stare at " (.-selectedText response))))
  character)

(defn show-inventory [term level character]
  (let [descriptions (->> character :inventory (map :colorful-description))
        inventory-selected (partial inventory-selected term character)]
    (when-not (empty? descriptions)
      (assoc character :can-move false)
      (.gridMenu term (clj->js descriptions) inventory-selected))
    [level character]))

