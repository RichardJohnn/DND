(ns dnd.color
  (:require [cljs.nodejs :as nodejs]))

(def COLOR
  (nodejs/require "color"))

(def name-this-color
  (nodejs/require "name-this-color"))

(defn rgb-vec->str [rgb]
  (apply str
         (interleave
           ["rgb(" ,,, "," ,,, "," ,,, ")"]
           (conj rgb nil))))

(defn color-name [color]
  (-> color
      rgb-vec->str
      name-this-color
      (aget 0)
      .-title))

(defn color-to-array [kolor]
  (-> kolor (.rgb) (.array)))

(defn color-to-vec [kolor]
  (-> kolor (color-to-array) (js->clj)))
