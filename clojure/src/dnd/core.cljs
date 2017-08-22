(ns dnd.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ cljs.core.async :refer [chan close! <! timeout] ]
    [ dnd.level :as level ]
    [ dnd.character :as character]
    [ dnd.showScreen :as show ]
    [ dnd.keyboard :as keyboard ]
    )
  (:require-macros
    [cljs.core.async.macros :as m :refer [go-loop]]))

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

(defn popper [queue]
  (go-loop [q queue]
    (if (not (empty? @queue))
      (let [next-action (peek @queue)]
        ;(println next-action)
        (apply move-handler next-action)
        (swap! queue pop))
      nil)

    (<! (timeout 100))
    (recur queue))
  )

(defn -main []
  (.clear term)
  (teardown term)
  (setup term)

  (def queue (atom #queue []))
  (defn pusher! [action & arguments] (swap! queue conj arguments))

  (keyboard/HandleCharacterKeys term level character)
  (.on keyboard/emitter "move" (partial pusher! "move"))
  (.on keyboard/emitter "get"  (partial pusher! "get"))
  (.on keyboard/emitter "drop" (partial pusher! "drop"))
  (show/show-screen term @level)
  (popper queue)
  )


(set! *main-cli-fn* -main)

(-main)
(show/show-screen term @level)
(.bgColor term 0)
