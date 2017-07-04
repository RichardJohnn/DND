(ns dnd.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ dnd.level :as level ]
    [ dnd.show-screen :as show ]
    [ dnd.keyboard :as keyboard ]
    ))

(nodejs/enable-util-print!)

(def term
  (.-terminal (nodejs/require "terminal-kit")))

(defn setup []
  (do
    (.applicationKeypad term)
    (.hideCursor term)
    (.grabInput term #js { :mouse 'button' :focus true })))

(defn teardown [])


(defn -main []
  (.clear term)
  (teardown)
  (setup)
  (def level
    (let [level (level/make-level)
          firstOne (level 1)
          updateHabs #(assoc firstOne :inhabitants ["shit"])
          updated (update level 1 updateHabs)
          ]
      updated
      ))
  (show/show-screen term level)
)

(set! *main-cli-fn* -main)

(-main)
(.bgColor term 0)
