(ns dnd.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ cljs.core.async :refer [chan close! <! timeout] ]
    [ dnd.level ]
    [ dnd.character :as character]
    [ dnd.showScreen :as show ]
    [ dnd.keyboard :as keyboard ]
    )
  (:require-macros
    [cljs.core.async.macros :as m :refer [go-loop]]))

(nodejs/enable-util-print!)

(defonce tkit (nodejs/require "terminal-kit"))
(defonce net (nodejs/require "net"))

(declare term)

(defn setup [term]
  (do
    (.applicationKeypad term)
    (.hideCursor term)
    (.grabInput term #js { :mouse "button" :focus true })
    ))

(defn teardown [term]
  (.removeAllListeners term "key")
  (.removeAllListeners keyboard/emitter))

(defonce character (atom
                     (assoc character/base-character
                            :isPlayer true
                            :description "hero"
                            )))

(defonce level
  (let [{:keys [x y]} @character
        level (atom (dnd.level/make-level))
        [level] (character/move-character! level character x y)]
    level))

(defn show-screen [] (show/show-screen term @level @character))

(defn show-inventory [] (show/show-inventory term @character))

(defn move-handler [level character dx dy]
  (let [{:keys [x y]} @character
        new-x (+ x dx)
        new-y (+ y dy)
        _character (character/redirect-character! character dx dy)
        [_level _character] (character/move-character! level character new-x new-y)]
    (show-screen)))

(defn get-handler [level character]
  (let [[_level _character] (character/get-item! level character)]
    (show-screen)))

(defn drop-handler [level character]
  (let [lastItem (last (:inventory @character))]
    (when-not (nil? lastItem)
      (character/drop-item! level character lastItem)
      (show-screen))))

(defn popper! [queue]
  (go-loop [q queue]
    (if (not (empty? @queue))
      (let [next-action (peek @queue)
            action-name (first next-action)
            args        (rest next-action)
            function    (case action-name
                          "move" move-handler
                          "get"  get-handler
                          "drop" drop-handler
                          "inventory" show-inventory
                          )]

        (apply function args)
        (swap! queue pop))
      nil)

    (<! (timeout 100))
    (recur queue))
  )


(defn herp [term]
  (.clear term)
  (.grabInput term true)
  (.red term "YEAH")
  ;; (.on term "key"  #((let [string (str "key was " %)]
  ;;                    (println string)
  ;;                    (.send client string)
  ;;                    )))
  ;; (.on term "data" #((let [string (str "data was " %)]
  ;;                    (println string)
  ;;                    (.send client string)
  ;;                    )))
  (teardown term)
  (setup term)

  (def queue (atom #queue []))
  (defn pusher! [& arguments] (swap! queue conj arguments))

  (keyboard/HandleCharacterKeys term level character)
  (.on keyboard/emitter "move" (partial pusher! "move"))
  (.on keyboard/emitter "get"  (partial pusher! "get"))
  (.on keyboard/emitter "drop" (partial pusher! "drop"))
  (.on keyboard/emitter "inventory" (partial pusher! "inventory"))

  (prn "first show screen")
  (show-screen)
  (popper! queue))

(defn telnet-server [client]
  (prn "telnet-server")

  (def term
    (.createTerminal tkit #js {:stdin client :stdout client}))

  (herp term))

(defn -main []
  (prn "main")
  (if (-> (last process.argv)
          (= "server"))
    (doto (.createServer net telnet-server)
      (.listen 23))
    (doto (def term (.-terminal tkit))
      (herp)
      ))
  )

(set! *main-cli-fn* -main)

;; (-main)


(show-screen)
