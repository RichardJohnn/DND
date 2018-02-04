(ns ^:figwheel-always dnd.test-runner
  (:require
    [cljs.test :as test :include-macros true :refer  [report]]
    [dnd.core-test]
    [figwheel.client :as fw]))

(enable-console-print!)

(defn runner []
  (test/run-tests
    'dnd.core-test))

(fw/start  {
            :websocket-url "ws://localhost:4449/figwheel-ws"
            ;; :autoload false
            :build-id "test"
            :on-jsload  (fn [] (runner))})
