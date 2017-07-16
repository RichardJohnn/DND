(ns dnd.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ dnd.level :as level ]
    [ dnd.character :as character]
    [ dnd.showScreen :as show ]
    [ dnd.keyboard :as keyboard ]
    ))

(nodejs/enable-util-print!)

(def term
  (.-terminal (nodejs/require "terminal-kit")))

(defn setup [term]
  (do
    (.applicationKeypad term)
    (.hideCursor term)
    (.grabInput term #js { :mouse "button" :focus true })
    ))

(defn teardown [term]
  (.removeAllListeners term "key")
  (.removeAllListeners keyboard/emitter))

(def character character/character)

(def level
  (let [level (level/make-level)
        [updated] (character/move-character level character 2 1)]
    updated))

(defn move-handler []
  (let [[level character] (character/move-character level character 0 (inc (character :x)))]
    ;(set! level level)
    ;(set! character character)
    (show/show-screen term level)))

(defn -main []
  (.clear term)
  (teardown term)
  (setup term)

  (keyboard/HandleCharacterKeys term level character)
  (.on keyboard/emitter "up" move-handler)
  (show/show-screen term level))

(set! *main-cli-fn* -main)

(-main)
(.bgColor term 0)
