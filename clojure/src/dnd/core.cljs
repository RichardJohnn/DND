(ns dnd.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ dnd.level :as level ]
    [ dnd.character ]
    [ dnd.show-screen :as show ]
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
  (.removeAllListeners term "key"))

(defn -main []
  (def character dnd.character/character)
  (.clear term)
  (teardown term)
  (setup term)

  (def level
    (let [level (level/make-level)
          firstOne (level 1)
          updateHabs #(assoc firstOne :inhabitants [character])
          updated (update level 1 updateHabs)
          ]
      updated))

  (keyboard/HandleCharacterKeys term level character)
  (.on keyboard/emitter "up" #(println "UP!!!!!!!!!!!"))
  (show/show-screen term level))

(set! *main-cli-fn* -main)

(-main)
(.bgColor term 0)
