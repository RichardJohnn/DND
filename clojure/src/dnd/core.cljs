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

(def character (atom character/base-character))

(defonce level
  (let [{:keys [x y]} @character
        level (atom (level/make-level))
        [level] (character/move-character! level character x y)]
    level))

(defn move-handler [level character dx dy]
  (let [{:keys [x y]} @character
        new-x (+ x dx)
        new-y (+ y dy)
        [_level _character] (character/move-character! level character new-x new-y)]
    (show/show-screen term @level)))

(defn get-handler [level character]
  (let [[_level _character] (character/get-item! level character)]
    (show/show-screen term @level)))

(defn drop-handler [level character]
  (let [lastItem (last (:inventory @character))]
    (character/drop-item! level character lastItem)
    (show/show-screen term @level)))

(defn -main []
  (.clear term)
  (teardown term)
  (setup term)

  (keyboard/HandleCharacterKeys term level character)
  (.on keyboard/emitter "move" move-handler)
  (.on keyboard/emitter "get" get-handler)
  (.on keyboard/emitter "drop" drop-handler)
  (show/show-screen term @level))

(set! *main-cli-fn* -main)

(-main)
(show/show-screen term @level)
(.bgColor term 0)
