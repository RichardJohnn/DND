(ns dnd.character.random-name
  (:require [ cljs.nodejs :as nodejs ]
            [ promesa.core :as p
             :refer [await]
             :rename {await <!}
             :refer-macros [alet]]
            ))

(nodejs/require "coffeescript/register")

;(def promisify (.-promisify (nodejs/require "bluebird")))
(def name-generator
  (nodejs/require "../../../lib/character/name-generator.coffee"))

(defn generate-name []
  (alet [samples #js ["Hags" "Chune" "Vibe"]
         length 1
         [[result]] (<! (name-generator samples length))]
        (aget result "name")))

