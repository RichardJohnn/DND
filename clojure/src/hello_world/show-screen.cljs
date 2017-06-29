(ns hello-world.show-screen
  (:require [cljs.nodejs :as nodejs]))

(defonce term
  (.-terminal (nodejs/require "terminal-kit")))

(.grabInput term #js { :mouse 'button' :focus true })

(def block {
            :x 0
            :y 0
            :inhabitants []
            :solid false
            })

(def dudeX  (atom 40))
(def dudeY  (atom 20))

(def width  60)
(def height 30)

(defn keyHandler [key matches data]
  (case key
    "UP"    (swap! dudeY dec)
    "DOWN"  (swap! dudeY inc)
    "LEFT"  (swap! dudeX dec)
    "RIGHT" (swap! dudeX inc)
    "ESCAPE" (.exit js/process)
    "meh")
  (.moveTo      term @dudeX @dudeY "@"))

(defn teardown []
  (.off term "key" keyHandler))

(defn setup []
  (.on term "key" keyHandler))

(defn make-block [x y]
  (let [solid (> (rand) 0.9)]
    (assoc block :x x :y y :solid solid)))

(def level
  (for [x (range width)
        y (range height)]
    (make-block x y)))

(defonce setup-term
  (do
    (.applicationKeypad term)
    (.hideCursor term)))

(defn draw-block [block]
  (let [{x :x
         y :y
         solid :solid } block
        color   (if solid 5 3)
        bgcolor (if solid 100 220)
        char    (if solid "." " ")]
    (.color256    term color)
    (.bgColor256  term bgcolor)
    (.moveTo      term x y char)))

(defn show-screen []
  (.clear term)
  (doall (map #(draw-block %) level)))

(defn demo []
  (teardown)
  (setup)
  (show-screen)

  (.color256    term 0)
  (.moveTo      term @dudeX @dudeY "@")

  (.color256    term 2)
  (.bgColor256  term 0)
  (.moveTo      term width 25))

(demo)