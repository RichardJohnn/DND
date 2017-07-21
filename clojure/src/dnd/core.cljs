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

(def level
  (let [character character/character
        {:keys [x y]} character
        level (level/make-level)
        [updated] (character/move-character level character x y)]
    updated))

(defn move-handler [level character dx dy]
  (let [{:keys [x y]} character
        new-x (+ x dx)
        new-y (+ y dy)
        [level character] (character/move-character level character new-x new-y)]
    (show/show-screen term level)))

(defn -main []
  (.clear term)
  (teardown term)
  (setup term)

  (keyboard/HandleCharacterKeys term level character/character)
  (.on keyboard/emitter "move" move-handler)
  (show/show-screen term level))

(set! *main-cli-fn* -main)

(-main)
(.bgColor term 0)
