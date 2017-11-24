(defproject dnd "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [com.rpl/specter "1.0.3"]
                 ]

  ;:jvm-opts ["-Dclojure.server.repl={:port 5555 :accept clojure.core.server/repl}"]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.13"]]

  :source-paths ["src"]

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   }}


  :clean-targets ["server.js"
                  "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel true
              :compiler {
                :main dnd.core
                :output-to "target/server_dev/dnd.js"
                :output-dir "target/server_dev"
                :target :nodejs
                :asset-path "target/server_dev"
                :optimizations :none
                :source-map true}}
             {:id "prod"
              :source-paths ["src"]
              :compiler {
                :output-to "server.js"
                :output-dir "target/server_prod"
                :target :nodejs
                :optimizations :simple}}]})
