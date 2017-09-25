(ns dnd.color
  (:require [cljs.nodejs :as nodejs]))

(def COLOR
  (nodejs/require "color"))

(defn color-to-array [kolor]
  (-> kolor (.rgb) (.array)))

(defn color-to-vec [kolor]
  (-> kolor (color-to-array) (js->clj)))
